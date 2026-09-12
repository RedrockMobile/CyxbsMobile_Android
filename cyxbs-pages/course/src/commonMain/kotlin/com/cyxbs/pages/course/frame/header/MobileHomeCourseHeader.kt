package com.cyxbs.pages.course.frame.header

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.cyxbs.components.config.login.rememberLoginDialogState
import com.cyxbs.components.config.time.MinuteTime
import com.cyxbs.components.config.time.Today
import com.cyxbs.components.view.ui.bottomsheet.BottomSheetAnchor
import com.cyxbs.components.view.ui.bottomsheet.BottomSheetMotionState
import com.cyxbs.pages.course.home.HomeCourseFrame
import com.cyxbs.pages.course.view.decoration.CoursePageDecorationManager
import com.cyxbs.pages.course.view.item.CourseItemState
import com.cyxbs.pages.course.view.page.CourseFrameHeader
import com.g985892345.provider.api.annotation.ImplProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

/**
 * 主页课表头，分为折叠时的外课表头与展开时的内课表头
 *
 * @author 985892345
 * @date 2025/3/18
 */
@Composable
fun MobileHomeCourseHeader(
  modifier: Modifier,
  frame: HomeCourseFrame,
) {
  Box(modifier = modifier) {
    val headerVisibility by remember(frame) {
      frame.bottomSheetState.motionStateFlow.filter {
        it !is BottomSheetMotionState.Idle || it.anchor != BottomSheetAnchor.Hidden
      }.map {
        when (it) {
          is BottomSheetMotionState.Idle -> when (it.anchor) {
            BottomSheetAnchor.Hidden -> error("")
            BottomSheetAnchor.Collapsed -> false
            BottomSheetAnchor.Expanded -> true
          }
          is BottomSheetMotionState.Dragging,
          is BottomSheetMotionState.Settling -> null
        }
      }
    }.collectAsState(false)
    // 主页课表外层 header
    MobileHomeCourseOuterHeader(
      frame = frame,
      modifier = Modifier.graphicsLayer {
        alpha = max(1 - frame.bottomSheetState.expansionFraction * 2, 0F)
      },
    )
    if (headerVisibility != false) { // 展开和滚动时才显示，折叠时需要移除掉，把触摸事件透给 MobileHomeCourseOuterHeader
      // 主页课表内层 header
      CourseFrameHeader(
        frame = frame,
        linkBtnVisibility = true,
        modifier = Modifier.pointerInput(Unit) {/*拦截 MobileHomeCourseOuterHeader 点击事件*/}
          .graphicsLayer {
            alpha = max(frame.bottomSheetState.expansionFraction * 2 - 1, 0F)
          }
      )
    }
  }
  LaunchedEffect(frame) {
    if (frame.beginDate.value == null) {
      val selectPageJon = launch {
        // beginDate 未初始化，则进行等待
        frame.beginDate.filterNotNull().first()
        frame.pagerState.scrollToPage(frame.initialPage) // beginDate 初始化后跳到 initialPage
      }
      launch {
        frame.bottomSheetState.awaitSettledAnchor(BottomSheetAnchor.Expanded)
        selectPageJon.cancel() // 如果触发一次展开，则取消回到 initialPage
      }
    }
  }
}

private val EmptyHeader = HintCourseBottomSheetHeader("加载中...")
private val NoLessonHeader = HintCourseBottomSheetHeader("今天和明天都没课咯～")
private val HolidayHeader = HintCourseBottomSheetHeader("享受假期吧～")

@Composable
private fun MobileHomeCourseOuterHeader(
  modifier: Modifier,
  frame: HomeCourseFrame,
) {
  val login = rememberLoginDialogState()
  login.doIfLoginNotShowDialog {
    val headerState = remember(frame) {
      mutableStateOf<CourseBottomSheetHeaderExtension>(EmptyHeader)
    }
    key(headerState.value) {
      headerState.value.CourseBottomSheetHeaderContent(modifier)
    }
    val decorationManager = CoursePageDecorationManager.current
    LaunchedEffect(frame) {
      frame.beginDate.filterNotNull().collectLatest { beginDate ->
        snapshotFlow { Today }.collectLatest { today ->
          if (today < beginDate) {
            headerState.value = HolidayHeader
          } else {
            val page = frame.getPage(today)
            if (page == null) {
              headerState.value = HolidayHeader
            } else {
              delay(500.milliseconds) // 防止上游数据因为首次加载的抖动
              decorationManager.nextItemFlow.collectLatest {
                headerState.value = if (it == null) NoLessonHeader else {
                  // MobileCourseNextSearch 保证返回的一定是 CourseBottomSheetHeaderExtension 类型
                  it.item.extensions.get(CourseBottomSheetHeaderExtension::class)!!
                }
              }
            }
          }
        }
      }
    }
  }.doIfNotLogin {
    HintCourseBottomSheetHeader("登录后才可查看课表") {
      // 点击事件
      showDialog("课表")
    }.CourseBottomSheetHeaderContent(modifier)
  }
}

@ImplProvider
object MobileCourseNextSearch : CoursePageDecorationManager.NextItemSearcher {
  override fun search(sortedList: List<CourseItemState>, now: MinuteTime): CourseItemState? {
    return sortedList.firstOrNull {
      it.item.whatTime.finalTime > now && it.item.extensions.get(CourseBottomSheetHeaderExtension::class) != null
    }
  }
}
