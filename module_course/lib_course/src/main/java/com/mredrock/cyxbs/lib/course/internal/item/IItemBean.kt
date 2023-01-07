package com.mredrock.cyxbs.lib.course.internal.item

import com.ndhzs.netlayout.attrs.INetBean

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:42
 */
interface IItemBean : INetBean {
}

inline fun IItemBean.forEachColumn(crossinline block: (column: Int) -> Unit) {
  for (column in startColumn .. endColumn) {
    block.invoke(column)
  }
}

inline fun IItemBean.forEachRow(crossinline block: (row: Int) -> Unit) {
  for (row in startRow .. endRow) {
    block.invoke(row)
  }
}
