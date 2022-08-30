package com.mredrock.cyxbs.course.page.course.ui.home

import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseTimelineHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeWeekFragment : CoursePageFragment() {
  
  companion object {
    fun newInstance(week: Int): HomeWeekFragment {
      return HomeWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  private val mWeek by arguments.helper<Int>()
  
  override fun initCourse() {
    super.initCourseInternal()
    setWeekNum()
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
}