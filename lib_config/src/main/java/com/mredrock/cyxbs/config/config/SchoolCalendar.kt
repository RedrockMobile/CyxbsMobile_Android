package com.mredrock.cyxbs.config.config

import androidx.core.content.edit
import com.mredrock.cyxbs.config.sp.defaultSp
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
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
object SchoolCalendar {
  
  /**
   * 得到这学期过去了多少天
   *
   * 返回 null，则说明不知道开学第一天是好久
   *
   * # 注意：存在返回负数的情况！！！
   */
  fun getDayOfTerm(): Int? {
    return mBehaviorSubject.value?.run {
      val diff = System.currentTimeMillis() - timeInMillis
      TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }
  }
  
  /**
   * 观察这学期过去了多少天
   *
   * ## 注意
   * - Activity 和 Fragment 中使用一般需要切换线程
   * ```
   * observeOn(AndroidSchedulers.mainThread())
   * ```
   */
  fun observeDayOfTerm(): Observable<Int> {
    return mBehaviorSubject
      .distinctUntilChanged()
      .map {
        val diff = System.currentTimeMillis() - it.timeInMillis
        TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
      }
  }
  
  /**
   * 得到当前周数
   *
   * @return 返回 null，则说明不知道开学第一天是好久；返回 0，则表示开学前的一周（因为第一周开学）
   *
   * # 注意：存在返回负数的情况！！！
   * ```
   *     -1      0      1      2       3        4             返回值
   *  ----------------------------------------------------------->
   * -14     -7      0      7      14       21       28       天数差
   * ```
   */
  fun getWeekOfTerm(): Int? {
    val dayOfTerm = getDayOfTerm() ?: return null
    return if (dayOfTerm >= 0) dayOfTerm / 7 + 1 else dayOfTerm / 7
  }
  
  /**
   * 观察当前周数
   *
   * ## 注意
   * - Activity 和 Fragment 中使用一般需要切换线程
   * ```
   * observeOn(AndroidSchedulers.mainThread())
   * ```
   */
  fun observeWeekOfTerm(): Observable<Int> {
    return observeDayOfTerm()
      .map { if (it >= 0) it / 7 + 1 else it / 7 }
  }
  
  /**
   * 得到开学第一周的星期一
   *
   * @return 返回 null，则说明不知道开学第一天是好久
   */
  fun getFirstMonDayOfTerm(): Calendar? {
    return mBehaviorSubject.value?.clone() as Calendar?
  }
  
  /**
   * 观察开学第一周的星期一
   *
   * ## 注意
   * - Activity 和 Fragment 中使用一般需要切换线程
   * ```
   * observeOn(AndroidSchedulers.mainThread())
   * ```
   */
  fun observeFirstMonDayOfTerm(): Observable<Calendar> {
    return mBehaviorSubject
      .distinctUntilChanged()
      .map { it.clone() as Calendar }
  }
  
  /**
   * 得到开学第一周的星期一的时间戳
   */
  fun getFirstMonDayTimestamp(): Long? {
    return mBehaviorSubject.value?.timeInMillis
  }
  
  /**
   * 观察开学第一周的星期一的时间戳
   *
   * ## 注意
   * - Activity 和 Fragment 中使用一般需要切换线程
   * ```
   * observeOn(AndroidSchedulers.mainThread())
   * ```
   */
  fun observeFirstMonDayTimestamp(): Observable<Long> {
    return mBehaviorSubject
      .distinctUntilChanged()
      .map { it.timeInMillis }
  }
  
  // Rxjava-Subject 教程 https://www.jianshu.com/p/d7efc29ec9d3
  // BehaviorSubject 与 LiveData 一致，订阅时会发送最后一次数据
  private val mBehaviorSubject = BehaviorSubject.create<Calendar>()
    .apply {
      val oldTimestamp = defaultSp.getLong(FIRST_MON_DAY, 0L)
      if (oldTimestamp != 0L) {
        val newCalendar = Calendar.getInstance().apply {
          timeInMillis = oldTimestamp
          // 保证是绝对的第一天的开始
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
        onNext(newCalendar)
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
    defaultSp.edit {
      // 因为那边 lib_common 有类还需要使用这个，所以需要保存在 defaultSp 中
      putLong(FIRST_MON_DAY, calendar.timeInMillis)
    }
    mBehaviorSubject.onNext(calendar)
  }
  
  private const val FIRST_MON_DAY = "first_day" // 这个与 lib_common 包中的 SchoolCalendar 保持一致
}