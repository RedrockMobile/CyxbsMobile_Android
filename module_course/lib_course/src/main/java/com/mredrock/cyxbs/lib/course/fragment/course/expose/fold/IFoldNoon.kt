package com.mredrock.cyxbs.lib.course.fragment.course.expose.fold

import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.noon.INoonPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:00
 */
interface IFoldNoon : INoonPeriod {
  
  /**
   * 折叠状态下显示的 View
   */
  val viewNoonFold: View
  
  /**
   * 展开状态下显示的 View
   */
  val viewNoonUnfold: View
  
  /**
   * 得到当前中午那一行的状态
   */
  fun getNoonRowState(): FoldState
  
  /**
   * 带有动画的强制折叠中午时间段。会 cancel 掉之前不同的动画，如果是想同的动画则忽略调用
   * @param duration 传入负数，则自动计算所需动画时间
   * @return 如果返回 null 则说明该次调用被抛弃
   */
  fun foldNoon(duration: Long = -1L): IFoldAnimation?
  
  /**
   * 不带动画的立即折叠中午时间段。会 cancel 掉之前的动画
   */
  fun foldNoonWithoutAnim()
  
  /**
   * 带有动画的强制展开中午时间段。会 cancel 掉之前不同的动画，如果是想同的动画则忽略调用
   * @param duration 传入负数，则自动计算所需动画时间
   * @return 如果返回 null 则说明该次调用被抛弃
   */
  fun unfoldNoon(duration: Long = -1L): IFoldAnimation?
  
  /**
   * 不带动画的立即展开中午时间段。会 cancel 掉之前的动画
   */
  fun unfoldNoonWithoutAnim()
  
  /**
   * 增加折叠监听
   */
  fun addNoonFoldListener(l: OnFoldListener)
  
  /**
   * 锁定折叠中午的操作，锁定后，将不允许折叠
   *
   * ## 注意：
   * - 这个只能限制折叠中午相关方法的调用，但你仍然可以使用其他方式来改变行比重
   * - 上锁次数与解锁次数相等时才能成功解锁
   * - 并不打算提供锁定展开操作，一般情况下是不需要不允许展开的
   *
   * @see [unlockFoldNoon]
   */
  fun lockFoldNoon()
  
  /**
   * 解锁折叠中午的操作
   *
   * @see [lockFoldNoon]
   */
  fun unlockFoldNoon()
}