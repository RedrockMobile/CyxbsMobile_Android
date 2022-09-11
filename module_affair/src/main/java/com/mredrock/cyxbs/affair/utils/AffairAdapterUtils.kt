package com.mredrock.cyxbs.affair.utils

import com.mredrock.cyxbs.affair.service.AffairEntity
import com.mredrock.cyxbs.affair.ui.adapter.data.*
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
object AffairDataUtils {

  /**
   * Time的第一个要换行,建议所有的submitList都用这个方法处理一下
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

  /**
   * 添加时间(会检查参数的合法性)
   */
  fun addNewTime(
    affairList: List<AffairAdapterData>,
    timeData: AffairTimeData
  ): List<AffairAdapterData> {
    val newList = affairList.toMutableList()
    var isLegal = false
    affairList.forEach {
      if (it is AffairTimeData) {
        if (funCheckTime(it, timeData)) isLegal = true
      }
    }
    if (isLegal) {
      newList.add(AffairTimeData(timeData.weekNum, timeData.beginLesson, timeData.period))
    }
    return getNewList(newList)
  }

  //判断加入时间的合法性
  private fun funCheckTime(oldTimeData: AffairTimeData, newTimeData: AffairTimeData): Boolean {
    if (newTimeData.period == 0) {
      "掌友,起始时间和结束时间不能一样哦".toast()
      return false
    }
    return if (oldTimeData.weekNum == newTimeData.weekNum) {
      if (oldTimeData.beginLesson >= newTimeData.beginLesson + newTimeData.period || oldTimeData.beginLesson + oldTimeData.period <= newTimeData.beginLesson) {
        true
      } else {
        "掌友,该时间段已经添加了呦".toast()
        false
      }
    } else {
      true
    }

  }

  // 将展示的数据转换为要上传的数据
  fun affairAdapterDataToAtWhatTime(affairList: List<AffairAdapterData>): List<AffairEntity.AtWhatTime> {
    val newList = arrayListOf<AffairEntity.AtWhatTime>()
    val weekList = arrayListOf<Int>()
    val timeList = arrayListOf<AffairTimeData>()
    affairList.forEach {
      if (it is AffairWeekData) weekList.add(it.week) else if (it is AffairTimeData) timeList.add(
        it
      )
    }
    timeList.forEach {
      newList.add(AffairEntity.AtWhatTime(it.beginLesson, it.weekNum, it.period, weekList))
    }
    return newList
  }

  // 将数据库的类转化为要展示的类
  fun affairEntityToAffairAdapterData(affairEntity: AffairEntity): List<AffairAdapterData> {
    val newList = arrayListOf<AffairAdapterData>()
    val affairList = affairEntity.atWhatTime
    affairList[0].week.forEach { newList.add(AffairWeekData(it, listOf())) }
    affairList.forEach { newList.add(AffairTimeData(it.day, it.beginLesson, it.period)) }
    return newList
  }

  /**
   * 判断整学期选择的逻辑
   */
  fun checkWeekSelectData(weekList: List<AffairWeekSelectData>): List<AffairWeekSelectData> {
    val newList = arrayListOf<AffairWeekSelectData>()
    // 整学期选择后,其他不能选择
    if (weekList[0].isChoice) {
      newList.add(AffairWeekSelectData(0, true))
      for (i in 1 until weekList.size) {
        newList.add(AffairWeekSelectData(weekList[i].week, false))
      }
    } else {
      // 反之,其他周数选择后,整学期不能选择
      newList.add(AffairWeekSelectData(0, false))
      for (i in 1 until weekList.size) {
        newList.add(AffairWeekSelectData(weekList[i].week, weekList[i].isChoice))
      }
    }
    return newList
  }
}