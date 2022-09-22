package com.mredrock.cyxbs.common.utils

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 学校日历工具类
 * Created by cc on 15/8/23.
 */
@Deprecated("使用 lib_utils 中的 SchoolCalendarUtil 进行替换")
open class SchoolCalendar {
    var firstDay: Calendar = GregorianCalendar(2015, Calendar.SEPTEMBER, 7)
    var calendar: Calendar
    
    @Deprecated("过时方法，之后不会在做兼容，请使用静态方法代替")
    constructor() {
        // 鄙人认为，在这个时候，我们有必要去更新一下firstDay
        val first = BaseApp.appContext.defaultSharedPreferences.getLong(FIRST_MON_DAY, firstDay.timeInMillis)
        firstDay.timeZone = TimeZone.getTimeZone("GMT+8:00")
        firstDay.timeInMillis = first
        calendar = GregorianCalendar()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
    }
    
    @Deprecated("过时方法，之后不会在做兼容，请使用静态方法代替")
    constructor(week: Int, weekDay: Int) : this() {
        var mWeekDay = weekDay
        calendar = firstDay
        calendar.add(Calendar.DATE, (week - 1) * 7)
        mWeekDay = if (mWeekDay == 0) 7 else mWeekDay
        calendar.add(Calendar.DATE, mWeekDay - dayOfWeek)
    }
    
    /**
     * 取这学期过去了多少天
     *
     * @return days 过去的天数
     */
    val dayOfTerm: Int
        get() {
            var days = getDeltaT(calendar, firstDay)
            if (days >= 0) {
                days++
            }
            return days
        }
    
    /**
     * 取当前第几周
     *
     * @return weeks 当前的周数
     */
    val weekOfTerm: Int
        get() {
            val currentTime2 = calendar.clone() as Calendar
            var beWeekDay = firstDay[Calendar.DAY_OF_WEEK]
            beWeekDay = if (beWeekDay == 1) 8 else beWeekDay
            var enWeekDay = currentTime2[Calendar.DAY_OF_WEEK]
            enWeekDay = if (enWeekDay == 1) 8 else enWeekDay
            currentTime2.add(Calendar.DATE, beWeekDay - enWeekDay)
            var weeks = getDeltaT(currentTime2, firstDay) / 7
            if (weeks >= 0) {
                weeks++
            }
            return weeks
        }
    
    private fun getDeltaT(end: Calendar, begin: Calendar): Int {
        val mBegin = begin.clone() as Calendar
        mBegin[Calendar.HOUR_OF_DAY] = 0
        mBegin[Calendar.MINUTE] = 0
        mBegin[Calendar.SECOND] = 0
        mBegin[Calendar.MILLISECOND] = 0
        val mEnd = end.clone() as Calendar
        mEnd[Calendar.HOUR_OF_DAY] = 0
        mEnd[Calendar.MINUTE] = 0
        mEnd[Calendar.SECOND] = 0
        mEnd[Calendar.MILLISECOND] = 0
        return ((mEnd.timeInMillis - mBegin.timeInMillis) / (1000 * 86400)).toInt()
    }
    
    /**
     * 日期加减
     *
     * @param day 增加的日子
     */
    fun addDay(day: Int): SchoolCalendar {
        calendar.add(Calendar.DATE, day)
        return this
    }
    
    /**
     * 格式化输出日期
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString 待格式化文本
     */
    fun getString(formatString: String?): String {
        val format = SimpleDateFormat(formatString, Locale.CHINA)
        return format.format(calendar.time)
    }
    
    /**
     * 格式化解析日期文本
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString 格式化的文本
     */
    fun parse(formatString: String?, content: String?): SchoolCalendar? {
        val format = SimpleDateFormat(formatString, Locale.CHINA)
        return try {
            calendar.time = format.parse(content)
            this
        } catch (e: ParseException) {
            null
        }
    }
    
    fun setDate(year: Int, month: Int, day: Int): SchoolCalendar {
        calendar[year, month] = day
        return this
    }
    
    val date: Date
        get() = calendar.time
    
    val dayOfWeek: Int
        get() {
            val weekDay = calendar[Calendar.DAY_OF_WEEK]
            return if (weekDay == 1) {
                7
            } else {
                weekDay - 1
            }
        }
    
    val day: Int
        get() = calendar[Calendar.DATE]
    
    val month: Int
        get() = calendar[Calendar.MONTH] + 1
    
    val year: Int
        get() = calendar[Calendar.YEAR]
    
    override fun equals(other: Any?): Boolean {
        return if (other is SchoolCalendar) {
            dayOfTerm == other.dayOfTerm
        } else false
    }
    
    override fun hashCode(): Int {
        var result = firstDay.hashCode()
        result = 31 * result + calendar.hashCode()
        return result
    }
    
    companion object {
        private const val FIRST_MON_DAY = "first_day"
        
        /*
        * TODO 目前该类已经移植到 lib_config 中的 SchoolCalendarUtils 中
        *  所以不再提供 set 方法，只提供 get 方法用于兼容旧模块
        * */
        
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
         * @return 返回 null，则说明不知道开学第一天是好久；返回 0，则表示开学前的一周（因为第一周开学）
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
}