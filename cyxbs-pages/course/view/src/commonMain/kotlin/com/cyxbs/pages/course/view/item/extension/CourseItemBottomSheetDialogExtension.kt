package com.cyxbs.pages.course.view.item.extension

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.cyxbs.components.config.compose.theme.LocalAppColors
import com.cyxbs.components.config.time.MinuteTimePair
import com.cyxbs.components.utils.compose.LocalImePaddingTargetState
import com.cyxbs.components.utils.compose.imePaddingWithTarget
import com.cyxbs.components.utils.compose.plusDsl
import com.cyxbs.components.utils.compose.rememberImePaddingTargetState
import com.cyxbs.components.view.ui.BottomSheetCompose
import com.cyxbs.components.view.ui.BottomSheetState
import com.cyxbs.components.view.ui.BottomSheetValueState
import com.cyxbs.components.view.ui.Window
import com.cyxbs.pages.course.view.item.CourseItemState
import com.cyxbs.pages.course.view.item.modifier.BeginFinalTimeShowModifier
import com.cyxbs.pages.course.view.item.modifier.observeItemRectOnScreen
import com.cyxbs.pages.course.view.overlay.OverlapResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 点击课表 item 弹起的 BottomSheetDialog
 *
 * @author 985892345
 * @date 2025/3/29
 */
interface CourseItemBottomSheetDialogExtension : CourseItemExtension {

  val itemState: CourseItemState

  @Composable
  fun CourseBottomSheetDialogContent(state: CourseItemBottomSheetDialogState)
}

private val DefaultCourseBottomSheetHeight = 280.dp

val LocalCourseItemBottomSheetDialog =
  staticCompositionLocalOf<CourseItemBottomSheetDialogState> { error("未初始化") }

@Stable
@Composable
fun rememberCourseItemBottomSheetDialogState(): CourseItemBottomSheetDialogState {
  val state = remember {
    CourseItemBottomSheetDialogState()
  }
  MobileCourseBottomSheetDialog(state) // 这里注册了 Dialog
  return state
}

@Stable
class CourseItemBottomSheetDialogState {

  val dialogContents: MutableStateFlow<List<CourseItemBottomSheetDialogExtension>> =
    MutableStateFlow(emptyList())

  private var dismissRequestGate: (suspend () -> Boolean)? = null
  private val windowOverlayContent = mutableStateOf<(@Composable () -> Unit)?>(null)

  val bottomSheetState = BottomSheetState(
    onDismissRequest = {
      // 业务内容可挂起关闭并展示确认；继续编辑后才回弹，放弃后完成收起。
      if (dismissRequestGate?.invoke() != false) collapseSuspend() else expandSuspend()
    },
  )

  // 当前选中的 item
  val currentPageItemFlow: MutableStateFlow<CourseItemBottomSheetDialogExtension?> =
    MutableStateFlow(null)

  // 编辑当前 item 时锁定 Pager；保留原内容列表以避免切换布局分支后重建并丢失表单状态。
  internal val currentPageLockedFlow = MutableStateFlow(false)

  fun showDialog(extension: CourseItemBottomSheetDialogExtension) {
    clear()
    dialogContents.value = listOf(extension)
  }

  fun showDialog(overlapResult: OverlapResult?) {
    if (overlapResult == null) {
      dismissDialog()
    } else {
      clear()
      dialogContents.value = collectCoveredItems(
        rootItemState = overlapResult.itemState,
        otherOverlap = overlapResult,
        set = linkedSetOf(overlapResult.itemState)
      ).mapNotNull { it.item.extensions.get(CourseItemBottomSheetDialogExtension::class) }
    }
  }

  fun dismissDialog() {
    clear()
    dialogContents.value = emptyList()
  }

  /**
   * 请求宿主弹窗先执行收起动画；[BottomSheet] 监听到 Collapsed 后才会调用 [dismissDialog] 清理内容。
   */
  fun dismissDialogAnimated() {
    bottomSheetState.collapseAsync()
  }

