package com.mredrock.cyxbs.widget.util

import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.config.SP_WIDGET_NEED_FRESH
import com.mredrock.cyxbs.common.config.WIDGET_COURSE
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.widget.bean.CourseStatus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zia on 2018/10/10.
 * 精力憔悴，这些方法直接揉在一起了
 */

const val EmptyCourseObject = -1

/**
 * 获得今天得课程list信息
 */
fun getTodayCourse(context: Context): List<CourseStatus.Course>? {
    return getCourseByCalendar(context, Calendar.getInstance())
}

fun getCourseByCalendar(context: Context, calendar: Calendar): ArrayList<CourseStatus.Course>? {
    val json = context.defaultSharedPreferences.getString(WIDGET_COURSE, "")
    val course = Gson().fromJson<CourseStatus>(json, CourseStatus::class.java) ?: return null
    if (course.data == null) return null

    val needFresh = context.defaultSharedPreferences.getBoolean(SP_WIDGET_NEED_FRESH, true)
    //计算当前周数
    var beginTime = context.defaultSharedPreferences.getLong(beginTimeShareName, 0L)
    //需要刷新当前周
    if (needFresh) {
        beginTime = saveBeginTime(context, course.nowWeek)
        context.defaultSharedPreferences.editor {
            putBoolean(SP_WIDGET_NEED_FRESH, false)
            putString(WIDGET_COURSE, json)
        }
    }
    val week = TimeUtil.calcWeekOffset(Date(beginTime), calendar.time)

    /*
    * 转换表，老外从周日开始计数,orz
    * 7 1 2 3 4 5 6 老外
    * 1 2 3 4 5 6 7 Calendar.DAY_OF_WEEK
    * 6 0 1 2 3 4 5 需要的结果(hash_day)
    * */
    val hash_day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

    val list = ArrayList<CourseStatus.Course>()
    course.data!!.forEach {
        if (it.hash_day == hash_day && it.week!!.contains(week)) {
//            LogUtils.d("Widget", it.toString())
            list.add(it)
        }
    }
    list.sortBy { it.hash_lesson }
    return list
}

val beginTimeShareName = "zscy_widget_beginTime"

private fun saveBeginTime(context: Context, nowWeek: Int): Long {
    val calendar = Calendar.getInstance()
    val targetWeek = calendar.get(Calendar.WEEK_OF_YEAR) - nowWeek
    calendar.set(Calendar.WEEK_OF_YEAR, targetWeek)
    val hash_day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - hash_day)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    context.defaultSharedPreferences.editor {
        putLong(beginTimeShareName, calendar.time.time)
    }
    return calendar.time.time
}

fun getErrorCourseList(): ArrayList<CourseStatus.Course> {
    val data = CourseStatus.Course()
    data.hash_lesson = 0
    data.course = "数据异常，请刷新"
    data.classroom = ""
    val list = ArrayList<CourseStatus.Course>()
    list.add(data)
    return list
}

fun getNoCourse(): CourseStatus.Course {
    val data = CourseStatus.Course()
    data.hash_lesson = EmptyCourseObject
    data.course = "无课"
    data.classroom = ""
    return data
}

fun saveHashLesson(context: Context, hash_lesson: Int, shareName: String) {
    context.defaultSharedPreferences.editor {
        putInt(shareName, hash_lesson)
    }
}

fun getHashLesson(context: Context, shareName: String): Int {
    return context.defaultSharedPreferences.getInt(shareName, 0)
}


private const val SP_DayOffset = "dayOffset"
//天数偏移量，用于LittleWidget切换明天课程
fun saveDayOffset(context: Context, offset: Int) {
    context.defaultSharedPreferences.editor {
        putInt(SP_DayOffset, offset)
    }
}
fun getDayOffset(context: Context): Int {
    return context.defaultSharedPreferences.getInt(SP_DayOffset, 0)
}


fun isNight(): Boolean {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) > 19
}

/**
 * hash_lesson == 0 第1节 返回8:00
 */
fun getStartCalendarByNum(hash_lesson: Int): Calendar {
    val calendar = Calendar.getInstance()
    when (hash_lesson) {
        0 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
        }
        1 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.set(Calendar.MINUTE, 15)
        }
        2 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 14)
            calendar.set(Calendar.MINUTE, 0)
        }
        3 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 16)
            calendar.set(Calendar.MINUTE, 15)
        }
        4 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
        }
        5 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 20)
            calendar.set(Calendar.MINUTE, 50)
        }
    }
    return calendar
}

fun getWeekDayChineseName(weekDay: Int): String {
    return when (weekDay) {
        1 -> "星期天"
        2 -> "星期一"
        3 -> "星期二"
        4 -> "星期三"
        5 -> "星期四"
        6 -> "星期五"
        7 -> "星期六"
        else -> "null"
    }
}

fun getClickPendingIntent(context: Context, @IdRes resId: Int, action: String, clazz: Class<AppWidgetProvider>): PendingIntent {
    val intent = Intent()
    intent.setClass(context, clazz)
    intent.action = action
    intent.data = Uri.parse("id:$resId")
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}

fun formatTime(calendar: Calendar): String {
    return SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE).format(calendar.time)
}

fun filterClassRoom(classRoom: String): String {
    return if (classRoom.length > 8) {
        classRoom.replace(Regex("[\\u4e00-\\u9fa5()（）]"), "")
    } else {
        classRoom
    }
}

//将widget模块的course转换为lib模块的WidgetCourse，WidgetCourse达到中转作用
fun changeCourseToWidgetCourse(courseStatusBean: CourseStatus.Course):WidgetCourse.DataBean{
    val bean = WidgetCourse.DataBean()
    bean.apply {
        hash_day = courseStatusBean.hash_day
        hash_lesson = courseStatusBean.hash_lesson
        begin_lesson = courseStatusBean.begin_lesson
        day = courseStatusBean.day
        lesson = courseStatusBean.lesson
        course = courseStatusBean.course
        course_num = courseStatusBean.course_num
        teacher = courseStatusBean.teacher
        classroom = courseStatusBean.classroom
        rawWeek = courseStatusBean.rawWeek
        weekModel = courseStatusBean.weekModel
        weekBegin = courseStatusBean.weekBegin
        weekEnd = courseStatusBean.weekEnd
        week = courseStatusBean.week
        type = courseStatusBean.type
        period = courseStatusBean.period
    }
    return bean
}