package com.mredrock.cyxbs.lib.course.view.course.base

import android.content.Context
import android.util.AttributeSet
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.column.ITimeLine
import com.mredrock.cyxbs.lib.course.helper.CourseTimelineHelper
import com.mredrock.cyxbs.lib.course.item.IItem
import com.mredrock.cyxbs.lib.course.period.ColumnInclude
import java.util.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 15:48
 */
abstract class CourseTimeLineImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : CourseFoldImpl(context, attrs, defStyleAttr, defStyleRes), ITimeLine {
  
  final override fun isIncludeTimeLine(item: IItem): ColumnInclude {
    val sColumn = isIncludeTimeLine(item.startColumn)
    val eColumn = isIncludeTimeLine(item.endColumn)
    return if (sColumn) {
      if (eColumn) ColumnInclude.CONTAIN_ITEM else ColumnInclude.INTERSECT_LEFT
    } else {
      if (eColumn) ColumnInclude.INTERSECT_RIGHT else ColumnInclude.CONTAIN_PERIOD
    }
  }
  
  final override fun isIncludeTimeLine(row: Int): Boolean {
    return row in TIME_LINE_LEFT .. TIME_LINE_RIGHT
  }
  
  final override fun forEachTimeLine(block: (column: Int) -> Unit) {
    for (column in TIME_LINE_LEFT .. TIME_LINE_RIGHT) {
      block.invoke(column)
    }
  }
  
  final override fun setIsShowNowTimeLine(boolean: Boolean) {
    mCourseTimelineHelper.setVisible(boolean)
  }
  
  override fun getTimeLineStartWidth(): Int {
    return getColumnsWidth(0, TIME_LINE_LEFT - 1)
  }
  
  override fun getTimeLineEndWidth(): Int {
    return getColumnsWidth(0, TIME_LINE_RIGHT)
  }
  
  
  
  final override fun isIncludeAmPeriod(row: Int): Boolean {
    return row in LESSON_1_TOP .. LESSON_4_BOTTOM
  }
  
  final override fun isIncludeNoonPeriod(row: Int): Boolean {
    return row in NOON_TOP .. NOON_BOTTOM
  }
  
  final override fun isIncludePmPeriod(row: Int): Boolean {
    return row in LESSON_5_TOP .. LESSON_9_BOTTOM
  }
  
  final override fun isIncludeDuskPeriod(row: Int): Boolean {
    return row in DUSK_TOP .. DUSK_BOTTOM
  }
  
  final override fun isIncludeNightPeriod(row: Int): Boolean {
    return row in LESSON_10_TOP .. LESSON_12_BOTTOM
  }
  
  
  
  final override fun forEachAm(block: (row: Int) -> Unit) {
    for (column in LESSON_1_TOP .. LESSON_4_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun forEachNoon(block: (row: Int) -> Unit) {
    for (column in NOON_TOP .. NOON_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun forEachPm(block: (row: Int) -> Unit) {
    for (column in LESSON_5_TOP .. LESSON_9_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun forEachDusk(block: (row: Int) -> Unit) {
    for (column in DUSK_TOP .. DUSK_BOTTOM) {
      block.invoke(column)
    }
  }
  
  final override fun forEachNight(block: (row: Int) -> Unit) {
    for (column in LESSON_10_TOP .. LESSON_12_BOTTOM) {
      block.invoke(column)
    }
  }
  
  override fun getLessonStartHeight(num: Int): Int {
    require(num >= 1)
    return getRowsHeight(0, transformLessonToNode(num) - 1)
  }
  
  override fun getLessonEndHeight(num: Int): Int {
    require(num >= 1)
    return getRowsHeight(0, transformLessonToNode(num))
  }
  
  override fun getNoonStartHeight(): Int {
    return getRowsHeight(0, NOON_TOP - 1)
  }
  
  override fun getNoonEndHeight(): Int {
    return getRowsHeight(0, NOON_BOTTOM)
  }
  
  override fun getDuskStartHeight(): Int {
    return getRowsHeight(0, DUSK_TOP - 1)
  }
  
  override fun getDuskEndHeight(): Int {
    return getRowsHeight(0, DUSK_BOTTOM)
  }
  
  @Suppress("LeakingThis")
  private val mCourseTimelineHelper = CourseTimelineHelper(this)
  
  init {
    // 添加时间线的绘制
    addItemDecoration(mCourseTimelineHelper)
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
    private const val AM_TOP = 0 // 上午板块开始行
    private const val AM_BOTTOM = 3 // 上午板块结束行
    private const val NOON_TOP = 4 // 中午板块开始行
    private const val NOON_BOTTOM = 4 // 中午板块结束行
    private const val PM_TOP = 5 // 下午板块开始行
    private const val PM_BOTTOM = 8 // 下午板块结束行
    private const val DUSK_TOP = 9 // 傍晚板块开始行
    private const val DUSK_BOTTOM = 9 // 傍晚板块结束行
    private const val NIGHT_TOP = 10 // 晚上板块开始行
    private const val NIGHT_BOTTOM = 13 // 晚上板块结束行
  
    private const val TIME_LINE_LEFT = 0 // 时间轴开始列
    private const val TIME_LINE_RIGHT = 0 // 时间轴结束列
  
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
      val hour = CALENDAR.get(Calendar.HOUR)
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
     *
     * @param num -1 为 中午；-2 为 傍晚
     */
    fun transformLessonToNode(num: Int): Int {
      require(num in 1 .. 12 || num == -1 || num == -2)
      return when (num) {
        -1 -> 4
        -2 -> 9
        in 1 .. 4 -> num - 1
        in 5 .. 8 -> num
        in 9 .. 12 -> num + 1
        else -> error("不应该出现的错误，num = $num")
      }
    }
  
    /**
     * 转换控件中对应的行数为课的节数
     *
     * @return -1 为 中午；-2 为 傍晚
     */
    fun transformNodeToLesson(node: Int): Int {
      require(node in 0 .. 13)
      return when (node) {
        4 -> -1
        9 -> -2
        in 0 .. 3 -> node + 1
        in 5 .. 8 -> node
        in 10 .. 13 -> node + 1
        else -> error("不应该出现的错误，node = $node")
      }
    }
  }
}