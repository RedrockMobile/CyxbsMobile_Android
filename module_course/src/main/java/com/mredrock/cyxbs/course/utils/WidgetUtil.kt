package com.mredrock.cyxbs.course.utils

import com.mredrock.cyxbs.common.bean.WidgetCourse
import com.mredrock.cyxbs.course.network.Course

/**
 * create by:Fxymine4ever
 * time: 2019/3/17
 */
//将lib模块的WidgetBean转换为course模块Course，WidgetBean相当于中介
fun changeLibBeanToCourse(widgetCourse: WidgetCourse.DataBean): Course {
    val mCourse = Course()
    mCourse.customType = 0
    mCourse.affairTime = null
    mCourse.affairDates = listOf()
    mCourse.courseId = 0
    mCourse.courseNum = widgetCourse.course_num
    mCourse.course = widgetCourse.course
    mCourse.hashDay = widgetCourse.hash_day
    mCourse.hashLesson = widgetCourse.hash_lesson
    mCourse.beginLesson = widgetCourse.begin_lesson
    mCourse.day = widgetCourse.day
    mCourse.lesson = widgetCourse.lesson
    mCourse.teacher = widgetCourse.teacher
    mCourse.classroom = widgetCourse.classroom
    mCourse.rawWeek = widgetCourse.rawWeek
    mCourse.weekModel = widgetCourse.weekModel
    mCourse.weekBegin = widgetCourse.weekBegin
    mCourse.weekEnd = widgetCourse.weekEnd
    mCourse.type = widgetCourse.type
    mCourse.period = widgetCourse.period
    mCourse.week = widgetCourse.week
    return mCourse
}