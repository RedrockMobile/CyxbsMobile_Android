package com.mredrock.cyxbs.course.page.course.item

import com.mredrock.cyxbs.api.course.utils.getEndTime
import com.mredrock.cyxbs.api.course.utils.getNowTime
import com.mredrock.cyxbs.lib.course.internal.day.ISingleDayItemData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 17:26
 */
interface IRank : ISingleDayItemData {
  
  /**
   * 类型比较
   *
   * 越小越显示在上面
   */
  val rank: Int
  
  fun compareToInternal(other: IRank): Int {
    if (this === other) return 0 // 如果是同一个对象直接返回 0
    return compareBy(weekNum - other.weekNum) {
      if (endRow < other.startRow) -1 else if (startRow > other.endRow) 1
      else {
        // 存在重叠的时候
        compareBy(other.rank - rank) {
          compareBy(length - other.length) { // 长度大的显示在上面，因为后面的项目开发实训课一般都是不能请假的
            if (startRow == other.startRow && endRow == other.endRow) {
              return hashCode() - other.hashCode() // 为了不得出 0 的结果使用 hashCode()
            } else {
              compareBy(getEndTime(endRow) - getNowTime()) {
                -1
              }
            }
          }
        }
      }
    }
  }
  
  companion object {
    inline fun compareBy(diff: Int, block: (diff: Int) -> Int): Int {
      return if (diff != 0) diff else block.invoke(diff)
    }
  }
}