  /**
   * 将重叠项 Pager 锁定在当前页。
   *
   * 编辑期间禁止切换对象并隐藏分页提示，但不改写 [dialogContents]，从而保留当前表单的 Compose 状态。
   * 弹窗关闭时会由 [clear] 统一解除锁定。
   */
  fun lockCurrentPage() {
    if (currentPageItemFlow.value != null) currentPageLockedFlow.value = true
  }

  /**
   * 更新当前业务内容的关闭拦截。
   *
   * [gate] 返回 true 时允许宿主继续收起；返回 false 时宿主会回弹至展开状态，由业务内容继续展示确认层；
   * 传入 null 表示业务内容已离开组合，解除拦截并恢复直接关闭。
   */
  fun updateDismissRequestGate(gate: (suspend () -> Boolean)?) {
    dismissRequestGate = gate
  }

  /** 更新绘制在课表 Window 最上层的业务弹层，避免其随 BottomSheet 一起被拖出屏幕。 */
  fun updateWindowOverlayContent(content: (@Composable () -> Unit)?) {
    windowOverlayContent.value = content
  }

  /** 在课表 Window 根布局末尾绘制当前业务弹层。 */
  @Composable
  internal fun WindowOverlayContent() {
    windowOverlayContent.value?.invoke()
  }

  private fun clear() {
    dismissRequestGate = null
    windowOverlayContent.value = null
    bottomSheetState.userScrollEnabled.value = true
    currentPageItemFlow.value = null
    currentPageLockedFlow.value = false
  }

  private fun collectCoveredItems(
    rootItemState: CourseItemState,
    otherOverlap: OverlapResult,
    set: MutableSet<CourseItemState>,
  ): Set<CourseItemState> {
    otherOverlap.coveredItemList.fastForEach {
      val itemState = it.result.itemState
      val itemWhatTimeFixed = itemState.item.whatTime.now.value
      val rootWhatTimeFixed = rootItemState.item.whatTime.now.value
      if (itemWhatTimeFixed.beginTime < rootWhatTimeFixed.finalTime
        && itemWhatTimeFixed.finalTime > rootWhatTimeFixed.beginTime
      ) {
        set.add(itemState)
      }
      collectCoveredItems(rootItemState, it.result, set)
    }
    return set
  }
}

/**
 * 移动端课表 item 点击后出现的 BottomSheetDialog
 */
@Composable
private fun MobileCourseBottomSheetDialog(
  state: CourseItemBottomSheetDialogState,
) {
  state.dialogContents.collectAsState().value.firstOrNull() ?: return
  Window(
    // 返回键交给 BottomSheetState，与蒙层点击共用业务关闭拦截。
    dismissOnBackPress = null,
  ) {
    val imePaddingTargetState = rememberImePaddingTargetState()
    CompositionLocalProvider(LocalImePaddingTargetState provides imePaddingTargetState) {
      // 只上移到目标编辑区域完整可见，弹窗其余部分仍允许被键盘覆盖。
      Box(modifier = Modifier.imePaddingWithTarget(imePaddingTargetState)) {
        ShowBeginFinalTime(state)
        CurrentItemShowTop(state)
        BottomSheet(state)
        // 业务确认层必须位于可拖动 BottomSheet 之外，收起后仍能立即显示。
        state.WindowOverlayContent()
      }
    }
  }
}

