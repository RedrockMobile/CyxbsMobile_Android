package com.mredrock.cyxbs.lib.course.fragment.page

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import java.util.*

/**
 * 每一周的页面模板
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:27
 */
abstract class CourseWeekFragment : CoursePageFragment() {
  
  /**
   * 建议写法：
   * ```
   * companion object {
   *   fun newInstance(week: Int): XXXWeekFragment {
   *     return XXXWeekFragment().apply {
   *       arguments = bundleOf(
   *         "mWeek" to week
   *       )
   *     }
   *   }
   * }
   *
   * override val mWeek by arguments<Int>()
   * ```
   */
  protected abstract val mWeek: Int
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onIsInThisWeek(setWeekNum())
  }
  
  /**
   * 设置星期数
   * @return 今天是否在本周
   */
  protected open fun setWeekNum(): Boolean? {
    val calendar = SchoolCalendarUtil.getFirstMonDayOfTerm()
    return if (calendar != null) {
      calendar.add(Calendar.DATE, (mWeek - 1) * 7)
      val startTimestamp = calendar.timeInMillis
      setMonth(calendar)
      calendar.add(Calendar.DATE, 7)
      System.currentTimeMillis() in startTimestamp .. calendar.timeInMillis
    } else null
  }
  
  protected open val mCourseNowTimeHelper by lazyUnlock {
    CourseNowTimeHelper.attach(this)
  }
  
  /**
   * 今天是否在本周内的回调
   * @param boolean 今天是否处于本周内，如果为 null，则未知 (一般是第一次打开掌邮没有请求课表数据时才会出现)
   */
  protected open fun onIsInThisWeek(boolean: Boolean?) {
    if (boolean == null) return
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
}