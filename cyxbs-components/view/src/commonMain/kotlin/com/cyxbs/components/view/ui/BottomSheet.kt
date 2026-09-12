package com.cyxbs.components.view.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.cyxbs.components.config.compose.theme.LocalAppColors
import com.cyxbs.components.utils.compose.backHandler
import com.cyxbs.components.utils.compose.clickableNoIndicator
import com.cyxbs.components.utils.compose.derivedStateOfStructure
import com.cyxbs.components.utils.compose.plusDsl
import com.cyxbs.components.utils.compose.rememberDerivedStateOfStructure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 在底部显示的抽屉组件
 *
 * @author 985892345
 * 2024/4/15 20:43
 */

@Stable
class BottomSheetState(
  var onDismissRequest: suspend BottomSheetState.() -> Unit = { collapseSuspend() },
  val hideable: Boolean = false,
  /**
   * 拖拽吸附到关闭位置时是否统一通过 [onDismissRequest]。
   *
   * 默认开启，使返回键、点击遮罩和下拉关闭共享同一套业务拦截。依赖 [hideable] 区分“收起”与
   * “完全隐藏”的调用方需要显式关闭，否则 [onDismissRequest] 无法获知本次拖拽的目标状态。
   */
  val requestDismissOnDrag: Boolean = true,
) {

  internal val showHeight = mutableFloatStateOf(0F)
  internal val showMaxHeight = mutableFloatStateOf(0F)

  /**
   * 折叠高度计算器，对外可读取包含父级剩余导航栏后的最终高度。
   *
   * 高度配置仍由 [BottomSheetCompose] 统一更新，调用方不应直接绕过组件修改计算结果。
   */
  val peekHeightUpdater = BottomSheetPeekHeightUpdater(this)

  val stateFlow: StateFlow<BottomSheetValueState> get() = stateFlowInternal
  private val stateFlowInternal = MutableStateFlow(BottomSheetValueState.Collapsed)

  /**
   * 命令通道：承载 expand/collapse/hide 的「一次性触发」语义。
   */
  internal val commandFlow = MutableStateFlow<BottomSheetValueState?>(null)

  var state: BottomSheetValueState by mutableStateOf(stateFlowInternal.value)
    private set

  // 用户是否可滚动
  val userScrollEnabled = mutableStateOf(true)

  // 小于 0 时表示处于 hide 状态
  val fraction by derivedStateOfStructure {
    val showHeight = showHeight.floatValue
    val showMaxHeight = showMaxHeight.floatValue
    val peekHeight = peekHeightUpdater.peekHeightPx
    if (showMaxHeight == 0F) 0F else (showHeight - peekHeight) / (showMaxHeight - peekHeight)
  }

  internal val scrollableState = ScrollableState {
    val max = showMaxHeight.floatValue
    val now = showHeight.floatValue
    val new = (now - it).coerceIn(0F, max)
    showHeight.floatValue = new
    now - new
  }

  internal val bottomSheetSpring = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = 1F  // 当距离目标 < 1px 时认为到达，如果不设置，会导致动画持续较久
  )

  /**
   * 异步触发 expand
   */
  fun expandAsync() {
    // 发命令给 collector（由 BottomSheetCompose 安装），不阻塞调用方
    commandFlow.value = BottomSheetValueState.Expanded
  }

  /**
   * 挂起直到 expand 完成或者被后续操作取消，若不需要等待则使用 [expandAsync]
   *
   * ⚠️外界需要考虑 CancellationException
   *
   * @throws kotlinx.coroutines.CancellationException 被另一次折叠或隐藏操作取消时抛出
   */
  suspend fun expandSuspend() {
    if (state == BottomSheetValueState.Expanded) return
    // 若直接以 0 作为动画目标，状态会被错误标记为 Expanded，但弹窗仍停留在屏幕外；
    // 因此需要等待有效高度后再展开，同时保留协程的可取消性。
    // Android Dialog 当前通常会在展开协程前完成首轮布局测量，因此不会暴露该问题；
    // iOS CMP Dialog 可能先执行展开、再触发 onSizeChanged，此时内容高度仍为 0，所以需要等待布局测量完成
    if (showMaxHeight.floatValue <= 0F) {
      snapshotFlow { showMaxHeight.floatValue }.first { it > 0F }
    }
    val now = showHeight.floatValue
    val target = showMaxHeight.floatValue
    if (now != target) {
      setState(BottomSheetValueState.Scrolling)
    }
    // now 即使等于 target 也需要执行 animateScrollBy，将其他正在进行中的协程给取消掉
    scrollableState.animateScrollBy(
      value = now - target,
      animationSpec = bottomSheetSpring,
    )
    setState(BottomSheetValueState.Expanded)
  }

  // 异步触发 collapse
  fun collapseAsync() {
    commandFlow.value = BottomSheetValueState.Collapsed
  }

  /**
   * 挂起直到 collapse 完成或者被后续操作取消，若不需要等待则使用 [collapseAsync]
   *
   * ⚠️外界需要考虑 CancellationException
   *
   * @throws kotlinx.coroutines.CancellationException 被另一次展开或隐藏操作取消时抛出
   */
  suspend fun collapseSuspend() {
    if (state == BottomSheetValueState.Collapsed) return
    val now = showHeight.floatValue
    val target = peekHeightUpdater.peekHeightPx
    if (now != target) {
      setState(BottomSheetValueState.Scrolling)
    }
    scrollableState.animateScrollBy(
      value = now - target,
      animationSpec = bottomSheetSpring,
    )
    setState(BottomSheetValueState.Collapsed)
  }

  // 异步触发 hide
  fun hideAsync() {
    commandFlow.value = BottomSheetValueState.Hide
  }

  /**
   * 挂起直到 hide 完成或者被后续操作取消，若不需要等待则使用 [hideAsync]
   *
   * ⚠️外界需要考虑 CancellationException
   *
   * @throws kotlinx.coroutines.CancellationException 被另一次展开或折叠操作取消时抛出
   */
  suspend fun hideSuspend() {
    if (state == BottomSheetValueState.Hide) return
    val now = showHeight.floatValue
    val target = 0F
    // hide 不触发 Scrolling 状态
    scrollableState.animateScrollBy(
      value = now - target,
      animationSpec = bottomSheetSpring,
    )
    setState(BottomSheetValueState.Hide)
  }

  internal fun setState(value: BottomSheetValueState) {
    stateFlowInternal.tryEmit(value)
    state = value
  }

  /**
   * 在有效折叠高度改变时同步当前显示高度。
   *
   * 折叠态需要同时响应增大与减小；其他可见状态只做下限保护，避免安全区增大后内容落入系统栏。
   */
  internal fun onPeekHeightChanged(value: Float) {
    when (stateFlow.value) {
      BottomSheetValueState.Collapsed -> showHeight.floatValue = value
      BottomSheetValueState.Hide -> Unit
      else -> if (showHeight.floatValue < value) {
        showHeight.floatValue = value
      }
    }
  }
}

