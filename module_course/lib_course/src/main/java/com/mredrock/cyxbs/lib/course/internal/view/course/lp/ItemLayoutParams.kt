package com.mredrock.cyxbs.lib.course.internal.view.course.lp

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import com.mredrock.cyxbs.lib.course.internal.item.IItemBean
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:36
 */
open class ItemLayoutParams : NetLayoutParams, IItemBean {
  
  constructor(
    c: Context,
    attrs: AttributeSet
  ) : super(c, attrs)
  
  constructor(
    data: IItemBean,
    width: Int = MATCH_PARENT,
    height: Int = MATCH_PARENT,
    gravity: Int = Gravity.CENTER,
  ) : super(data, width, height, gravity)
  
  constructor(
    startRow: Int,
    endRow: Int,
    startColumn: Int,
    endColumn: Int,
    width: Int = MATCH_PARENT,
    height: Int = MATCH_PARENT,
    gravity: Int = Gravity.CENTER,
  ) : super(startRow, endRow, startColumn, endColumn, width, height, gravity)
}