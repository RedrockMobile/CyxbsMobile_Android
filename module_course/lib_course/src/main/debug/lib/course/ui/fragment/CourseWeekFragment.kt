package lib.course.ui.fragment

import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseTimelineHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import kotlinx.coroutines.flow.map
import lib.course.item.SelfLessonItem
import lib.course.ui.viewmodel.VpViewModel
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:36
 */
class CourseWeekFragment : CoursePageFragment() {
  
  companion object {
    fun newInstance(week: Int): CourseWeekFragment {
      return CourseWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  private val mWeek by arguments.helper<Int>()
  
  private val viewModel by createViewModelLazy(
    VpViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun initCourse() {
    setWeekNum()
    initObserve()
  }
  
  /**
   * 设置星期数
   */
  private fun setWeekNum() {
    val calendar = SchoolCalendarUtil.getFirstMonDayOfTerm()
    if (calendar != null) {
      calendar.add(Calendar.DATE, (mWeek - 1) * 7)
      val startTimestamp = calendar.timeInMillis
      mTvMonth.text = "${calendar.get(Calendar.MONTH) + 1}月"
      forEachWeek { _, month ->
        month.text = calendar.get(Calendar.DATE).toString()
        calendar.add(Calendar.DATE, 1) // 天数加 1
      }
      onIsInThisWeek(System.currentTimeMillis() in startTimestamp .. calendar.timeInMillis)
    } else {
      onIsInThisWeek(false)
    }
  }
  
  private val mCourseTimelineHelper by lazyUnlock {
    CourseTimelineHelper.attach(this)
  }
  
  /**
   * 今天是否在本周内的回调
   */
  private fun onIsInThisWeek(boolean: Boolean) {
    mCourseTimelineHelper.setVisible(boolean)
  }
  
  private fun initObserve() {
    viewModel.selfLessons
      .map { it[mWeek] ?: emptyList() }
      .collectLaunch {
        addLesson(it.map { data -> SelfLessonItem(data) })
      }
  }
}