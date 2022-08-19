package com.mredrock.cyxbs.lib.course.internal.item

import com.ndhzs.netlayout.attrs.INetBean

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:42
 */
interface IItemData : INetBean {
  
  fun forEachColumn(block: (column: Int) -> Unit) {
    for (column in startColumn .. endColumn) {
      block.invoke(column)
    }
  }
  
  fun forEachRow(block: (row: Int) -> Unit) {
    for (row in startRow .. endRow) {
      block.invoke(row)
    }
  }
}