// 如果 item 被弹窗遮挡，则将滚轴向上移动
@Composable
@OptIn(ExperimentalCoroutinesApi::class)
private fun OffsetScroll(
  state: CourseItemBottomSheetDialogState,
  layoutTopOnScreenFlow: SharedFlow<Float>,
) {
  val marginBottomKey = "MobileCourseBottomSheetDialog#OffsetScroll"
  val scrollContext = remember {
    state.dialogContents.value.first().itemState.coursePageFlow.value?.scrollContext
  }
  if (scrollContext == null) return // 如果是下一周的课程的话，则会有未初始化的情况
  val marginBottomState = remember {
    scrollContext.timeline.marginBottom
  }
  LaunchedEffect(Unit) {
    val initScrollValue = scrollContext.scrollState.value
    var settledBaseScrollValue = initScrollValue
    var hasSettledBaseScroll = false
    var lastSettledState = state.bottomSheetState.state

    /**
     * 返回未施加弹窗避让时的课表滚动位置。
     *
     * 首次打开期间固定为点击前的位置；BottomSheet 首次展开后会根据 Item 的当前位置重新确定一次，
     * 之后切换 Item、拖拽和关闭均使用该固定值，避免滚动基准随布局反复变化。
     */
    fun baseScrollValue(): Int = settledBaseScrollValue.coerceIn(0, scrollContext.scrollState.maxValue)

    fun CourseItemBottomSheetDialogExtension.observeOverlapOnce(): Flow<Float> = flow {
      val layoutTopOnScreen = layoutTopOnScreenFlow.first()
      val itemRectOnScreen = itemState.observeItemRectOnScreen(forceCalculate = true).first()
        .translate(
          translateX = 0F,
          translateY = (
            marginBottomState.getOrElse(marginBottomKey) { 0 } +
                scrollContext.scrollState.value - baseScrollValue()
            ).toFloat(),
        )
      emit(itemRectOnScreen.bottom - layoutTopOnScreen)
    }

    /** 返回 OffsetScroll 已经施加到课表上的完整偏移。 */
    fun currentOffset(): Float {
      val scrollOffset = scrollContext.scrollState.value - baseScrollValue()
      val marginOffset = marginBottomState.getOrElse(marginBottomKey) { 0 }
      return (scrollOffset + marginOffset).toFloat().coerceAtLeast(0F)
    }

    /**
     * BottomSheet 首次展开后确定关闭弹窗要恢复到的滚动位置。
     *
     * 如果 Item 在点击前的滚动位置本就可见，则继续沿用原位置；仅当最终 Item 底部超出课表视口时，
     * 才把基准向下移动到刚好可见。这样中午时间轴不会因为 `maxValue == 0` 被误判为需要追随底部，
     * 夜间时间轴关闭弹窗后也不会重新把 Item 留在屏幕外。
     */
    suspend fun trySettleBaselineAfterOpening() {
      if (hasSettledBaseScroll) return
      hasSettledBaseScroll = true
      val outerCoordinates = scrollContext.outerCoordinates ?: return
      if (!outerCoordinates.isAttached) return
      val currentItem = state.currentPageItemFlow.value ?: return
      if (scrollContext.scrollState.value == scrollContext.scrollState.maxValue) {
        // 如果展开后滚轴已经是最大值，则收回时也显示在最大值，因为晚上的 item 通过后面的 requiredScroll 的计算会少一些距离
        settledBaseScrollValue = scrollContext.scrollState.maxValue
        return
      }
      val currentRect = currentItem.itemState.observeItemRectOnScreen(forceCalculate = true).first()
      // 撤销当前弹窗避让，得到 Item 位于旧基准滚动位置时的最终布局坐标。
      val itemBottomWithoutAvoidance = currentRect.bottom + currentOffset()
      val viewportBottom = outerCoordinates.positionOnScreen().y + outerCoordinates.size.height
      val requiredScroll = (itemBottomWithoutAvoidance - viewportBottom)
        .coerceAtLeast(0F).roundToInt()
      settledBaseScrollValue = (settledBaseScrollValue + requiredScroll)
        .coerceIn(0, scrollContext.scrollState.maxValue)
    }

    /**
     * 立即应用完整偏移，优先使用课表本身可滚动的空间，不足的部分再交给底部补偿。
     *
     * 弹窗与时间轴本身已在逐帧动画时调用该方法，避免在外层再叠加一层动画。
     */
    suspend fun applyOffset(targetOffset: Float) {
      val target = targetOffset.coerceAtLeast(0F)
      val targetScrollValue = (baseScrollValue() + target.roundToInt())
        .coerceIn(0, scrollContext.scrollState.maxValue)
      scrollContext.scrollState.scrollBy(
        (targetScrollValue - scrollContext.scrollState.value).toFloat()
      )
      marginBottomState[marginBottomKey] = (
        target - (scrollContext.scrollState.value - baseScrollValue())
        ).roundToInt().coerceAtLeast(0)
    }

    /**
     * 将当前完整偏移动画到新 Item 的目标偏移。
     *
     * 滚动值和底部补偿必须共享同一动画进度，否则可滚动部分会先跳变，视觉上就像切换动画消失。
     */
    suspend fun animateOffset(targetOffset: Float) {
      val target = targetOffset.coerceAtLeast(0F)
      val initial = currentOffset()
      if (initial == target) return
      scrollContext.scrollState.scroll {
        val scrollScope = this
        animate(
          initialValue = initial,
          targetValue = target,
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        ) { value, _ ->
          val targetScrollValue = (baseScrollValue() + value.roundToInt())
            .coerceIn(0, scrollContext.scrollState.maxValue)
          scrollScope.scrollBy(
            (targetScrollValue - scrollContext.scrollState.value).toFloat()
          )
          marginBottomState[marginBottomKey] = (
            value - (scrollContext.scrollState.value - baseScrollValue())
            ).roundToInt().coerceAtLeast(0)
        }
      }
    }

    state.bottomSheetState.stateFlow.collectLatest { bottomSheetState ->
      when (bottomSheetState) {
        BottomSheetValueState.Scrolling -> {
          if (lastSettledState == BottomSheetValueState.Expanded) {
            // 关闭时不再读取 Item；仅按照 BottomSheet 收起进度还原已经施加的偏移。
            val startFraction = state.bottomSheetState.fraction.coerceAtLeast(0F)
            val startOffset = currentOffset()
            if (startFraction == 0F) {
              applyOffset(0F)
            } else {
              snapshotFlow { state.bottomSheetState.fraction }.collect { fraction ->
                applyOffset(startOffset * (fraction / startFraction).coerceIn(0F, 1F))
              }
            }
          } else {
            // 打开期间同时跟随 BottomSheet 与时间轴动画，动画结束后立即停止观察。
            layoutTopOnScreenFlow.combine(
              state.currentPageItemFlow.filterNotNull()
                .flatMapLatest { extension ->
                  extension.itemState.observeItemRectOnScreen(forceCalculate = true)
                    .map { rect ->
                      // 排除本逻辑自身施加的滚动与 margin，防止坐标反馈造成上下闪动；
                      // 时间轴展开改变的真实布局坐标仍会保留。
                      rect.translate(
                        translateX = 0F,
                        translateY = (
                          marginBottomState.getOrElse(marginBottomKey) { 0 } +
                              scrollContext.scrollState.value - baseScrollValue()
                          ).toFloat(),
                      )
                    }
                }
            ) { layoutTopOnScreen, itemRectOnScreen ->
              itemRectOnScreen.bottom - layoutTopOnScreen
            }.collect { applyOffset(it) }
          }
        }

        BottomSheetValueState.Expanded -> {
          lastSettledState = BottomSheetValueState.Expanded
          trySettleBaselineAfterOpening()
          // 进入稳定展开态时跳过当前值；后续仅为 Pager 切换的新 Item 计算一次目标位置。
          state.currentPageItemFlow.filterNotNull().drop(1)
            .flatMapLatest { it.observeOverlapOnce() }
            .collectLatest { animateOffset(it) }
        }

        BottomSheetValueState.Collapsed,
        BottomSheetValueState.Hide -> {
          lastSettledState = bottomSheetState
          if (currentOffset() != 0F) {
            applyOffset(0F)
          }
        }
      }
    }
  }
  DisposableEffect(Unit) {
    onDispose {
      // 在弹窗消失时强制重置 marginBottom
      marginBottomState[marginBottomKey] = 0
    }
  }
}

