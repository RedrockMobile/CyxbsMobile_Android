package com.mredrock.cyxbs.lib.course.fragment.page.base

import com.mredrock.cyxbs.lib.course.fragment.page.period.ICoursePeriod
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:02
 */
abstract class PagePeriodImpl : PageFoldImpl(), ICoursePeriod {
  
  /**
   * 得到某节课开始前的高度值
   */
  final override fun getLessonStartHeight(num: Int): Int {
    require(num >= 1)
    return course.getRowsHeight(0, transformLessonToNode(num) - 1)
  }
  
  /**
   * 得到某节课结束时的高度值
   */
  final override fun getLessonEndHeight(num: Int): Int {
    require(num >= 1)
    return course.getRowsHeight(0, transformLessonToNode(num))
  }
  
  
  
  // 上午
  
  final override fun isIncludeAmPeriod(row: Int): Boolean {
    return row in AM_TOP .. AM_BOTTOM
  }
  
  final override fun forEachAm(block: (row: Int) -> Unit) {
    for (column in AM_TOP .. PM_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun getAmStartHeight(): Int {
    return course.getRowsHeight(0, AM_TOP - 1)
  }
  
  final override fun getAmEndHeight(): Int {
    return course.getRowsHeight(0, AM_BOTTOM)
  }
  
  // 中午
  
  final override fun isIncludeNoonPeriod(row: Int): Boolean {
    return row in NOON_TOP .. NOON_BOTTOM
  }
  
  final override fun forEachNoon(block: (row: Int) -> Unit) {
    for (column in NOON_TOP .. NOON_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun getNoonStartHeight(): Int {
    return course.getRowsHeight(0, NOON_TOP - 1)
  }
  
  final override fun getNoonEndHeight(): Int {
    return course.getRowsHeight(0, NOON_BOTTOM)
  }
  
  // 下午
  
  final override fun isIncludePmPeriod(row: Int): Boolean {
    return row in PM_TOP .. PM_BOTTOM
  }
  
  final override fun forEachPm(block: (row: Int) -> Unit) {
    for (column in PM_TOP .. PM_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun getPmStartHeight(): Int {
    return course.getRowsHeight(0, PM_TOP - 1)
  }
  
  final override fun getPmEndHeight(): Int {
    return course.getRowsHeight(0, PM_BOTTOM)
  }
  
  // 傍晚
  
  final override fun isIncludeDuskPeriod(row: Int): Boolean {
    return row in DUSK_TOP .. DUSK_BOTTOM
  }
  
  final override fun forEachDusk(block: (row: Int) -> Unit) {
    for (column in DUSK_TOP .. DUSK_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun getDuskStartHeight(): Int {
    return course.getRowsHeight(0, DUSK_TOP - 1)
  }
  
  final override fun getDuskEndHeight(): Int {
    return course.getRowsHeight(0, DUSK_BOTTOM)
  }
  
  // 晚上
  
  final override fun isIncludeNightPeriod(row: Int): Boolean {
    return row in NIGHT_TOP .. NIGHT_BOTTOM
  }
  
  final override fun forEachNight(block: (row: Int) -> Unit) {
    for (column in NIGHT_TOP .. NIGHT_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun getNightStartHeight(): Int {
    return course.getRowsHeight(0, NIGHT_TOP - 1)
  }
  
  final override fun getNightEndHeight(): Int {
    return course.getRowsHeight(0, NIGHT_BOTTOM)
  }
  
  companion object {
    /*
    * 为什么要这样区分开始行和结束行？
    * 原因如下：
    * 1、方便以后好维护
    * 2、虽然目前中午和傍晚只有一行，但也不能保证以后不改为两行，所以中午和傍晚也得分为开始行和结束行
    * 3、课的话也是如此，但因为有了中午和傍晚，所以课与行数不对应
    * 尽量不要自己写数字来固定
    * */
    
    // 上午
    private const val AM_TOP = 0
    private const val AM_BOTTOM = 3
  
    // 第一节课
    private const val LESSON_1_TOP = 0
    private const val LESSON_1_BOTTOM = 0
  
    // 第二节课
    private const val LESSON_2_TOP = 1
    private const val LESSON_2_BOTTOM = 1
  
    // 第三节课
    private const val LESSON_3_TOP = 2
    private const val LESSON_3_BOTTOM = 2
  
    // 第四节课
    private const val LESSON_4_TOP = 3
    private const val LESSON_4_BOTTOM = 3
  
  
    
    // 中午
    private const val NOON_TOP = 4 // 中午板块开始行
    private const val NOON_BOTTOM = 4 // 中午板块结束行
    
    
    
    // 下午
    private const val PM_TOP = 5 // 下午板块开始行
    private const val PM_BOTTOM = 8 // 下午板块结束行
  
    // 第五节课
    private const val LESSON_5_TOP = 5
    private const val LESSON_5_BOTTOM = 5
  
    // 第六节课
    private const val LESSON_6_TOP = 6
    private const val LESSON_6_BOTTOM = 6
  
    // 第七节课
    private const val LESSON_7_TOP = 7
    private const val LESSON_7_BOTTOM = 7
  
    // 第八节课
    private const val LESSON_8_TOP = 8
    private const val LESSON_8_BOTTOM = 8
    
    
    
    // 傍晚
    private const val DUSK_TOP = 9 // 傍晚板块开始行
    private const val DUSK_BOTTOM = 9 // 傍晚板块结束行
    
    
    
    // 晚上
    private const val NIGHT_TOP = 10 // 晚上板块开始行
    private const val NIGHT_BOTTOM = 13 // 晚上板块结束行
    
    // 第九节课
    private const val LESSON_9_TOP = 10
    private const val LESSON_9_BOTTOM = 10
  
    // 第十节课
    private const val LESSON_10_TOP = 11
    private const val LESSON_10_BOTTOM = 11
  
    // 第十一节课
    private const val LESSON_11_TOP = 12
    private const val LESSON_11_BOTTOM = 12
  
    // 第十二节课
    private const val LESSON_12_TOP = 13
    private const val LESSON_12_BOTTOM = 13
  
    
    
    
    private val CALENDAR = Calendar.getInstance()
  
    /**
     * 得到当前时间
     */
    fun getNowTime(): Int {
      CALENDAR.timeInMillis = System.currentTimeMillis()
      val hour = CALENDAR.get(Calendar.HOUR_OF_DAY)
      val minute = CALENDAR.get(Calendar.MINUTE)
      return hour * 60 + minute
    }
  
    /**
     * 得到当前行开始时间
     */
    fun getStartTime(row: Int): Int {
      return when (row) {
        0 -> 8 * 60
        1 -> 8 * 60 + 55
        2 -> 10 * 60 + 15
        3 -> 11 * 60 + 10
        4 -> 11 * 60 + 55
        5 -> 14 * 60
        6 -> 14 * 60 + 55
        7 -> 16 * 60 + 15
        8 -> 17 * 60 + 10
        9 -> 19 * 60
        10 -> 19 * 60 + 55
        11 -> 20 * 60 + 50
        12 -> 21 * 60 + 45
        13 -> 22 * 60 + 30
        else -> 0
      }
    }
  
    /**
     * 得到当前行结束时间
     */
    fun getEndTime(row: Int): Int {
      return when (row) {
        0 -> 8 * 60 + 45
        1 -> 9 * 60 + 40
        2 -> 11 * 60
        3 -> 11 * 60 + 55
        4 -> 14 * 60
        5 -> 14 * 60 + 45
        6 -> 15 * 60 + 40
        7 -> 17 * 60
        8 -> 17 * 60 + 55
        9 -> 19 * 60 + 45
        10 -> 20 * 60 + 40
        11 -> 21 * 60 + 35
        12 -> 22 * 60 + 30
        13 -> 24 * 60
        else -> 0
      }
    }
  
    /**
     * 转换课的节数为控件中对应的行数
     */
    fun transformLessonToNode(num: Int): Int {
      require(num in 1 .. 12)
      return when (num) {
        in 1 .. 4 -> num - 1
        in 5 .. 8 -> num
        in 9 .. 12 -> num + 1
        else -> error("不应该出现的错误，num = $num")
      }
    }
    //
    //    /**
    //     * 转换控件中对应的行数为课的节数
    //     *
    //     * @return -1 为 中午；-2 为 傍晚
    //     */
    //    fun transformNodeToLesson(node: Int): Int {
    //      require(node in 0 .. 13)
    //      return when (node) {
    //        4 -> -1
    //        9 -> -2
    //        in 0 .. 3 -> node + 1
    //        in 5 .. 8 -> node
    //        in 10 .. 13 -> node + 1
    //        else -> error("不应该出现的错误，node = $node")
    //      }
    //    }
  }
}