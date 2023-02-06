package com.mredrock.cyxbs.lib.course.helper.affair

import com.mredrock.cyxbs.lib.course.helper.affair.expose.IBoundary
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler

/**
 * 处理创建事务的单根手指处理者
 *
 * @author 985892345
 * @date 2022/9/21 15:20
 */
internal interface ICreateAffairHandler : IPointerTouchHandler {
  
  /**
   * 是否正在使用中
   */
  fun isInUse(): Boolean
  
  /**
   * 是否正显示在课表中，是 [isInUse] 的一个子集
   */
  fun isInShow(): Boolean
  
  /**
   * 取消显示 [ITouchAffairItem]
   */
  fun cancelShow()
  
  /**
   * 设置 [ITouchAffairItem]
   */
  fun setTouchAffairItem(item: ITouchAffairItem?)
  
  companion object {
    
    /**
     * 工厂模式（虽然只有一个实现类）
     */
    fun getImpl(
      course: ICourseViewGroup,
      iTouch: ITouchCallback,
      iBoundary: IBoundary,
    ): ICreateAffairHandler {
      return CreateAffairHandler(course, iTouch, iBoundary)
    }
  }
}