package com.mredrock.cyxbs.lib.utils.utils

import androidx.core.content.edit
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * 学校日历的工具类
 *
 * 这里面可以得到开学的第一天
 *
 * 后端并没有单独提供某个接口来获取当前开学周数，每次的开学周数都是从课表接口那里得到的
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/15 14:59
 */
object SchoolCalendarUtil {
  
  private const val FIRST_MON_DAY = "first_day"
  
  /**
   * 得到这学期过去了多少天
   *
   * 返回 null，则说明不知道开学第一天是好久
   *
   * # 注意：存在返回负数的情况！！！
   */
  fun getDayOfTerm(): Int? {
    return checkFirstDay {
      val diff = System.currentTimeMillis() - mFirstMonDayCalendar.timeInMillis
      TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }
  }
  
  /**
   * 得到当前周数
   *
   * @return 返回 null，则说明不知道开学第一天是好久；返回 0，则表示还没有开学
   *
   * # 注意：存在返回负数的情况！！！
   */
  fun getWeekOfTerm(): Int? {
    return getDayOfTerm()?.div(7)?.plus(1)
  }
  
  /**
   * 是否是上学期（即秋季学期），否则是下学期（春季学期）
   */
  fun isFirstSemester() : Boolean {
    return Calendar.getInstance()[Calendar.MONTH + 1] > 8
  }
  
  /**
   * 得到开学第一周的星期一
   *
   * @return 返回 null，则说明不知道开学第一天是好久
   */
  fun getFirstMonDayOfTerm(): Calendar? {
    return checkFirstDay {
      mFirstMonDayCalendar.clone() as Calendar
    }
  }
  
  fun getFirstMonDayTimestamp(): Long? {
    return checkFirstDay {
      mFirstMonDayCalendar.timeInMillis
    }
  }
  
  /**
   * 更新开学时间
   * @param nowWeek 当前周数，支持负数
   */
  fun updateFirstCalendar(nowWeek: Int) {
    val calendar = Calendar.getInstance()
    calendar.add(
      Calendar.DATE,
      -((nowWeek - 1) * 7 + (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7)
    )
    /*
     * (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 的逻辑如下：
     * 星期天：1 -> 6
     * 星期一：2 -> 0
     * 星期二：3 -> 1
     * 星期三：4 -> 2
     * 星期四：5 -> 3
     * 星期五：6 -> 4
     * 星期六：7 -> 5
     *
     * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
     * 右边一栏是该数字距离周一的天数差
     *
     * 如果 nowWeek = 0，且今天是星期一：
     * (nowWeek - 1) * 7                                 = -7
     * (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7      = 0
     * 那么 合在一起就是 -(-7 + 0) = 7
     *
     * 再使用 add(Calendar.DATE, 7)
     *
     * 得到的就是开学第一周星期一的 calendar
     * */
    
    // 保证是绝对的第一天的开始
    calendar.apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    mFirstMonDayCalendar.timeInMillis = calendar.timeInMillis
    BaseApp.appContext.defaultSharedPreferences.edit {
      // 因为那边 lib_common 有类还需要使用这个，所以需要保存在 defaultSp 中
      putLong(FIRST_MON_DAY, calendar.timeInMillis)
    }
  }
  
  private var mFirstMonDayCalendar = Calendar.getInstance().apply {
    timeInMillis = BaseApp.appContext.defaultSharedPreferences.getLong(FIRST_MON_DAY, 0L)
  }
  
  /**
   * 检查 [mFirstMonDayCalendar] 是否正确
   *
   * 只有他第一次安装且没有网络时才会出现这个情况，只要之后加载了网络，都不会再出现问题
   */
  private inline fun <T> checkFirstDay(action: () -> T): T? {
    // 不知道第一天的时间戳，说明之前都没有登录过课表
    mFirstMonDayCalendar.timeInMillis = BaseApp.appContext.defaultSharedPreferences.getLong(FIRST_MON_DAY, 0L)
    if (mFirstMonDayCalendar.timeInMillis == 0L) return null
    mFirstMonDayCalendar.apply {
      // 保证是绝对的第一天的开始
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return action.invoke()
  }
}