package com.mredrock.cyxbs.lib.course.fragment.page

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.helper.show.CourseNowTimeHelper
import java.util.*

/**
 * 整学期页面模板
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:26
 */
abstract class CourseSemesterFragment : CoursePageFragment() {
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initToday()
  }
  
  protected open fun initToday() {
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
  
  override fun initTimeline() {
    super.initTimeline()
    CourseNowTimeHelper.attach(this)
  }
}