enum class BottomSheetValueState {
  Hide, Collapsed, Scrolling, Expanded
}

@Composable
fun rememberBottomSheetState(
  onDismissRequest: suspend BottomSheetState.() -> Unit = { collapseSuspend() }
): BottomSheetState {
  return remember { BottomSheetState(onDismissRequest) }.also {
    it.onDismissRequest = onDismissRequest
  }
}

/**
 * 显示可拖拽的底部抽屉。
 *
 * @param bottomSheetState 抽屉状态，可用于展开、折叠和隐藏。
 * @param modifier 应用于抽屉外层容器；其中位于本组件之前的 Insets 消费会参与剩余高度计算。
 * @param peekHeight 业务折叠高度，不需要手动包含 navBar 高度。
 * @param navigationBarContent 父级剩余 navBar 区域的占位内容，会在折叠和展开状态时进行 navBar 的占位。
 * 默认使用 `LocalAppColors.topBg`，传入 {} 可保留高度但保持透明，传 null 则关闭导航栏适配，由调用方自行兼容。
 * @param navigationBarPaddingInContent 是否对展开的内容应用 `navigationBarsPadding()`，
 * 默认在 [navigationBarContent] 非 null 时开启；业务已自行处理时传 false。
 */
@Composable
fun BottomSheetCompose(
  bottomSheetState: BottomSheetState = rememberBottomSheetState(),
  modifier: Modifier = Modifier,
  peekHeight: Dp = 0.dp,
  navigationBarContent: (@Composable BoxScope.() -> Unit)? = { DefaultBottomSheetNavigationBarContent() },
  navigationBarPaddingInContent: Boolean = navigationBarContent != null,
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = false,
  scrimColor: Color = Color.Transparent.copy(alpha = 0.6F),
  content: @Composable BottomSheetScope.() -> Unit
) {
  val density = LocalDensity.current
  val navigationBarBottomPx = WindowInsets.navigationBars.getBottom(density)
  val peekHeightUpdater = bottomSheetState.peekHeightUpdater
  val navigationBarEnabled = navigationBarContent != null
  BottomSheetBackgroundCompose(
    // 回调位于调用方 modifier 之后，可以拿到祖先和调用方已经消费的 Insets 总量。
    modifier = modifier.onConsumedWindowInsetsChanged { consumedInsets ->
      peekHeightUpdater.updateConsumedInsets(consumedInsets)
    },
    scrimColor = scrimColor,
    bottomSheetState = bottomSheetState,
    dismissOnBackPress = dismissOnBackPress,
    dismissOnClickOutside = dismissOnClickOutside,
  ) {
    BottomSheetContent(
      modifier = Modifier.align(Alignment.BottomCenter),
      bottomSheetState = bottomSheetState,
      navigationBarContent = navigationBarContent,
      navigationBarPaddingInContent = navigationBarPaddingInContent,
      content = content
    )
  }
  DisposableEffect(
    peekHeightUpdater,
    peekHeight,
    density,
    navigationBarBottomPx,
    navigationBarEnabled,
  ) {
    peekHeightUpdater.updateConfiguration(
      basePeekHeightPx = with(density) { peekHeight.toPx() },
      navigationBarBottomPx = navigationBarBottomPx,
      includeNavigationBar = navigationBarEnabled,
      density = density,
    )
    onDispose { }
  }
  LaunchedEffect(bottomSheetState) {
    bottomSheetState.commandFlow.collectLatest { command ->
      try {
        when (command) {
          BottomSheetValueState.Expanded -> bottomSheetState.expandSuspend()
          BottomSheetValueState.Collapsed -> bottomSheetState.collapseSuspend()
          BottomSheetValueState.Hide -> bottomSheetState.hideSuspend()
          else -> Unit
        }
        bottomSheetState.commandFlow.value = null
      } catch (_: CancellationException) {
        // 被新命令的 animateScrollBy 取消（例如展开动画中触发了折叠）
      }
    }
  }
}

