package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.page.CourseWeekFragment
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeWeekFragment : CourseWeekFragment() {
  
  companion object {
    fun newInstance(week: Int): HomeWeekFragment {
      return HomeWeekFragment().apply {
        arguments = bundleOf(
          this::mWeek.name to week
        )
      }
    }
  }
  
  override val mWeek by arguments<Int>()
  
  // 供外部调用
  val week: Int
   get() = mWeek
  
  val parentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  // 大部分的初始化方法在这里面
  private val mPageFragmentHelper = PageFragmentHelper()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // 因为 HomeSemesterFragment 与 HomeWeekFragment 很多逻辑一样，所以统一放在这里面初始化
    mPageFragmentHelper.init(this, savedInstanceState)
    initObserve()
  }
  
  /**
   * 是否显示关联人，但需要点击一次关联人图标，才会激活该变量
   * 初始值为 null 是表示没有点击过关联人图标
   */
  private var mIsShowLinkEventAfterClick: Boolean? = null
  
  private val mSelfLessonContainerProxy = SelfLessonContainerProxy(this)
  private val mLinkLessonContainerProxy = LinkLessonContainerProxy(this)
  private val mAffairContainerProxy = AffairContainerProxy(this)
  
  private fun initObserve() {
    parentViewModel.showLinkEvent
      .collectLaunch {
        mIsShowLinkEventAfterClick = it
      }
    
    parentViewModel.homeWeekData
      .map { it[mWeek] ?: HomeCourseViewModel.HomePageResult }
      .distinctUntilChanged()
      .observe {
        mAffairContainerProxy.diffRefresh(it.affair)
        mSelfLessonContainerProxy.diffRefresh(it.self)
        mLinkLessonContainerProxy.diffRefresh(it.link) { data ->
          if (mIsShowLinkEventAfterClick == true && parentViewModel.currentItem == mWeek && data.isNotEmpty()) {
            // 这时说明触发了关联人的显示，需要开启入场动画
            mLinkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  override fun isHideNoLessonImg(item: IItem): Boolean {
    return super.isHideNoLessonImg(item) || item is ITouchAffairItem
  }
}