@Composable
private fun BottomSheet(
  state: CourseItemBottomSheetDialogState,
) {
  BottomSheetCompose(
    bottomSheetState = state.bottomSheetState,
    dismissOnClickOutside = true,
    scrimColor = Color.Transparent,
  ) {
    val currentPageLocked by state.currentPageLockedFlow.collectAsState()
    val layoutTopOnScreenFlow = remember {
      MutableSharedFlow<Float>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
      )
    }
    val bottomSheetBackgroundColor = LocalAppColors.current.whiteBlack
    val shadowHeightPx = with(LocalDensity.current) { 36.dp.toPx() }
    val shadowBrush = remember(shadowHeightPx) {
      // 阴影高度固定，仅在 density 变化时重建 Brush，内容尺寸动画不会产生重复分配。
      Brush.verticalGradient(
        colors = listOf(Color(0x005369BC), Color(0x205369BC)),
        endY = shadowHeightPx,
      )
    }
    OffsetScroll(state, layoutTopOnScreenFlow)
    Box(
      modifier = Modifier
        // 背景必须画在尺寸动画外层；子项的 matchParentSize 会先跳到目标高度，收缩期间会透出课表。
        .drawBehind {
          val top = 20.dp.toPx().coerceAtMost(size.height)
          val fillHeight = size.height - top
          val radius = minOf(16.dp.toPx(), fillHeight / 2F)
          val shadowHeight = shadowHeightPx.coerceAtMost(size.height)
          // 先画完整阴影，再用不透明圆角背景覆盖其内部，只从弹窗的圆角外缘保留阴影。
          drawRect(
            brush = shadowBrush,
            size = Size(size.width, shadowHeight),
          )
          drawRoundRect(
            color = bottomSheetBackgroundColor,
            topLeft = Offset(0F, top),
            size = Size(size.width, fillHeight),
            cornerRadius = CornerRadius(radius),
          )
          // 只保留顶部圆角，底部继续铺满导航栏区域。
          if (fillHeight > radius) {
            drawRect(
              color = bottomSheetBackgroundColor,
              topLeft = Offset(0F, top + radius),
              size = Size(size.width, fillHeight - radius),
            )
          }
        }
        .navigationBarsPadding()
        .fillMaxWidth()
        .then(
          if (currentPageLocked) Modifier.heightIn(min = DefaultCourseBottomSheetHeight)
          else Modifier.height(DefaultCourseBottomSheetHeight)
        )
        // 编辑态由当前内容的实测高度撑开；尺寸变化由宿主统一做平滑过渡。
        .animateContentSize()
        .then(bottomSheetDraggable())
        .onGloballyPositioned {
          // IME 位移由 Compose 布局完成，因此普通布局回调即可同步课表滚轴。
          layoutTopOnScreenFlow.tryEmit(it.positionOnScreen().y)
        }
    ) {
      Box(
        modifier = Modifier.padding(top = 20.dp)
          .fillMaxWidth()
          .then(if (currentPageLocked) Modifier else Modifier.fillMaxSize())
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      ) {
        CourseBottomSheetDialogContent(state)
      }
    }
  }
  LaunchedEffect(Unit) {
    try {
      state.bottomSheetState.expandSuspend()
    } catch (e: CancellationException) {
      // 在展开动画时用户可能快速点击空白区域触发 collapse()，这里就会抛出 CancellationException
    }
    state.bottomSheetState.stateFlow.first { it == BottomSheetValueState.Collapsed }
    state.dismissDialog()
  }
}