/**
 * 汇总业务 peekHeight、系统导航栏和父级 Insets 消费量，并维护最终有效折叠高度。
 *
 * Insets 消费回调发生在布局阶段，因此这里直接保存最近配置并同步计算结果，避免调用方再维护
 * 一套高度状态。配置或消费量任一变化时都会重新计算，旋转屏幕和动态安全区也能生效。
 * 调用方可通过 [BottomSheetState.peekHeightUpdater] 取得本对象，并读取 [peekHeightPx]；更新入口
 * 仅供 [BottomSheetCompose] 使用，以保证业务高度和 Insets 始终经过同一条计算链。
 */
@Stable
class BottomSheetPeekHeightUpdater internal constructor(
  private val bottomSheetState: BottomSheetState,
) {
  /** 当前最终有效折叠高度，单位为 px；包含配置允许时父级尚未消费的导航栏底部高度。 */
  var peekHeightPx by mutableFloatStateOf(0F)
    private set

  /** 业务传入的原始折叠高度，单位为 px，不包含导航栏。 */
  var basePeekHeightPx by mutableFloatStateOf(0F)
    private set

  /** 父级尚未消费的导航栏底部高度，单位为 px；即使业务 peekHeight 为 0 也会保留用于绘制占位。 */
  var remainingNavigationBarHeightPx by mutableFloatStateOf(0F)
    private set

  private var navigationBarBottomPx = 0
  private var includeNavigationBar = true
  private var density: Density? = null
  private var consumedInsets: WindowInsets? = null

  /** 更新组合阶段可得的业务高度与系统配置。 */
  internal fun updateConfiguration(
    basePeekHeightPx: Float,
    navigationBarBottomPx: Int,
    includeNavigationBar: Boolean,
    density: Density,
  ) {
    this.basePeekHeightPx = basePeekHeightPx
    this.navigationBarBottomPx = navigationBarBottomPx
    this.includeNavigationBar = includeNavigationBar
    this.density = density
    updatePeekHeight()
  }

  /** 更新当前节点之前已经消费的 Insets；其底部值会从导航栏总高度中扣除。 */
  internal fun updateConsumedInsets(consumedInsets: WindowInsets) {
    this.consumedInsets = consumedInsets
    updatePeekHeight()
  }

  /**
   * 计算导航栏剩余高度与最终折叠高度。
   *
   * 导航栏占位在 peekHeight 为 0 的模态 Sheet 中也需要绘制，但不能因此改变其完全隐藏的目标；
   * 所以剩余高度始终计算，仅在业务传入正数 peekHeight 时才加入最终折叠高度。
   */
  private fun updatePeekHeight() {
    val density = density ?: return
    val remainingNavigationBarPx = if (includeNavigationBar) {
      val consumedBottomPx = consumedInsets?.getBottom(density) ?: 0
      (navigationBarBottomPx - consumedBottomPx).coerceAtLeast(0)
    } else {
      0
    }
    remainingNavigationBarHeightPx = remainingNavigationBarPx.toFloat()
    val navigationBarPeekHeightPx = if (basePeekHeightPx > 0F) {
      remainingNavigationBarHeightPx
    } else {
      0F
    }
    val value = basePeekHeightPx + navigationBarPeekHeightPx
    if (peekHeightPx != value) {
      peekHeightPx = value
      bottomSheetState.onPeekHeightChanged(value)
    }
  }
}

