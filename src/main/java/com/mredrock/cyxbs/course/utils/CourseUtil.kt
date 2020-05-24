package com.mredrock.cyxbs.course.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.course.network.Course
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Jovines on 2020/1/22.
 */

/**
 * 获取当前的课程用于显示在课表的头部
 * @param courses 当天的课程数据
 * @param nowWeek 现在是第几周
 * @return 返回两个值，第一个是课表，第二个是是否第二天的课表
 */
fun getNowCourse(courses: List<Course>, wholeCourses: List<Course>, nowWeek: Int): Pair<Course?, String> {
    var course: Course? = null
    courses.forEach {
        val startCalendar = getStartCalendarByNum(it.hashLesson)
        val endCalendar = getEndCalendarByNum(it.hashLesson)
        val middleCalendar = startCalendar.clone() as Calendar
        middleCalendar.add(Calendar.MINUTE, 45)

        //如果今天还有下一节课，显示下一节
        when {
            //下节课还没开始这节课已经过半，显示下节课提示
            Calendar.getInstance() < startCalendar -> return Pair(it, "下节课")
            Calendar.getInstance() < middleCalendar -> return Pair(it, "进行中...")
            Calendar.getInstance() < endCalendar -> { course = it }
        }
    }
    val tomorrowCourse = getTomorrowCourse(wholeCourses, nowWeek)
    return if (tomorrowCourse == null) {
        //如果明天也没课了，那么就把当前这节课显示完（例如周五最后一节课，细节）
        if (course != null) Pair(course, "进行中...") else Pair(null, "")
    } else {
        //明天有课显示明天的课程
        Pair(tomorrowCourse, "明天")
    }
}

/**
 * 获取明天的课程用于显示在课表的头部
 * @param courses 整个课表数据
 * @param nowWeek 现在是第几周
 */
fun getTomorrowCourse(courses: List<Course>, nowWeek: Int): Course? {
    val schoolCalendar = SchoolCalendar()
    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.add(Calendar.DATE, 1)
    val tomorrowWeek =
            /**
             * 这里两个判断，第一是当前如果是周末的话，就取下一周
             * 但是有个问题是如果是已经放寒假暑假，那么nowWeek也是会是0，这样到了周末依然会显示第一周的星期一的课
             * 所以要排除这种情况，然后做了一个专门为了开学第一天的判断
             */
            if ((tomorrowCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && nowWeek != 0)
                    || (tomorrowCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                            && tomorrowCalendar[Calendar.DAY_OF_YEAR] == schoolCalendar.firstDay[Calendar.DAY_OF_YEAR]))
                nowWeek + 1
            else nowWeek
    val tomorrowList = getCourseByCalendar(courses, tomorrowWeek, tomorrowCalendar)
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
fun getTodayCourse(courses: List<Course>, nowWeek: Int): List<Course>? {
    return getCourseByCalendar(courses, nowWeek, Calendar.getInstance())
}


fun getCourseByCalendar(courses: List<Course>, nowWeek: Int, calendar: Calendar): ArrayList<Course>? {
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

fun getEndCalendarByNum(hash_lesson: Int): Calendar {
    val calendar = Calendar.getInstance()
    when (hash_lesson) {
        0 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 9)
            calendar.set(Calendar.MINUTE, 40)
        }
        1 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 11)
            calendar.set(Calendar.MINUTE, 55)
        }
        2 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 15)
            calendar.set(Calendar.MINUTE, 40)
        }
        3 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 17)
            calendar.set(Calendar.MINUTE, 55)
        }
        4 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 20)
            calendar.set(Calendar.MINUTE, 40)
        }
        5 -> {
            calendar.set(Calendar.HOUR_OF_DAY, 22)
            calendar.set(Calendar.MINUTE, 30)
        }
    }
    return calendar
}


/**
 * 这个方法来制造课表item的圆角背景
 * @param rgb 背景颜色
 * 里面的圆角的参数是写在资源文件里的
 */
fun createCornerBackground(rgb: Int, corner: Float): Drawable {
    val drawable = GradientDrawable()
    drawable.cornerRadii = floatArrayOf(corner, corner, corner, corner, corner, corner, corner, corner)
    drawable.setColor(rgb)
    return drawable
}
