package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.course.page.course.ui.home.base.HomeCourseVpLinkFragment
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.service.CourseServiceImpl
import com.mredrock.cyxbs.lib.course.fragment.page.CoursePageFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 16:53
 */
class HomeCourseVpFragment : HomeCourseVpLinkFragment() {
  
  private val mViewModel by viewModels<HomeCourseViewModel>()
  
  override val mNowWeek: Int
    get() = SchoolCalendarUtil.getWeekOfTerm() ?: 0 // 当前周数
  
  override var mPageCount: Int = 22 // 21 周加上第一页为整学期的课表
  
  private val mCourseService = ICourseService::class.impl as CourseServiceImpl
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initTouch()
    initViewPager()
    initObserve()
  }
  
  override fun createFragment(position: Int): CoursePageFragment {
    return if (position == 0) HomeSemesterFragment() else HomeWeekFragment.newInstance(position)
  }
  
  private fun initTouch() {
    mIvLink.setOnSingleClickListener {
      val isShowingDouble = isShowingDoubleLesson() ?: return@setOnSingleClickListener
      if (isShowingDouble) {
        showSingleLink()
        mViewModel.changeLinkStuVisible(false)
      } else {
        showDoubleLink()
        mViewModel.changeLinkStuVisible(true)
      }
    }
    
    mTvWhichWeek.setOnLongClickListener {
      mViewModel.refreshData()
      true
    }
  }
  
  private fun initViewPager() {
    // 初次加载时移到对应的周数
    // 这里课表的翻页不建议带有动画，因为数据过多会较卡
    mViewPager.setCurrentItem(if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek, false)
  }
  
  private fun initObserve() {
    mViewModel.linkStu.observe {
      if (it.isNull()) {
        mIvLink.gone()
      } else {
        if (it.isShowLink) {
          showDoubleLink()
        } else {
          showSingleLink()
        }
      }
    }
    mViewModel.refreshEvent.collectLaunch {
      if (it) {
        toast("刷新成功！")
      } else {
        toast("刷新失败！")
      }
    }
    
    mCourseService.headerAlphaState.observe {
      mHeader.alpha = it
    }
    mCourseService.courseVpAlphaState.observe {
      mViewPager.alpha = it
    }
  }
}