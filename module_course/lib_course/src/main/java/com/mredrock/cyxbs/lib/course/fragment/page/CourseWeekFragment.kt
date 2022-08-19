package com.mredrock.cyxbs.lib.course.fragment.page

import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.helper.CourseTimelineHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import java.util.*

/**
 * 每一周的 Fragment
 *
 * 生成实例时必须要传入当前的周数
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 20:54
 */
open class CourseWeekFragment @Deprecated(
  "使用 newInstance() 代替",
  ReplaceWith("CourseWeekFragment.newInstance()")
) constructor() : CoursePageFragmentImpl() {
  
  companion object {
    
    /**
     * @param week 显示的第几周
     * @param startTimestamp 第一天的时间戳
     */
    @Suppress("DEPRECATION")
    fun newInstance(
      week: Int,
      startTimestamp: Long
    ): CourseWeekFragment {
      return CourseWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week,
          this::mStartTimestamp.name to startTimestamp
        )
      }
    }
  }
  
  private val mWeek by arguments.helper<Int>()
  private val mStartTimestamp by arguments.helper<Long>()
  
  override fun initCourse() {
    super.initCourse()
    setWeekNum()
  }
  
  /**
   * 设置星期数
   */
  protected open fun setWeekNum() {
    val calendar = Calendar.getInstance()
    val nowTime = calendar.timeInMillis
    calendar.apply {
      timeInMillis = mStartTimestamp
      // 保证是绝对的第一天的开始
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    mTvMonth.text = "${calendar.get(Calendar.MONTH) + 1}月"
    forEachWeek { _, month ->
      month.text = calendar.get(Calendar.DATE).toString()
      calendar.add(Calendar.DATE, 1) // 天数加 1
    }
    // 这里用现在的时间戳与第一天开始和 7 天结束做比较
    onIsInThisWeek(nowTime in mStartTimestamp .. calendar.timeInMillis)
  }
  
  protected val mCourseTimelineHelper by lazyUnlock {
    CourseTimelineHelper(mCourseLayout).also { mCourseLayout.addItemDecoration(it) }
  }
  
  /**
   * 今天是否在本周内的回调
   */
  protected open fun onIsInThisWeek(boolean: Boolean) {
    mCourseTimelineHelper.setVisible(boolean)
  }
}