/** 默认导航栏占位，跟随主题使用与课表、校车和地图 Sheet 一致的顶部背景色。 */
@Composable
internal fun DefaultBottomSheetNavigationBarContent() {
  Spacer(
    modifier = Modifier
      .fillMaxSize()
      .background(LocalAppColors.current.topBg)
  )
}

@Composable
private fun BottomSheetBackgroundCompose(
  modifier: Modifier,
  bottomSheetState: BottomSheetState,
  scrimColor: Color,
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = false,
  content: @Composable BoxScope.() -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val focusRequester = remember { FocusRequester() }
  LaunchedEffect(Unit) { focusRequester.requestFocus() }
  Box(
    modifier = modifier
      .fillMaxSize()
      .focusRequester(focusRequester)
      .focusable()
      .plusDsl {
        if (dismissOnBackPress) {
          val enable by rememberDerivedStateOfStructure {
            // 因为 onPostFling 执行的动画比较缓慢，就会导致短时间内不是 Expanded
            // 最好的方式就是判断当前展开是否比较多，大于一定区间后都拦截返回键
            bottomSheetState.fraction > 0.5F
          }
          backHandler(enabled = enable) {
            coroutineScope.launch {
              bottomSheetState.onDismissRequest.invoke(bottomSheetState)
            }
          }
        }
        if (dismissOnClickOutside) {
          clickableNoIndicator { // 这里给背景设置点击事件默认会拦截后面的 XML 布局，所以只有需要时才设置
            coroutineScope.launch {
              bottomSheetState.onDismissRequest.invoke(bottomSheetState)
            }
          }
        }
      }
  ) {
    if (scrimColor != Color.Transparent) {
      Spacer(
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer {
            alpha = bottomSheetState.fraction
          }
          .background(scrimColor)
      )
    }
    content()
  }
}

