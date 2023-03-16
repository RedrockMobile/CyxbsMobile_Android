package com.mredrock.cyxbs.course.page.course.ui.home.utils

import android.view.View
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.toAffair
import com.mredrock.cyxbs.course.page.course.item.affair.Affair
import com.mredrock.cyxbs.course.page.course.item.affair.IMovableAffairManager
import com.mredrock.cyxbs.course.page.course.ui.home.HomeSemesterFragment
import com.mredrock.cyxbs.course.page.course.ui.home.HomeWeekFragment
import com.mredrock.cyxbs.course.page.course.ui.home.IHomePageFragment
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.LocationUtil
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * 管理课表事务的长按移动后更新的逻辑
 *
 * 因为课表采取观察者模式，如果直接更新数据库，会导致课表直接更新布局，
 * 但是此时可能正在执行最后的移动动画，更新布局会导致动画出问题，
 *
 * 所以该类在长按开始时就取消课表对数据的观察，然后更新数据库中的数据，最后在动画结束时重新开启观察流
 *
 * @author 985892345
 * 2023/2/23 13:09
 */
class MovableAffairManager(
  val fragment: IHomePageFragment,
) : IMovableAffairManager {
  
  private var mCount = 0
  
  override fun isMovableToNewLocation(
    page: ICoursePage,
    affair: Affair,
    child: View,
    newLocation: LocationUtil.Location,
    data: AffairData
  ): Boolean {
    return when (fragment) {
      is HomeSemesterFragment -> false // 整学期课表中的事务不允许移动
      is HomeWeekFragment -> {
        val viewModel = fragment.parentViewModel
        val homePageResultMap = viewModel.homeWeekData.value ?: return false
        
        var result = 0
        outer@ for (entry in homePageResultMap) {
          // 遍历所有事务数据，从中寻找拥有相同 id 的 affair 有几个
          for (it in entry.value.affair) {
            if (it.onlyId == data.onlyId) {
              result++
            }
            if (result > 1) {
              // 如果找到的结果大于 1 就跳出循环
              break@outer
            }
          }
        }
        
        if (result == 1) {
          // 目前因为事务存在重复的情况，所以只有当相同的事务只有一个的时候才能移动到新位置
          return true
        } else {
          toast("暂不支持移动重复事务")
          return false
        }
      }
    }
  }
  
  override fun onLongPressStart(
    page: ICoursePage,
    affair: Affair,
    child: View,
    data: AffairData
  ) {
    incCount()
  }
  
  override fun onOverAnimStart(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    affair: Affair,
    child: View,
    data: AffairData
  ) {
    if (newLocation != null) {
      val newData = data.copy(
        hashDay = newLocation.startColumn - 1,
        beginLesson = getBeginLesson(newLocation.startRow),
      )
      // 修改差分刷新时比对的数据
      fragment.affairContainerProxy.replaceDataFromOldList(data, newData)
  
      incCount()
      IAffairService::class.impl
        .updateAffair(newData.toAffair())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnTerminate {
          decCount()
        }.unsafeSubscribeBy()
    }
  }
  
  override fun onOverAnimEnd(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    affair: Affair,
    child: View,
    data: AffairData
  ) {
    decCount()
  }
  
  private fun incCount() {
    if (mCount == 0) {
      // 取消课表数据的观察流
      fragment.parentViewModel.cancelDataObserve()
    }
    mCount++
  }
  
  private fun decCount() {
    mCount--
    if (mCount == 0) {
      // 在移动结束后才重新开启课表数据的观察流
      fragment.parentViewModel.refreshDataObserve()
    }
  }
}