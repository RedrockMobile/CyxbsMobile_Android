package com.mredrock.cyxbs.lib.course.item.overlap

import com.mredrock.cyxbs.lib.course.fragment.course.base.FoldImpl
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapContainer

/**
 * 重叠相关的接口
 *
 * 使用了 [OverlapHelper] 来完成部分接口的默认实现
 *
 * ## 注意
 * - 如果你遇到了重新布局时只有 Item 出现卡顿的情况，可以看看 [IOverlap.lockRefreshAnim]
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/9 19:46
 */
interface IOverlap {
  
  /**
   * 是否想添加到父布局上
   *
   * ## 注意
   * - 这个回调是在把所有 Item 都添加完后的一个 Runnable 中调用的
   * - 你需要在这个回调判断是否能添加到父布局中
   * - 即使你返回 true，并不能保证就一定能添加进父布局中，因为有 item 拦截机制
   */
  fun isAddIntoParent(): Boolean
  
  /**
   * 刷新重叠
   *
   * ## 注意
   * - 只有添加进父布局的才可能会收到回调
   * - 在父布局添加或移除了 [IOverlapItem] 时回调
   * - 某个 [IOverlapItem] 的 visibility 发生改变时回调
   * - 手动调用 [IOverlapContainer.refreshOverlap] 时回调
   *
   * @param isAllowAnim 是否允许执行动画，因为动画会抑制布局，在改变 View 位置时会修改失败
   */
  fun refreshOverlap(isAllowAnim: Boolean)
  
  /**
   * 即将被移除时的回调
   *
   * 这里需要清除掉所有重叠的引用
   */
  fun clearOverlap()
  
  /**
   * ([row], [column]) 位置是否显示
   */
  fun isDisplayable(row: Int, column: Int): Boolean
  
  /**
   * 被重叠时的回调
   * @param row 自己 ([row], [column]) 的位置被重叠
   */
  fun onAboveItem(row: Int, column: Int, item: IOverlapItem?)
  
  /**
   * 重叠了其他 item 时的回调
   * @param row 自己 ([row], [column]) 的位置重叠了另一个 item
   */
  fun onBelowItem(row: Int, column: Int, item: IOverlapItem?)
  
  /**
   * 得到 ([row], [column]) 位置重叠在上面的 [IOverlapItem]
   */
  fun getAboveItem(row: Int, column: Int): IOverlapItem?
  
  /**
   * 得到重叠在自身区域上的所有 [IOverlapItem]
   * 注意：
   * - 没有传递获取父级的重叠。每个 item 每一格上最多只有一个，所以这个该方法返回的最大长度就是自身的格数
   */
  fun getAboveItems(): List<IOverlapItem>
  
  /**
   * 是否存在在自身区域上的 [IOverlapItem]
   */
  fun hasAboveItem(): Boolean
  
  /**
   * 得到 ([row], [column]) 位置重叠在下面的 item
   */
  fun getBelowItem(row: Int, column: Int): IOverlapItem?
  
  /**
   * 得到所有重叠在下面的 [IOverlapItem]
   * 注意：
   * - 没有传递获取子级的重叠
   */
  fun getBelowItems(): List<IOverlapItem>
  
  /**
   * 是否存在在自身区域下的 [IOverlapItem]
   */
  fun hasBelowItem(): Boolean
  
  /**
   * 是否锁定刷新动画，锁定后将不执行行数改变时的动画
   *
   * 因为刷新动画使用了 ChangeBounds，会导致布局被抑制，出现的效果就是突然的卡一下，
   * 与 [FoldImpl] 头注释上表述的原因相同，
   * 所以提供该方法用于暂时取消动画
   *
   * ## 注意
   * - 上锁次数与解锁次数相等时才能成功解锁
   * - 因为刷新是通过 post 到下一个 Runnable 中执行的，所以不能立即解锁，可以在使用 [IOverlapContainer.refreshOverlap] 时传入该 item
   *
   * @see IOverlapContainer.refreshOverlap 另一种解决方法
   */
  fun lockRefreshAnim()
  
  /**
   * 解锁刷新动画
   */
  fun unlockRefreshAnim()
}