private class BottomSheetSnapLayoutInfoProvider(
  private val bottomSheetState: BottomSheetState,
) : SnapLayoutInfoProvider {
  override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
    if (velocity == 0F) return 0F
    // 返回衰减动画应该需要执行的偏移量，decayOffset 是根据衰减动画计算出来可以执行的最大偏移量
    val min = if (bottomSheetState.hideable) 0F else bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val now = bottomSheetState.showHeight.floatValue
    val new = now - decayOffset
    if (new < min) return now - min
    if (new > max) return now - max
    return 0F
  }

  override fun calculateSnapOffset(velocity: Float): Float {
    // 衰减动画执行完 calculateApproachOffset 返回的偏移后，开启新动画需要偏移的量
    // 如果衰减动画的起始速度为 0，则就相当于松手后执行动画回到起点或终点
    val min = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val now = bottomSheetState.showHeight.floatValue
    if (bottomSheetState.hideable && now <= min) {
      if (now == 0F) return 0F
      return if (now <= min / 2F) now else now - min
    }
    if (now == min || now == max) return 0F
    val boundary = (min + max) / 2F
    return if (now <= boundary) now - min else now - max
  }
}

@Composable
private fun BottomSheetContent(
  modifier: Modifier,
  bottomSheetState: BottomSheetState,
  navigationBarContent: (@Composable BoxScope.() -> Unit)?,
  navigationBarPaddingInContent: Boolean,
  content: @Composable BottomSheetScope.() -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val decayAnimationSpec = rememberSplineBasedDecay<Float>()
  // 参考 PagerDefaults#flingBehavior
  val flingBehavior = remember(bottomSheetState) {
    snapFlingBehavior(
      snapLayoutInfoProvider = BottomSheetSnapLayoutInfoProvider(bottomSheetState),
      decayAnimationSpec = decayAnimationSpec,
      snapAnimationSpec = bottomSheetState.bottomSheetSpring,
    )
  }
  val scope = remember(bottomSheetState) {
    BottomSheetScopeImpl(
      coroutineScope = coroutineScope,
      bottomSheetState = bottomSheetState,
      flingBehavior = flingBehavior,
    )
  }
  Box(
    modifier = modifier.fillMaxWidth()
      .onSizeChanged {
        if (bottomSheetState.showMaxHeight.floatValue != it.height.toFloat()) {
          bottomSheetState.showMaxHeight.floatValue = it.height.toFloat()
          if (bottomSheetState.stateFlow.value == BottomSheetValueState.Collapsed) {
            bottomSheetState.showHeight.floatValue = bottomSheetState.peekHeightUpdater.peekHeightPx
          } else if (bottomSheetState.stateFlow.value == BottomSheetValueState.Expanded) {
            bottomSheetState.showHeight.floatValue = it.height.toFloat()
          } else if (bottomSheetState.stateFlow.value == BottomSheetValueState.Hide) {
            bottomSheetState.showHeight.floatValue = 0F
          }
        }
      }.graphicsLayer {
        translationY = size.height - bottomSheetState.showHeight.floatValue
      }
      .clickableNoIndicator { /*防止点击穿透*/ }
      .nestedScroll(remember(bottomSheetState) {
        BottomSheetNestedScrollConnection(
          bottomSheetState = bottomSheetState,
          flingBehavior = flingBehavior,
        )
      })
  ) {
    val contentWrapperModifier = if (navigationBarPaddingInContent) {
      Modifier.navigationBarsPadding()
    } else {
      Modifier
    }
    Box(modifier = contentWrapperModifier) {
      content(scope)
    }
    if (navigationBarContent != null) {
      with(scope) {
        NavigationBarContent(navigationBarContent)
      }
    }
  }
}

