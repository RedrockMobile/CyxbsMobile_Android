package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import java.util.*

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.fragment
 * @ClassName:      NoClassSemesterFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 10:25:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约显示整个学期的view
 */
class NoClassSemesterFragment : CoursePageFragment() {
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initToday()
    initTimeline()
  }
  
  
  private fun initToday(){
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
  
  private fun initTimeline() {
    CourseNowTimeHelper.attach(this)
  }


}