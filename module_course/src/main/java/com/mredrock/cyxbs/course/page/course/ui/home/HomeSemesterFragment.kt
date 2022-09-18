package com.mredrock.cyxbs.course.page.course.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import androidx.core.view.children
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.course.page.course.base.CompareWeekSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.course.page.course.utils.container.AffairContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.LinkLessonContainerProxy
import com.mredrock.cyxbs.course.page.course.utils.container.SelfLessonContainerProxy
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.utils.extensions.anim
import com.ndhzs.netlayout.child.OnChildExistListener

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/20 19:25
 */
class HomeSemesterFragment : CompareWeekSemesterFragment() {
  
  private val mParentViewModel by createViewModelLazy(
    HomeCourseViewModel::class,
    { requireParentFragment().viewModelStore }
  )
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      // 如果是被异常重启，则不执行动画
      initEntrance()
    }
    initObserve()
  }
  
  private fun initEntrance() {
    // 先判断周数
    if (mParentViewModel.nowWeek == 0) {
      val viewList = arrayListOf<View>()
      // 把所有添加进来的 View 都修改透明度为 0
      viewList.addAll(course.getIterable().onEach { it.alpha = 0F })
      val viewListener = object : OnChildExistListener {
        override fun onChildViewAdded(parent: ViewGroup, child: View) {
          child.alpha = 0F
          viewList.add(child)
        }
        override fun onChildViewRemoved(parent: ViewGroup, child: View) {
          child.alpha = 1F
          viewList.remove(child)
          parent.children
        }
      }
      course.addChildExistListener(viewListener)
      // 观察 BottomSheet 滑动的值
      mParentViewModel.courseService
        .bottomSheetSlideOffset
        .observe(
          viewLifecycleOwner,
          object : Observer<Float> {
            override fun onChanged(t: Float) {
              if (t == 1F) {
                // 当为 1 时取消观察
                mParentViewModel.courseService.bottomSheetSlideOffset.removeObserver(this)
                // 设置入场动画
                course.setLayoutAnimation(LayoutAnimationController(R.anim.course_anim_entrance.anim, 0.1F))
                course.startLayoutAnimation() // 现在已经布局了，需要手动开启动画执行
                course.postOnAnimation {
                  // 在下一帧回调，因为动画是在下一帧执行的，这个时候动画已经开始执行，可以移除动画防止第二次运行
                  viewList.forEach { it.alpha = 1F }
                  course.setLayoutAnimation(null) // 执行完后移除动画
                  course.removeChildExitListener(viewListener) // 移除上面设置的监听
                }
              }
            }
          }
        )
    }
  }
  
  private var mIsNeedStartLinkLessonEntranceAnim: Boolean? = null
  
  private val mSelfLessonContainerProxy = SelfLessonContainerProxy(this)
  private val mLinkLessonContainerProxy = LinkLessonContainerProxy(this)
  private val mAffairContainerProxy = AffairContainerProxy(this)
  
  private fun initObserve() {
    mParentViewModel.showLinkEvent.collectLaunch {
      mIsNeedStartLinkLessonEntranceAnim = it
    }
    
    mParentViewModel.homeWeekData
      .observe { map ->
        val self = map.mapValues { it.value.self }.mapToMinWeek()
        val link = map.mapValues { it.value.link }.mapToMinWeek()
        val affair = map.values.map { it.affair }.flatten()
        mSelfLessonContainerProxy.diffRefresh(self)
        mAffairContainerProxy.diffRefresh(affair)
        mLinkLessonContainerProxy.diffRefresh(link) {
          if (mIsNeedStartLinkLessonEntranceAnim == true) {
            // 这时说明触发了关联人的显示，需要实现入场动画
            // 使用 mIsNeedStartLinkLessonEntranceAnim 很巧妙的避开了 Fragment 重建数据倒灌的问题
            mLinkLessonContainerProxy.startEntranceAnim()
          }
        }
      }
  }
}