@Stable
interface BottomSheetScope {
  // 这里直接使用 Modifier.bottomSheetDraggable(): Modifier 会出问题，使用处会经历两次 layout，原因未知
  @Stable
  fun bottomSheetDraggable(): Modifier
}

/**
 * 用于把 [BottomSheetScope] 下传到 [BottomSheetCompose] content 之外的地方。
 *
 * 当 BottomSheet 的内容由其他不携带 [BottomSheetScope] receiver 的 @Composable（如
 * navigation3 的 NavEntry.Content()）渲染时，可通过本 CompositionLocal 获取 scope 来调用
 * [BottomSheetScope.bottomSheetDraggable]。
 *
 * 仅在 [BottomSheetCompose] 内部被提供，外部使用前需先 provide。
 */
val LocalBottomSheetScope = staticCompositionLocalOf<BottomSheetScope> {
  error("LocalBottomSheetScope 未被提供，请确认处于 BottomSheetCompose 的 content 作用域内")
}

private class BottomSheetScopeImpl(
  private val coroutineScope: CoroutineScope,
  private val bottomSheetState: BottomSheetState,
  private val flingBehavior: TargetedFlingBehavior,
) : BottomSheetScope {
  override fun bottomSheetDraggable(): Modifier = Modifier.composed {
    draggable(
      enabled = bottomSheetState.userScrollEnabled.value,
      orientation = Orientation.Vertical,
      state = rememberDraggableState {
        val min = bottomSheetState.peekHeightUpdater.peekHeightPx
        val max = bottomSheetState.showMaxHeight.floatValue
        val now = bottomSheetState.showHeight.floatValue
        val new = (now - it).coerceIn(if (bottomSheetState.hideable) 0f else min, max)
        bottomSheetState.scrollableState.dispatchRawDelta(now - new)
      },
      onDragStarted = {
        bottomSheetState.scrollableState.scroll(scrollPriority = MutatePriority.UserInput) {
          // 这里会打断惯性滑动
          bottomSheetState.setState(BottomSheetValueState.Scrolling)
        }
      },
      onDragStopped = { velocity ->
        coroutineScope.launch {
          var targetState = BottomSheetValueState.Expanded
          bottomSheetState.scrollableState.scroll {
            with(flingBehavior) {
              performFling(velocity)
            }
            targetState = if (bottomSheetState.fraction == 0F) {
              BottomSheetValueState.Collapsed
            } else if (bottomSheetState.fraction < 0F) {
              BottomSheetValueState.Hide
            } else {
              BottomSheetValueState.Expanded
            }
          }
          if (targetState != BottomSheetValueState.Expanded && bottomSheetState.requestDismissOnDrag) {
            // 离开 scroll mutation 后再请求关闭，回弹/收起动画才能安全重新取得 ScrollableState。
            bottomSheetState.onDismissRequest.invoke(bottomSheetState)
          } else {
            bottomSheetState.setState(targetState)
          }
        }
      }
    )
  }

  /**
   * 在尚未消费的导航栏区域上叠加业务内容。
   *
   * peekHeight 为 0 时，占位直接跟随整个 Sheet 的显示和隐藏动画；peekHeight 大于 0 时，占位在
   * 展开到折叠区间固定覆盖屏幕底部，从折叠继续进入 Hide 区间后再锁定于业务 peekHeight 下方，
   * 随整个 Sheet 一起下移。默认铺满 BottomSheet；地图横屏等特殊宽度由业务通过传入的 [content]
   * 自行约束。
   */
  @Composable
  fun BoxScope.NavigationBarContent(content: @Composable BoxScope.() -> Unit) {
    val density = LocalDensity.current
    val remainingHeightPx = bottomSheetState.peekHeightUpdater.remainingNavigationBarHeightPx
    if (remainingHeightPx <= 0F) return
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset {
          val showHeightPx = bottomSheetState.showHeight.floatValue
          val showMaxHeightPx = bottomSheetState.showMaxHeight.floatValue
          val basePeekHeightPx = bottomSheetState.peekHeightUpdater.basePeekHeightPx
          val offsetYPx = if (basePeekHeightPx <= 0F) {
            // 模态 Sheet 没有常驻折叠区，导航栏占位直接留在内容底部并随整个 Sheet 下移。
            0F
          } else {
            // 折叠以上抵消 Sheet 位移并固定在屏幕底部；折叠以下固定到业务 peek 底部一起下移。
            val topPx = maxOf(basePeekHeightPx, showHeightPx - remainingHeightPx)
            val bottomAlignedTopPx = showMaxHeightPx - remainingHeightPx
            topPx - bottomAlignedTopPx
          }
          IntOffset(
            x = 0,
            y = offsetYPx.roundToInt(),
          )
        }
        .fillMaxWidth()
        .height(with(density) { (remainingHeightPx + 2).toDp() }), // 添加 2px 防止衔接处出现虚线
      content = content,
    )
  }
}

