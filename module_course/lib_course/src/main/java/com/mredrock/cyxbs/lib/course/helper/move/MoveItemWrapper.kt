package com.mredrock.cyxbs.lib.course.helper.move

import com.mredrock.cyxbs.lib.course.helper.move.expose.BaseMoveItemWrapper
import com.mredrock.cyxbs.lib.course.helper.move.expose.IMovableItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/7 14:27
 */
open class MoveItemWrapper(val item: IMovableItem) : BaseMoveItemWrapper(item) {
  
  override fun onLongPressStart() {
  
  }
}