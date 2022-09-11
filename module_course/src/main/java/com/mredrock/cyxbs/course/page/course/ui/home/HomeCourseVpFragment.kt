package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.course.ui.home.base.BaseHomeCourseVpFragment
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.utils.SchoolCalendarUtil

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 16:53
 */
class HomeCourseVpFragment : BaseHomeCourseVpFragment() {
  
  private val mViewModel by viewModels<HomeCourseViewModel>()
  
  override val mNowWeek: Int
    get() = SchoolCalendarUtil.getWeekOfTerm() ?: 0 // 当前周数
  
  override var mPageCount: Int = 22 // 21 周加上第一页为整学期的课表
  
  override val mViewPager by R.id.course_vp_fragment_home.view<ViewPager2>()
  
  // SmartRefreshLayout 刷新
//  private val mRefreshLayout by R.id.course_srl_fragment_home.view<SmartRefreshLayout>()
  
  
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.course_fragment_home_course_show, container, false)
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initTouch()
    initRefresh()
    initViewPager()
    initObserve()
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
    mBtnBackNowWeek.setOnSingleClickListener {
      // 回到本周，如果周数大于等于了总的 itemCount，则显示整学期界面
      mViewPager.currentItem = if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek
    }
  }
  
  private fun initRefresh() {
//    mRefreshLayout.setEnableHeaderTranslationContent(false)
//    mRefreshLayout.setHeaderHeight(0F)
//    mRefreshLayout.setFooterTriggerRate(2F)
//    mRefreshLayout.setOnLoadMoreListener {
//      mViewModel.refreshData()
//    }
  }
  
  private fun initViewPager() {
    // 初次加载时移到对应的周数
    // 这里课表的翻页不建议带有动画，因为数据过多会较卡
//    mViewPager.setCurrentItem(if (mNowWeek >= mVpAdapter.itemCount) 0 else mNowWeek, false)
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
//      mRefreshLayout.finishLoadMore()
      if (it) {
        toast("刷新成功！")
      } else {
        toast("刷新失败！")
      }
    }
  }
}