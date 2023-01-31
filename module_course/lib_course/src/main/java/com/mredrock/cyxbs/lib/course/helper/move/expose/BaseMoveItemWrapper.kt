package com.mredrock.cyxbs.lib.course.helper.move.expose

import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/7 14:31
 */
abstract class BaseMoveItemWrapper(private val item: IMovableItem) {
  
  protected var mIsInLongPress = false
    private set
  
  open fun onLongPressStart() {
    mIsInLongPress = true
  }
  
  protected open fun getMovableViewLayoutParams(
    itemLp: ItemLayoutParams
  ): ItemLayoutParams {
    return ItemLayoutParams(itemLp).apply { changeAll(itemLp) }
  }
}