// 显示 item 开始结束时间
@Composable
private fun ShowBeginFinalTime(
  state: CourseItemBottomSheetDialogState,
) {
  LaunchedEffect(Unit) {
    var unlockRunnable: Runnable? = null
    var isLockWhenBegin: Boolean? = null
    state.currentPageItemFlow.filterNotNull().map {
      it.itemState
    }.onCompletion {
      unlockRunnable?.run()
    }.collectLatest { itemState ->
      unlockRunnable?.run()
      if (isLockWhenBegin == null) {
        isLockWhenBegin = BeginFinalTimeShowModifier.showLock.get(itemState).isLocked() // 如果为 true 则说明已经可见
      }
      unlockRunnable = BeginFinalTimeShowModifier.showLock.get(itemState).lock().let {
        Runnable {
          // 包裹一层用于还原 alphaState
          it.run()
          BeginFinalTimeShowModifier.alphaState.get(itemState).floatValue = 1F
        }
      }
      if (!isLockWhenBegin) {
        // 如果最开始已经锁定，说明已经在展示开始结束时间了，那就不主动关联上透明度变化
        BeginFinalTimeShowModifier.alphaState.get(itemState).floatValue = 0F
        snapshotFlow { state.bottomSheetState.fraction.coerceIn(0F, 1F) }.collect {
          BeginFinalTimeShowModifier.alphaState.get(itemState).floatValue = it
        }
      }
    }
  }
}

