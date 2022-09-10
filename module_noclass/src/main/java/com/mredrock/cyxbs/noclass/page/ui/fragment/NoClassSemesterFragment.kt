package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.mredrock.cyxbs.lib.course.helper.CourseNowTimeHelper
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.viewmodel.NoClassViewModel
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
class NoClassSemesterFragment : NoClassPageFragment() {
  
  private val mViewModel by activityViewModels<NoClassViewModel>()
  companion object {
    fun newInstance(data: NoClassSpareTime): NoClassSemesterFragment {
      return NoClassSemesterFragment().apply {
        arguments = bundleOf(
          this::mNoClassSpareTime.name to data
        )
      }
    }
  }
  
  private val mNoClassSpareTime by arguments<NoClassSpareTime>()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initToday()
    initTimeline()
    initObserve()
    addLessons(mNoClassSpareTime)
  }
  
  private fun initObserve(){
    mViewModel.noclassData.observe(viewLifecycleOwner){
      val mNoClassSpareTime = it[0]!!
      clearLesson()
      addLessons(mNoClassSpareTime)
    }
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