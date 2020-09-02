package com.mredrock.cyxbs.common.utils

import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 学校日历工具类
 * Created by cc on 15/8/23.
 */
open class SchoolCalendar {
    var firstDay: Calendar = GregorianCalendar(2015, Calendar.SEPTEMBER, 7)
    var calendar: Calendar

    constructor() {
        // 鄙人认为，在这个时候，我们有必要去更新一下firstDay
        val first = context.defaultSharedPreferences.getLong(FIRST_DAY, firstDay.timeInMillis)
        firstDay.timeZone = TimeZone.getTimeZone("GMT+8:00")
        firstDay.timeInMillis = first
        calendar = GregorianCalendar()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
    }

    constructor(date: Date?) : this() {
        calendar.time = date
    }

    constructor(timestamp: Long) : this(Date(timestamp * 1000))
    constructor(year: Int, month: Int, day: Int) {
        calendar = GregorianCalendar(year, month, day)
    }

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
        const val FIRST_DAY = "first_day"
    }
}