private class BottomSheetNestedScrollConnection(
  private val bottomSheetState: BottomSheetState,
  private val flingBehavior: TargetedFlingBehavior,
) : NestedScrollConnection {

  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    if (!bottomSheetState.userScrollEnabled.value) return super.onPreScroll(available, source)
    val min = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val old = bottomSheetState.showHeight.floatValue
    // 先消耗手指向上的滑动
    if (available.y < 0) {
      bottomSheetState.setState(BottomSheetValueState.Scrolling)
      val new = (old - available.y).coerceIn(min, max)
      val diff = old - new
      bottomSheetState.scrollableState.dispatchRawDelta(diff)
      return Offset(x = 0F, y = diff)
    }
    return super.onPreScroll(available, source)
  }

  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset {
    if (!bottomSheetState.userScrollEnabled.value) return super.onPostScroll(consumed, available, source)
    val min = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val old = bottomSheetState.showHeight.floatValue
    // 再消耗手指向下的滑动，只有 手指拖动 或者 惯性滑动但已经不是完全展开时 才能消耗
    if (available.y > 0 && (source == NestedScrollSource.UserInput || old != max)) {
      bottomSheetState.setState(BottomSheetValueState.Scrolling)
      val new = (old - available.y).coerceIn(min, max)
      val diff = old - new
      bottomSheetState.scrollableState.dispatchRawDelta(diff)
      return Offset(x = 0F, y = diff)
    }
    return super.onPostScroll(consumed, available, source)
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    if (!bottomSheetState.userScrollEnabled.value) return super.onPostFling(consumed, available)
    val max = bottomSheetState.showMaxHeight.floatValue
    val old = bottomSheetState.showHeight.floatValue
    if (old == max) return available.copy(x = 0F) // 完全展开时继续保持展开状态
    var consumeVelocity = available.y
    var targetState = BottomSheetValueState.Expanded
    bottomSheetState.scrollableState.scroll(scrollPriority = MutatePriority.UserInput) {
      with(flingBehavior) {
        consumeVelocity = available.y - performFling(available.y)
      }
      targetState = if (bottomSheetState.fraction == 0F) {
        BottomSheetValueState.Collapsed
      } else {
        BottomSheetValueState.Expanded
      }
    }
    if (targetState != BottomSheetValueState.Expanded && bottomSheetState.requestDismissOnDrag) {
      bottomSheetState.onDismissRequest.invoke(bottomSheetState)
    } else {
      bottomSheetState.setState(targetState)
    }
    return Velocity(x = 0F, y = consumeVelocity)
  }
}
