package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.course.fragment.vp.AbstractHeaderCourseVpFragment
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil
import com.mredrock.cyxbs.noclass.R

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

  override val mPageCount: Int
    get() = 22

  override fun createFragment(position: Int): CoursePageFragment {

    return if (position == 0) NoClassSemesterFragment() else NoClassWeekFragment.newInstance(position)
  }

  override val mViewPager: ViewPager2 by R.id.noclass_vp_fragment_course.view()
  
  override val mNowWeek: Int
    get() = SchoolCalendarUtil.getWeekOfTerm() ?: 0
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) : View = inflater.inflate(R.layout.noclass_layout_course, container, false)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initTouch()
    initViewPager()
  }
  
  private fun initTouch(){
    mBtnBackNowWeek.setOnSingleClickListener {
      // 回到本周，如果周数大于等于了总的 itemCount，则显示整学期界面
      mViewPager.currentItem = if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek
    }
  }
  
  private fun initViewPager(){
    mViewPager.setCurrentItem(if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek, false)
  }
  
}