// 点击后的 item 置顶全显示
@Composable
private fun CurrentItemShowTop(
  state: CourseItemBottomSheetDialogState,
) {
  LaunchedEffect(Unit) {
    var lastItem: CourseItemState? = null
    val showAllInterceptor = CourseItemState.ShowRangeTransformer { _, overlap ->
      // item 被遮挡的区域都显示出来
      val whatTimeFixed = overlap.itemState.item.whatTime.now.value
      val beginTime = whatTimeFixed.beginTime
      val finalTime = whatTimeFixed.finalTime
      listOf(MinuteTimePair(beginTime, finalTime))
    }

    fun reset() {
      lastItem?.zIndexState?.floatValue--
      lastItem?.removeShowRangeTransformer(showAllInterceptor)
      lastItem = null
    }

    fun setItem() {
      if (lastItem != null) return
      val item = state.currentPageItemFlow.value?.itemState
      lastItem = item
      item?.zIndexState?.floatValue++ // 置顶展示
      item?.addShowRangeTransformer(showAllInterceptor)
    }
    launch {
      state.currentPageItemFlow.onCompletion {
        reset() // 协程作用域被取消时调用，此时 Compose 组件被移除
      }.collect {
        reset()
        setItem()
      }
    }
    launch {
      // 因为底部弹窗关闭时存在动画，导致需要一定时间才会触发 onCompletion 的 reset
      // 所以单独监听滚动距离来检测是否需要 reset
      // todo 后续想办法修下这个弹窗关闭动画过长的问题
      snapshotFlow { state.bottomSheetState.fraction.coerceIn(0F, 1F) }.collect {
        if (it < 0.2F) reset() else setItem()
      }
    }
  }
}

