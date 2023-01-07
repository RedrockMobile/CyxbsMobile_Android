package com.mredrock.cyxbs.lib.course.helper.affair

import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler

/**
 * 处理创建事务的单根手指处理者
 *
 * @author 985892345
 * @date 2022/9/21 15:20
 */
interface ICreateAffairHandler : IPointerTouchHandler {
  
  /**
   * 是否正在使用中
   */
  fun isInUse(): Boolean
  
  /**
   * 是否正显示在课表中，是 [isInUse] 的一个子集
   */
  fun isInShow(): Boolean
  
  /**
   * 取消显示 [ITouchAffair]
   */
  fun cancelShow()
  
  /**
   * 创建事务收到的触摸事件回调
   */
  interface TouchCallback {
    fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {}
  
    /**
     * @param initialRow 初始行
     * @param initialColumn 初始列
     * @param topRow 与上边界比较后的实际值
     * @param bottomRow 与下界比较后的实际值
     * @param touchRow 当前触摸的值
     */
    fun onMove(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      topRow: Int,
      bottomRow: Int,
      touchRow: Int
    ) {}
    
    fun onEnd(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      topRow: Int,
      bottomRow: Int,
      touchRow: Int
    ) {}
  }
  
  companion object {
    
    /**
     * 工厂模式（虽然只有一个实现类）
     */
    fun getImpl(
      course: ICourseViewGroup,
      affair: ITouchAffair,
      iTouch: TouchCallback
    ): ICreateAffairHandler {
      return CreateAffairHandler(course, affair, iTouch)
    }
  }
}