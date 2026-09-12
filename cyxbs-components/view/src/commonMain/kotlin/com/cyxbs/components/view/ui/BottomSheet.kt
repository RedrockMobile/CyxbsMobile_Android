package com.cyxbs.components.view.ui

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import kotlin.math.abs
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
   * 当前吸附动画在 ScrollableState 坐标系中的速度，正值表示 Sheet 向下移动。
   *
   * 动画正常结束时清零；被新动画取消时保留最后一帧速度，使接管动画可以连续起步。该状态只在
   * Compose UI 线程中的 ScrollableState 互斥区读写，不参与重组。
   */
  internal var animationVelocity = 0F

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

  /**
   * BottomSheet 统一使用的吸附弹簧。
   *
   * 拖拽释放由 [BottomSheetFlingBehavior] 将手势速度传入该弹簧；程序化切换若接管一段
   * 正在运行的吸附动画，也会从上一帧速度继续，静止状态下则从零速度启动。
   */
  internal val bottomSheetSpring = spring<Float>(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = 1F,
  )

  /**
   * 使用当前吸附弹簧把 Sheet 移动到指定显示高度。
   *
   * 新动画取得 ScrollableState 互斥权后才读取 [animationVelocity]，确保上一段被取消的动画已经
   * 写入最后一帧速度。正常完成或因布局边界拒绝位移而提前结束时清零；协程被接管动画取消时则
   * 刻意保留速度。
   *
   * @param targetHeight Sheet 最终露出的高度，单位为 px。
   */
  private suspend fun animateToHeight(targetHeight: Float) {
    scrollableState.scroll {
      val targetScrollOffset = showHeight.floatValue - targetHeight
      if (abs(targetScrollOffset) < 1F) {
        scrollBy(targetScrollOffset)
        animationVelocity = 0F
        return@scroll
      }

      var consumedOffset = 0F
      AnimationState(
        initialValue = 0F,
        initialVelocity = animationVelocity,
      ).animateTo(
        targetValue = targetScrollOffset,
        animationSpec = bottomSheetSpring,
      ) {
        val requestedDelta = value - consumedOffset
        val consumedDelta = scrollBy(requestedDelta)
        consumedOffset += consumedDelta
        animationVelocity = velocity
        if (abs(requestedDelta - consumedDelta) >= 1F) {
          cancelAnimation()
        }
      }
      // 外部取消会直接抛出 CancellationException，不会执行到这里，最后一帧速度会留给接管动画。
      animationVelocity = 0F
    }
  }

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
    // now 即使等于 target 也需要进入 scroll mutation，将其他正在进行中的动画取消掉。
    animateToHeight(target)
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
    animateToHeight(target)
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
    val target = 0F
    // hide 不触发 Scrolling 状态
    animateToHeight(target)
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
        // 被新命令的吸附动画取消（例如展开动画中触发了折叠），最后一帧速度会由新动画继承。
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

/**
 * 使用单段弹簧完成 Sheet 松手后的锚点吸附。
 */
