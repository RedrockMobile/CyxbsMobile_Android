package com.mredrock.cyxbs.affair.ui.adapter.data

import com.mredrock.cyxbs.affair.room.AffairEntity
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.utils.getEndRow
import com.mredrock.cyxbs.api.course.utils.getStartRow

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/12 21:12
 */
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
interface AffairAdapterData {
  abstract override fun equals(other: Any?): Boolean
  val onlyId: Any // 唯一 id，用于比对 item 是否发生位置改变
}

/**
 * 用来显示周数的数据类
 */
data class AffairWeekData(
  val week: Int,
) : AffairAdapterData {
  override val onlyId: Any
    get() = week
  
  fun getWeekStr(): String {
    return WEEK_ARRAY[week]
  }
  
  companion object {
    /**
     * 这里面的个数跟 [ICourseService.maxWeek] 挂钩
     */
    val WEEK_ARRAY = arrayOf(
      "整学期", "第一周", "第二周", "第三周", "第四周", "第五周",
      "第六周", "第七周", "第八周", "第九周", "第十周", "第十一周",
      "第十二周", "第十三周", "第十四周", "第十五周", "第十六周",
      "第十七周", "第十八周", "第十九周", "第二十周", "第二十一周",
    )
  }
}

/**
 * 用来显示时间的数据类
 */
data class AffairTimeData(
  val weekNum: Int, // 星期几，星期一 为 0
  val beginLesson: Int, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
  val period: Int, // 长度
  val isWrapBefore: Boolean = false // 用于判断是否换行
) : AffairAdapterData {
  override val onlyId: Any
    get() = weekNum * 10000 + beginLesson * 100 + period
  
  fun getTimeStr(): String {
    val startRow = getStartRow(beginLesson)
    val endRow = getEndRow(beginLesson, period)
    return if (startRow == endRow) {
      "${DAY_ARRAY[weekNum]} ${LESSON_ARRAY[getStartRow(beginLesson)]}"
    } else {
      "${DAY_ARRAY[weekNum]} ${LESSON_ARRAY[getStartRow(beginLesson)]}-${LESSON_ARRAY[getEndRow(beginLesson, period)]}"
    }
  }
  
  companion object {
    val LESSON_ARRAY = arrayOf(
      "第一节课", //8:00
      "第二节课", //8:55
      "第三节课", //10:15
      "第四节课", //11:10
      "中午", //12:00
      "第五节课", //14:00
      "第六节课", //14:55
      "第七节课", //16:15
      "第八节课", //17:10
      "傍晚", //18:00
      "第九节课", //19:00
      "第十节课", //19:55
      "第十一节课", //20:50
      "第十二节课", //21:45
    )
    val DAY_ARRAY = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
  }
}

/**
 * 用来给事务显示加号的数据类
 */
object AffairTimeAdd : AffairAdapterData {
  override fun equals(other: Any?): Boolean = other is AffairTimeAdd
  override val onlyId: Any
    get() = javaClass.hashCode()
}

// 将展示的数据转换为要上传的数据
fun List<AffairAdapterData>.toAtWhatTime(): List<AffairEntity.AtWhatTime> {
  val newList = arrayListOf<AffairEntity.AtWhatTime>()
  val weekList = arrayListOf<Int>()
  val timeList = arrayListOf<AffairTimeData>()
  forEach {
    when (it) {
      is AffairWeekData -> {
        weekList.add(it.week)
      }
      is AffairTimeData -> {
        timeList.add(it)
      }
      is AffairTimeAdd -> {}
    }
  }
  timeList.forEach {
    newList.add(AffairEntity.AtWhatTime(it.beginLesson, it.weekNum, it.period, weekList))
  }
  return newList
}