@Composable
private fun CourseBottomSheetDialogContent(
  state: CourseItemBottomSheetDialogState,
) {
  val itemDialogContents by state.dialogContents.collectAsState()
  val currentPageLocked by state.currentPageLockedFlow.collectAsState()
  val currentPageItem by state.currentPageItemFlow.collectAsState()
  if (itemDialogContents.isEmpty()) return
  // 同一 movable content 可从 Pager 移到单项容器而不重建，编辑表单及焦点状态因此得以保留。
  val movableContents = remember(state) {
    mutableMapOf<Pair<Int, CourseItemBottomSheetDialogExtension>, @Composable () -> Unit>()
  }
  fun movableContent(
    page: Int,
    extension: CourseItemBottomSheetDialogExtension,
  ): @Composable () -> Unit =
    movableContents.getOrPut(page to extension) {
      movableContentOf { extension.CourseBottomSheetDialogContent(state) }
    }
  val pagerState = rememberPagerState(
    initialPage = if (itemDialogContents.size == 1) 0 else itemDialogContents.size * 1000,
  ) {
    if (itemDialogContents.size == 1) 1 else Int.MAX_VALUE
  }
  LaunchedEffect(Unit) {
    snapshotFlow { pagerState.currentPage }.collect {
      state.currentPageItemFlow.value =
        itemDialogContents[pagerState.currentPage % itemDialogContents.size]
    }
  }
  Column(
    modifier = if (currentPageLocked) Modifier.fillMaxWidth() else Modifier.fillMaxSize()
  ) {
    if (currentPageLocked) {
      val extension = currentPageItem ?: itemDialogContents.first()
      val contentPage = if (itemDialogContents.size == 1) 0 else pagerState.currentPage
      movableContent(contentPage, extension).invoke()
    } else if (itemDialogContents.size > 1) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth().weight(1F),
        // Pager 默认垂直居中；短内容（例如日程详情）会因此在顶部留下大块空白。
        verticalAlignment = Alignment.Top,
      ) { page ->
        val itemDialogContent = if (itemDialogContents.isEmpty()) null
        else itemDialogContents[page % itemDialogContents.size]
        itemDialogContent?.let { movableContent(page, it).invoke() }
      }
    } else {
      val itemDialogContent = itemDialogContents.firstOrNull()
      if (itemDialogContent != null) {
        key(itemDialogContent.hashCode()) {
          movableContent(0, itemDialogContent).invoke()
        }
      }
    }
    // 底部的圆点指示器
    Spacer(modifier = Modifier.fillMaxWidth()
      .height(if (currentPageLocked) 0.dp else 24.dp)
      .plusDsl {
      if (itemDialogContents.size > 1 && !currentPageLocked) {
        drawWithCache {
          val radius = 4.dp.toPx()
          val interval = 16.dp.toPx()
          val beginX = size.width / 2 - (itemDialogContents.size - 1) * interval / 2
          val beginY = size.height / 2
          val path1 = Path()
          val path2 = Path()
          onDrawBehind {
            val itemCount = itemDialogContents.size
            val currentPage = pagerState.currentPage
            val currentPageOffset = pagerState.currentPageOffsetFraction
            val absoluteOffset = currentPage + currentPageOffset
            val relativeOffset = if (absoluteOffset % itemCount > itemCount - 1) { // 当划出边界时
              (1 - (absoluteOffset - absoluteOffset.toInt())) * (itemCount - 1) // 这里可以表示从右边界到左边界(或相反)经过的值
            } else absoluteOffset % itemCount
            repeat(itemCount) {
              drawCircle(Color(0xFF888888), radius, Offset(beginX + it * interval, beginY))
            }
            val relativeOffsetInt = relativeOffset.toInt()
            val path = getWaterDropIndicator(
              path1,
              path2,
              radius,
              relativeOffset - relativeOffsetInt,
              interval
            )
            path.translate(Offset(beginX + relativeOffsetInt * interval, beginY))
            drawPath(path, Color(0xFF788EFA))
          }
        }
      }
    })
  }
}

// 基本思路是两个圆点之间的上下方有两个半径很大的圆, 小圆点就在这两个大圆之间被挤压着移动
private fun getWaterDropIndicator(
  path1: Path,
  path2: Path,
  radius: Float,
  fraction: Float, // 0.0 -> 1.0
  interval: Float,
): Path {
  path1.rewind()
  path2.rewind()
  // 中间大圆的坐标
  val outerX = interval / 2
  val outerY = interval
  val outerR = hypot(outerX, outerY) - radius
  // 绘制当前移动点的圆
  val nowX = fraction * interval
  val nowR = hypot(outerX - nowX, outerY) - outerR
  path1.addRoundRect(RoundRect(Rect(Offset(nowX, 0F), nowR), CornerRadius(nowR)))
  // 绘制跟随移动的圆
  val startMove = 0.6F
  val k = 1 / (1 - startMove)
  val b = 1 - k
  val followX = max(0F, k * fraction + b) * interval
  val followR = hypot(outerX - followX, outerY) - outerR
  path1.addRoundRect(RoundRect(Rect(Offset(followX, 0F), followR), CornerRadius(followR)))
  // 与两个圆上下端点构成的四边形相并
  path2.moveTo(nowX, nowR)
  path2.lineTo(nowX, -nowR)
  path2.lineTo(followX, -followR)
  path2.lineTo(followX, followR)
  path2.close()
  path1.op(path1, path2, PathOperation.Union)
  // 排除上下两个大圆
  path2.rewind()
  path2.addRoundRect(RoundRect(Rect(Offset(outerX, outerY), outerR), CornerRadius(outerR)))
  path2.addRoundRect(RoundRect(Rect(Offset(outerX, -outerY), outerR), CornerRadius(outerR)))
  path1.op(path1, path2, PathOperation.Difference)
  return path1
}
