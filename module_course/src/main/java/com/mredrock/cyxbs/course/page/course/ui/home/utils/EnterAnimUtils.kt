package com.mredrock.cyxbs.course.page.course.ui.home.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.course.page.course.ui.home.viewmodel.HomeCourseViewModel
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.utils.extensions.anim
import com.ndhzs.netlayout.child.OnChildExistListener

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/21 17:28
 */
object EnterAnimUtils {
  
  fun startEnterAnim(
    course: ICourseViewGroup,
    viewModel: HomeCourseViewModel,
    viewLifecycleOwner: LifecycleOwner
  ) {
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
    viewModel.courseService
      .bottomSheetSlideOffset
      .observe(
        viewLifecycleOwner,
        object : Observer<Float> {
          override fun onChanged(t: Float) {
            if (t > 0.8F) {
              // 当为 1 时取消观察
              viewModel.courseService.bottomSheetSlideOffset.removeObserver(this)
              // 设置入场动画
              course.setLayoutAnimation(
                LayoutAnimationController(
                  R.anim.course_anim_entrance.anim,
                  0.1F
                )
              )
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