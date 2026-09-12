package com.cyxbs.pages.course.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cyxbs.components.view.ui.bottomsheet.BottomSheetState
import com.cyxbs.pages.course.api.IMobileHomeCourseFrame
import com.cyxbs.pages.course.frame.header.MobileHomeCourseHeader
import com.cyxbs.pages.course.home.bottomsheet.MobileHomeBottomSheet
import com.cyxbs.pages.course.home.item.MobileCourseCreateItemFactory
import com.cyxbs.pages.course.home.item.MobileCourseLessonItemFactory
import com.cyxbs.pages.course.home.item.MobileCourseLinkLessonItemFactory
import com.cyxbs.pages.course.home.item.MobileScheduleItemFactory
import com.cyxbs.pages.course.view.AbstractCourseFrame
import com.cyxbs.pages.course.view.HomeCoursePageContent
import com.cyxbs.pages.course.view.decoration.CoursePageDecorationManager
import com.cyxbs.pages.course.view.decoration.impl.CreateItemPageDecoration
import com.cyxbs.pages.course.view.decoration.impl.LinkLessonPageDecoration
import com.cyxbs.pages.course.view.decoration.impl.ScheduleAffairPageDecoration
import com.cyxbs.pages.course.view.decoration.impl.ScheduleAllDayPageDecoration
import com.cyxbs.pages.course.view.decoration.impl.ScheduleDeadlinePageDecoration
import com.cyxbs.pages.course.view.decoration.impl.ScheduleTodoTimedPageDecoration
import com.cyxbs.pages.course.view.decoration.impl.SelfLessonPageDecoration
import com.cyxbs.pages.course.view.item.extension.LocalCourseItemBottomSheetDialog
import com.cyxbs.pages.course.view.item.extension.rememberCourseItemBottomSheetDialogState
import com.g985892345.provider.api.annotation.ImplProvider

/**
 * 移动端主页课表框架
 *
 * 展开时：
 * 课表主体:     0.0 --------> 1.0
 * 课表头部:     0.0 -> 0.0 -> 1.0
 * 主界面头部:   1.0 -> 0.0 -> 0.0
 * 折叠时：
 * 课表主体:     1.0 --------> 0.0
 * 课表头部:     1.0 -> 0.0 -> 0.0
 * 主界面头部:   0.0 -> 0.0 -> 1.0
 *
 * @author 985892345
 * @date 2025/2/15
 */
@Stable
@ImplProvider(clazz = IMobileHomeCourseFrame::class)
class HomeCourseFrame : AbstractCourseFrame(), IMobileHomeCourseFrame {

  // 底部抽屉状态
  override val bottomSheetState by lazy {
    BottomSheetState()
  }

  val peekHeightState = mutableStateOf(70.dp)

  val bottomBarHeightState = mutableStateOf(0.dp)

  @Composable
  override fun HomeCourseContent(modifier: Modifier, bottomBarHeight: Dp) {
    val decorationManager = createCoursePageDecorationManager(this)
    CompositionLocalProvider(
      Local provides this,
      CoursePageDecorationManager.Local provides decorationManager,
    ) {
      MobileHomeCourseFrameContent(
        modifier = modifier,
        frame = this,
      )
    }
    SideEffect {
      bottomBarHeightState.value = bottomBarHeight
    }
  }
}

@Composable
private fun MobileHomeCourseFrameContent(
  modifier: Modifier,
  frame: HomeCourseFrame,
) {
  // item 点击后出现的 BottomSheetDialog
  val itemBottomSheetDialog = rememberCourseItemBottomSheetDialogState()
  CompositionLocalProvider(
    LocalCourseItemBottomSheetDialog provides itemBottomSheetDialog
  ) {
    MobileHomeBottomSheet(
      modifier = modifier.statusBarsPadding(),
      frame = frame,
      // 这里只传业务底导高度，父级未消费的系统导航栏由 BottomSheetCompose 自动补入。
      peekHeightExtra = frame.bottomBarHeightState.value,
      header = { MobileHomeCourseHeader(modifier = Modifier, frame = frame) },
    ) {
      HorizontalPager(
        modifier = Modifier.navigationBarsPadding().fillMaxSize().graphicsLayer {
          alpha = frame.bottomSheetState.expansionFraction
        },
        state = frame.pagerState,
        pageContent = { page ->
          frame.HomeCoursePageContent(
            page = page,
          )
        },
      )
    }
  }
}

@Composable
private fun createCoursePageDecorationManager(
  frame: AbstractCourseFrame
): CoursePageDecorationManager {
  val coroutineScope = rememberCoroutineScope()
  return remember {
    CoursePageDecorationManager(
      courseFrame = frame,
      courseCoroutineScope = coroutineScope,
      ScheduleDeadlinePageDecoration(
        courseFrame = frame,
        coroutineScope = coroutineScope,
        platformItemFactory = MobileScheduleItemFactory,
      ), // 截止时间点始终位于课表最上层
      CreateItemPageDecoration(
        courseFrame = frame,
        platformItemFactory = MobileCourseCreateItemFactory
      ), // 长按创建事务
      SelfLessonPageDecoration(
        platformItemFactory = MobileCourseLessonItemFactory
      ), // 自己的课程
      ScheduleTodoTimedPageDecoration(
        courseFrame = frame,
        coroutineScope = coroutineScope,
        platformItemFactory = MobileScheduleItemFactory,
      ), // 清单时间段独立位于事务上方
      ScheduleAffairPageDecoration(
        courseFrame = frame,
        coroutineScope = coroutineScope,
        platformItemFactory = MobileScheduleItemFactory,
      ), // Schedule 原生事务使用独立层级
      LinkLessonPageDecoration(
        platformItemFactory = MobileCourseLinkLessonItemFactory
      ), // 关联人的课程
      ScheduleAllDayPageDecoration(
        courseFrame = frame,
        coroutineScope = coroutineScope,
        platformItemFactory = MobileScheduleItemFactory,
      ), // 全天背景不参与重叠，固定放在最底层
    )
  }
}
