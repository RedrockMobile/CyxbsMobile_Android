package com.mredrock.cyxbs.course.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.course.network.Course
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jon on 2020/1/22.
 * 将子来学长的工具类复用到这个位置
 */

/**
 * 获取当前的课程用于显示在课表的头部
 * @param courses 当天的课程数据
 * @param nowWeek 现在是第几周
 */
fun getNowCourse(courses: List<Course>,wholeCourses:List<Course>, nowWeek: Int):Course? {
    var isFound = false
    courses.forEach {
        val endCalendar = getStartCalendarByNum(it.hashLesson)
        //如果今天还有下一节课，显示下一节
        if (Calendar.getInstance() < endCalendar) {
            return it
        }
        isFound = true
    }

    return if (isFound) {//今天有课，但是上完了
        //新策略：显示明天第一节
        getTomorrowCourse(wholeCourses, nowWeek)
    } else {//今天没有课
        if (isNight()) {//如果在晚上，显示明天课程
            getTomorrowCourse(wholeCourses, nowWeek)
        } else {
            //白天显示今天无课
            null
        }
    }

}

/**
 * 获取明天的课程用于显示在课表的头部
 * @param courses 整个课表数据
 * @param nowWeek 现在是第几周
 */
fun getTomorrowCourse(courses: List<Course>, nowWeek: Int): Course? {
    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.set(Calendar.DAY_OF_YEAR, tomorrowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
    val tomorrowList = getCourseByCalendar(courses, nowWeek, tomorrowCalendar)
    return if (tomorrowList == null) {//数据出错
        null
    } else {
        if (tomorrowList.isEmpty()) {//明日无课
            null
        } else {//显示明天第一节课
            tomorrowList.first()
        }
    }
}

/**
 * 获得今天得课程list信息
 */
fun getTodayCourse(courses:List<Course>,nowWeek:Int): List<Course>? {
    return getCourseByCalendar(courses,nowWeek, Calendar.getInstance())
}


fun getCourseByCalendar(courses:List<Course>,nowWeek:Int,calendar: Calendar): ArrayList<Course>? {
    /*
    * 转换表，老外从周日开始计数,orz
    * 7 1 2 3 4 5 6 老外
    * 1 2 3 4 5 6 7 Calendar.DAY_OF_WEEK
    * 6 0 1 2 3 4 5 需要的结果(hash_day)
    * */
    val hashDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val list = ArrayList<Course>()
    courses.forEach {
        if (it.hashDay == hashDay && it.week!!.contains(nowWeek)) {
            list.add(it)
        }
    }
    list.sortBy { it.hashLesson }
    return list
}

private val beginTimeShareName = "zscy_widget_beginTime"

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

fun getErrorCourseList(): ArrayList<Course> {
    val data = Course()
    data.hashLesson = 0
    data.course = "数据异常，请刷新"
    data.classroom = ""
    val list = ArrayList<Course>()
    list.add(data)
    return list
}

fun getNoCourse(): Course {
    val data = Course()
    data.hashLesson = 0
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
            calendar.set(Calendar.HOUR_OF_DAY, 21)
            calendar.set(Calendar.MINUTE, 15)
        }
        6 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 19)
            calendar.set(Calendar.MINUTE, 0)
        }
        7 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 21)
            calendar.set(Calendar.MINUTE, 0)
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
fun changeCourseToWidgetCourse(courseBean: Course):WidgetCourse.DataBean{
    val bean = WidgetCourse.DataBean()
    bean.apply {
        hash_day = courseBean.hashDay
        hash_lesson = courseBean.hashLesson
        begin_lesson = courseBean.beginLesson
        day = courseBean.day
        lesson = courseBean.lesson
        course = courseBean.course
        course_num = courseBean.courseNum
        teacher = courseBean.teacher
        classroom = courseBean.classroom
        rawWeek = courseBean.rawWeek
        weekModel = courseBean.weekModel
        weekBegin = courseBean.weekBegin
        weekEnd = courseBean.weekEnd
        week = courseBean.week
        type = courseBean.type
        period = courseBean.period
    }
    return bean
}