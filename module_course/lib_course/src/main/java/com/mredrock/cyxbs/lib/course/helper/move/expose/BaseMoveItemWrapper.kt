package com.mredrock.cyxbs.lib.course.helper.move.expose

import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil

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
    
    val parent = item.parent
    // 禁止父布局拦截
    parent.getParent().requestDisallowInterceptTouchEvent(true)
    VibratorUtil.start(36) // 长按触发来个震动提醒
    
    item.cancelShowView()
    
    val moveView = item.createMovableView()
  }
  
  protected open fun getMovableViewLayoutParams(
    itemLp: ItemLayoutParams
  ): ItemLayoutParams {
    return ItemLayoutParams(itemLp).apply { changeAll(itemLp) }
  }
}