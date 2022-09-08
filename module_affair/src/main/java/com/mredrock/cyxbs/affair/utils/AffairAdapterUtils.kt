package com.mredrock.cyxbs.affair.utils

import com.mredrock.cyxbs.affair.ui.adapter.data.AffairAdapterData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeAdd
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
object AffairDataUtils {

  /**
   * Time的第一个要换行
   */
  fun getNewList(affairList: List<AffairAdapterData>): List<AffairAdapterData> {
    val list = sort(affairList)
    val newList = arrayListOf<AffairAdapterData>()
    var index = 0
    while (index < list.size) {
      val prev = newList.lastOrNull()
      //val next = list.getOrNull(index + 1)
      when (val now = list[index]) {
        is AffairWeekData -> {
          newList.add(now)
        }
        is AffairTimeData -> {
          if (prev is AffairWeekData) {
            // 这个时候 isWrapBefore 该为 true
            newList.add(if (now.isWrapBefore) now else now.copy(isWrapBefore = true))
          } else {
            // 这个时候 isWrapBefore 该为 false
            newList.add(if (!now.isWrapBefore) now else now.copy(isWrapBefore = false))
          }

        }
        is AffairTimeAdd -> {
        }
      }
      index++
    }
    // 最后一个是加号
    newList.add(AffairTimeAdd(1))
    return newList
  }

  // 获取有哪几周
  fun getAffairWeekData(list: List<AffairAdapterData>): List<Int> {
    val newList = arrayListOf<Int>()
    list.forEach { if (it is AffairWeekData) newList.add(it.week) }
    return newList
  }

  // 将数据按照先AffairWeekData后AffairTimeData排序
  private fun sort(list: List<AffairAdapterData>): List<AffairAdapterData> {
    val newList = arrayListOf<AffairAdapterData>()
    val weekList = arrayListOf<AffairWeekData>()
    val timeList = arrayListOf<AffairTimeData>()
    list.forEach {
      if (it is AffairWeekData) weekList.add(it) else if (it is AffairTimeData) timeList.add(
        it
      )
    }
    newList.addAll(weekList)
    newList.addAll(timeList)
    return newList
  }
}