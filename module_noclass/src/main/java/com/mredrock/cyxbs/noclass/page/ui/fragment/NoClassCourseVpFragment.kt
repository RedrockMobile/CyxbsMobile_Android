package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import kotlin.math.max

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.fragment
 * @ClassName:      NoClassCourseVpFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 01:02:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约查询分组没课的VP
 */

class NoClassCourseVpFragment : AbstractHeaderCourseVpFragment() {
  
  companion object {
    fun newInstance(data: HashMap<Int, NoClassSpareTime>): NoClassCourseVpFragment {
      return NoClassCourseVpFragment().apply {
        arguments = bundleOf(
          this::mNoClassData.name to data,
        )
      }
    }
  }
  
  private val mNoClassData : HashMap<Int, NoClassSpareTime> by arguments()

  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) NoClassSemesterFragment.newInstance(mNoClassData[0]!!) else NoClassWeekFragment.newInstance(position,mNoClassData[position]!!)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViewPager()
  }
  
  private fun initViewPager(){
    mViewPager.setCurrentItem(
      if (mNowWeek >= mVpAdapter.itemCount) 0 else max(mNowWeek, 0),
      false
    )
  }
}