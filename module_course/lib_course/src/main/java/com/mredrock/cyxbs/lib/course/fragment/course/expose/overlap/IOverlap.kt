package com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap

/**
 * 重叠相关的接口
 *
 * 使用了 [OverlapHelper] 来完成部分接口的默认实现
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
   * 添加进父布局是否成功的回调
   */
  fun onAddIntoParentResult(isSuccess: Boolean)
  
  /**
   * 刷新重叠
   *
   * ## 注意
   * - 只有添加进父布局的才会收到回调
   * - 在父布局添加或移除了 [IOverlapItem] 和某个 [IOverlapItem] 的 visibility 发生改变时回调
   */
  fun refreshOverlap()
  
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
   * 得到 ([row], [column]) 位置重叠在下面的 item
   */
  fun getBelowItem(row: Int, column: Int): IOverlapItem?
  
  /**
   * 得到所有重叠在下面的 [IOverlapItem]
   * 注意：
   * - 没有传递获取子级的重叠
   */
  fun getBelowItems(): List<IOverlapItem>
}