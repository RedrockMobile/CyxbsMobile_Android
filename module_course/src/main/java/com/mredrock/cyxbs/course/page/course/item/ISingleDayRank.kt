package com.mredrock.cyxbs.course.page.course.item

import com.mredrock.cyxbs.api.course.utils.getNowTime
import com.mredrock.cyxbs.api.course.utils.getStartTimeMinute
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.lib.course.item.single.ISingleDayData

/**
 * 单列 item 的排序
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 17:26
 */
interface ISingleDayRank : ISingleDayData, IWeek {
  
  /**
   * 类型比较
   *
   * 越小越显示在上面
   */
  val rank: Int
  
  /**
   * 返回 1，则显示在上面
   */
  fun compareToInternal(other: ISingleDayRank): Int {
    if (this === other) return 0 // 如果是同一个对象直接返回 0
    return compareBy(other.week - week) { // 周数小的显示在上面
      val s1 = startNode
      val e1 = s1 + length
      val s2 = other.startNode
      val e2 = other.startNode + other.length
      return compareBy(weekNum - other.weekNum) {
        if (e1 < s2) -1 else if (s1 > e2) 1
        else {
          // 存在重叠的时候
          compareBy(other.rank - rank) {
            compareBy(length - other.length) { // 长度大的显示在上面，因为后面的项目开发实训课一般都是不能请假的
              if (s1 == s2 && e1 == e2) {
                compareBy(hashCode() - other.hashCode()) {
                  // 为了不得出 0 的结果使用 identityHashCode()
                  // 与 hashcode() 不同之处在于 identityHashCode() 返回不重写 hashcode() 时的值 (即对象的内存地址)
                  val code1 = System.identityHashCode(this)
                  val code2 = System.identityHashCode(other)
                  compareBy(code1 - code2) {
                    // 如果连 identityHashCode() 都相等，那我确实没得办法了
                    // 但这是不可能出现的情况，因为同一个对象已经在最开始就判断了
                    return 1
                  }
                }
              } else {
                // 长度相同，但起始位置不同，以当前时间来计算谁在谁上面
                compareBy(getNowTime() - getStartTimeMinute(maxOf(s1, s2))) {
                  -1
                }
              }
            }
          }
        }
      }
    }
  }
  
  companion object {
    inline fun compareBy(diff: Int, block: () -> Int): Int {
      return if (diff != 0) diff else block.invoke()
    }
  }
}