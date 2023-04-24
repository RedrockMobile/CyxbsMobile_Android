package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.distinctUntilChanged
import com.mredrock.cyxbs.course.page.course.data.LessonData
import com.mredrock.cyxbs.course.page.course.ui.home.utils.AffairManager
import com.mredrock.cyxbs.course.page.course.ui.home.utils.PageFragmentHelper
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.page.CourseSemesterFragment
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeSemesterFragment : CourseSemesterFragment(), IHomePageFragment {
  
  override val week: Int
    get() = 0
  
  override val parentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  // 大部分的初始化方法在这里面
  private val mPageFragmentHelper = PageFragmentHelper<HomeSemesterFragment>()
  
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
  
  override val selfLessonContainerProxy = SelfLessonContainerProxy(this)
  override val linkLessonContainerProxy = LinkLessonContainerProxy(this)
  override val affairContainerProxy = AffairContainerProxy(this, AffairManager(this))
  
  private fun initObserve() {
    parentViewModel.showLinkEvent
      .collectLaunch {
        mIsShowLinkEventAfterClick = it
      }
    
    parentViewModel.homeWeekData
      .distinctUntilChanged()
      .observe { map ->
        val self = map.mapValues { it.value.self }.mapToMinWeek()
        val link = map.mapValues { it.value.link }.mapToMinWeek()
        val affair = map.values.map { it.affair }.flatten()
        affairContainerProxy.diffRefresh(affair)
        selfLessonContainerProxy.diffRefresh(self)
        linkLessonContainerProxy.diffRefresh(link) {
          if (mIsShowLinkEventAfterClick == true
            && parentViewModel.currentItem.value == 0
            && link.isNotEmpty()
          ) {
            // 这时说明触发了关联人的显示，需要开启入场动画
            linkLessonContainerProxy.startAnimation()
          }
        }
      }
  }
  
  override fun isHideNoLessonImg(item: IItem): Boolean {
    return super.isHideNoLessonImg(item) || item is ITouchAffairItem
  }
  
  
  
  
  
  
  
  
  /**
   * 筛选掉重复的课程，只留下最小周数的课程
   */
  private fun <T : LessonData> Map<Int, List<T>>.mapToMinWeek(): List<T> {
    val map = hashMapOf<LessonData.Course, T>()
    forEach { entry ->
      entry.value.forEach {
        val value = map[it.course]
        if (value == null || value.week > it.week) {
          map[it.course] = it
        }
      }
    }
    return map.map { it.value }
  }
}