package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import com.mredrock.cyxbs.noclass.page.course.NoClassLesson
import com.mredrock.cyxbs.noclass.page.course.NoClassLessonData
import java.util.*

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.fragment
 * @ClassName:      NoClassWeekFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 10:26:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassWeekFragment : CoursePageFragment(){
  
  
  companion object {
    fun newInstance(week: Int): NoClassWeekFragment {
      return NoClassWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  private val mWeek by arguments<Int>()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setWeekNum()
    initObserve()
    addLesson(NoClassLesson(NoClassLessonData(3,3,3)))
  }
  
  /**
   * 设置星期数
   */
  private fun setWeekNum() {
    val calendar = SchoolCalendarUtil.getFirstMonDayOfTerm()
    if (calendar != null) {
      calendar.add(Calendar.DATE, (mWeek - 1) * 7)
      val startTimestamp = calendar.timeInMillis
      setMonth(calendar)
      calendar.add(Calendar.DATE, 7)
      onIsInThisWeek(System.currentTimeMillis() in startTimestamp .. calendar.timeInMillis)
    } else {
      onIsInThisWeek(false)
    }
  }
  
  private val mCourseNowTimeHelper by lazy {
    CourseNowTimeHelper.attach(this)
  }
  
  /**
   * 今天是否在本周内的回调
   */
  private fun onIsInThisWeek(boolean: Boolean) {
    mCourseNowTimeHelper.setVisible(boolean)
    if (boolean) {
      val calendar = Calendar.getInstance()
      val weekNum = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
      /*
      * 星期天：1 -> 7
      * 星期一：2 -> 1
      * 星期二：3 -> 2
      * 星期三：4 -> 3
      * 星期四：5 -> 4
      * 星期五：6 -> 5
      * 星期六：7 -> 6
      *
      * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
      * 右边一栏是 weekNum 对应的数字
      * */
      showToday(weekNum)
    }
  }
  
  private fun initObserve() {

  }
  
  
}