private class BottomSheetFlingBehavior(
  private val bottomSheetState: BottomSheetState,
) : TargetedFlingBehavior {

  /** Spring 与布局边界之间不足该距离时视为抵达目标，单位为 px。 */
  private val settlingThresholdPx = 1F

  /**
   * 将松手速度投影到未来位置时使用的时间窗口，单位为秒。
   *
   * 投影只决定最终吸附锚点，不会先播放一段衰减动画。
   */
  private val velocityProjectionSeconds = 0.2F

  /**
   * `initialVelocity` 一方面用于预测用户意图并选择 Hide、Collapsed 或 Expanded，另一方面作为
   * Spring 的初速度直接延续手势。这里不播放 decay 动画，避免速度曲线在 decay 与 snap 的交界处
   * 发生肉眼可见的阶段切换。
   */
  override suspend fun ScrollScope.performFling(
    initialVelocity: Float,
    onRemainingDistanceUpdated: (Float) -> Unit,
  ): Float {
    bottomSheetState.animationVelocity = initialVelocity
    val now = bottomSheetState.showHeight.floatValue
    val targetHeight = calculateTargetHeight(initialVelocity)
    // ScrollableState 的正方向会减小 showHeight，因此目标滚动量需要使用 now - targetHeight。
    val targetScrollOffset = now - targetHeight
    onRemainingDistanceUpdated(targetScrollOffset)
    if (abs(targetScrollOffset) < settlingThresholdPx) {
      scrollBy(targetScrollOffset)
      onRemainingDistanceUpdated(0F)
      bottomSheetState.animationVelocity = 0F
      return 0F
    }

    var consumedOffset = 0F
    var remainingVelocity = initialVelocity
    AnimationState(
      initialValue = 0F,
      initialVelocity = initialVelocity,
    ).animateTo(
      targetValue = targetScrollOffset,
      animationSpec = bottomSheetState.bottomSheetSpring,
    ) {
      val requestedDelta = value - consumedOffset
      val consumedDelta = scrollBy(requestedDelta)
      consumedOffset += consumedDelta
      remainingVelocity = velocity
      bottomSheetState.animationVelocity = velocity
      onRemainingDistanceUpdated(targetScrollOffset - consumedOffset)

      // 内容高度或 Insets 在动画途中变化时，布局边界可能拒绝部分位移；此时立即结束并把剩余速度
      // 交还嵌套滚动链，避免弹簧持续尝试越过已经失效的目标。
      if (abs(requestedDelta - consumedDelta) >= settlingThresholdPx) {
        cancelAnimation()
      }
    }
    val result = if (abs(targetScrollOffset - consumedOffset) < settlingThresholdPx) {
      0F
    } else {
      remainingVelocity
    }
    // 被外部动画取消时不会执行到这里，最后一帧速度会保留给接管动画。
    bottomSheetState.animationVelocity = 0F
    return result
  }

  /** 根据当前位置和释放速度投影选择最终锚点，不实际播放投影过程。 */
  private fun calculateTargetHeight(initialVelocity: Float): Float {
    val peekHeight = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val now = bottomSheetState.showHeight.floatValue
    if (max <= 0F) return 0F

    // 正速度表示向下拖动，因此从 showHeight 中减去速度投影距离。
    val projectedHeight = (
      now - initialVelocity * velocityProjectionSeconds
    ).coerceIn(0F, max)
    if (!bottomSheetState.hideable) {
      return if (projectedHeight <= (peekHeight + max) / 2F) peekHeight else max
    }

    return when {
      projectedHeight <= peekHeight / 2F -> 0F
      projectedHeight <= (peekHeight + max) / 2F -> peekHeight
      else -> max
    }
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
  val flingBehavior = remember(bottomSheetState) {
    BottomSheetFlingBehavior(bottomSheetState)
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
          bottomSheetState.animationVelocity = 0F
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
    // 只接管用户手指产生的剩余位移；列表的 SideEffect 惯性应在 onPostFling 以剩余速度一次性交接，
    // 否则 Sheet 会先跟随列表 decay 移动，再切换到 Spring，形成两段式速度变化。
    if (available.y > 0 && source == NestedScrollSource.UserInput) {
      bottomSheetState.setState(BottomSheetValueState.Scrolling)
      val new = (old - available.y).coerceIn(min, max)
      val diff = old - new
      bottomSheetState.scrollableState.dispatchRawDelta(diff)
      return Offset(x = 0F, y = diff)
    }
    return super.onPostScroll(consumed, available, source)
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    if (!bottomSheetState.userScrollEnabled.value) return super.onPreFling(available)
    val min = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val current = bottomSheetState.showHeight.floatValue
    val isBetweenAnchors = current > min && current < max
    val shouldExpandFromCollapsed = current <= min && available.y < 0F
    if (!isBetweenAnchors && !shouldExpandFromCollapsed) return Velocity.Zero

    // Sheet 已经离开锚点时，应在子列表启动 decay 前取得完整手势速度；折叠态的向上 fling 也应
    // 优先展开 Sheet。完全展开态则先交给列表；列表到顶后的剩余惯性由 onPostFling 截断，避免
    // 同一次 fling 继续折叠 Sheet。
    val remainingVelocity = settleWithVelocity(available.y)
    return Velocity(x = 0F, y = available.y - remainingVelocity)
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    if (!bottomSheetState.userScrollEnabled.value) return super.onPostFling(consumed, available)
    val min = bottomSheetState.peekHeightUpdater.peekHeightPx
    val max = bottomSheetState.showMaxHeight.floatValue
    val current = bottomSheetState.showHeight.floatValue

    // Sheet 完全展开时，onPostFling 收到的是子列表 fling 后未消费的剩余速度。这里刻意截断它，
    // 使列表的惯性最多滚到顶部，不会在同一次 fling 中继续带动 Sheet 折叠；用户需要继续按住拖动，
    // 或重新向下拖动，才会通过 onPostScroll 让 Sheet 离开展开锚点。返回 available 表示这部分速度
    // 已被当前嵌套滚动节点消费，避免它继续传给更外层容器。
    if (current == max) return available.copy(x = 0F)

    val canMoveDown = available.y > 0F && current > min
    val canMoveUp = available.y < 0F && current < max
    if (!canMoveDown && !canMoveUp) return Velocity.Zero

    // 子列表已经优先处理其 fling，这里只用它无法消费的剩余速度驱动 Sheet。
    val remainingVelocity = settleWithVelocity(available.y)
    return Velocity(x = 0F, y = available.y - remainingVelocity)
  }

  /**
   * 使用嵌套滚动交付的速度吸附 Sheet，并在离开滚动互斥区后同步最终业务状态。
   *
   * @param initialVelocity 嵌套滚动坐标系中的初速度，正值向下、负值向上。
   * @return Sheet 动画结束后未消费、可继续交给祖先节点的速度。
   */
  private suspend fun settleWithVelocity(initialVelocity: Float): Float {
    var remainingVelocity = initialVelocity
    var targetState = BottomSheetValueState.Expanded
    bottomSheetState.scrollableState.scroll(scrollPriority = MutatePriority.UserInput) {
      with(flingBehavior) {
        remainingVelocity = performFling(initialVelocity)
      }
      targetState = when {
        bottomSheetState.fraction < 0F -> BottomSheetValueState.Hide
        bottomSheetState.fraction == 0F -> BottomSheetValueState.Collapsed
        else -> BottomSheetValueState.Expanded
      }
    }
    if (targetState != BottomSheetValueState.Expanded && bottomSheetState.requestDismissOnDrag) {
      bottomSheetState.onDismissRequest.invoke(bottomSheetState)
    } else {
      bottomSheetState.setState(targetState)
    }
    return remainingVelocity
  }
}
