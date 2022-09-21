package com.mredrock.cyxbs.lib.course.fragment.course.base

import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.ICoursePeriod

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:02
 */
abstract class PeriodImpl : OverlapImpl(), ICoursePeriod {
  
  /**
   * 得到某节课开始前的高度值
   *
   * 如果需要得到中午和傍晚，请使用 [getNoonStartHeight]、[getDuskStartHeight]
   */
  final override fun getLessonStartHeight(num: Int): Int {
    require(num >= 1)
    return course.getRowsHeight(0, transformLessonToNode(num) - 1)
  }
  
  /**
   * 得到某节课结束时的高度值
   *
   * 如果需要得到中午和傍晚，请使用 [getNoonEndHeight]、[getDuskEndHeight]
   */
  final override fun getLessonEndHeight(num: Int): Int {
    require(num >= 1)
    return course.getRowsHeight(0, transformLessonToNode(num))
  }
  
  
  
  // 上午
  
  final override fun compareAmPeriod(row: Int): Int {
    return if (row < AM_TOP) -1 else if (row > AM_BOTTOM) 1 else 0
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
  
  final override fun compareNoonPeriod(row: Int): Int {
    return if (row < NOON_TOP) -1 else if (row > NOON_BOTTOM) 1 else 0
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
  
  final override fun comparePmPeriod(row: Int): Int {
    return if (row < PM_TOP) -1 else if (row > PM_BOTTOM) 1 else 0
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
  
  final override fun compareDuskPeriod(row: Int): Int {
    return if (row < DUSK_TOP) -1 else if (row > DUSK_BOTTOM) 1 else 0
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
  
  final override fun compareNightPeriod(row: Int): Int {
    return if (row < NIGHT_TOP) -1 else if (row > NIGHT_BOTTOM) 1 else 0
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
    *
    * 为什么设置成 private ?
    * 因为不建议直接用数字去操作，因为这个是对应课的 row 数，更推荐使用其他方法来代替直接获取细节的操作
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
  
  
    /**
     * 转换课的节数为控件中对应的行数
     */
    fun transformLessonToNode(num: Int): Int {
      return when (num) {
        in 1 .. 4 -> num - 1
        in 5 .. 8 -> num
        in 9 .. 12 -> num + 1
        else -> error("不存在 num = $num 的课")
      }
    }
  }
}