package com.mredrock.cyxbs.course.network

import com.mredrock.cyxbs.common.utils.LogUtils
import io.reactivex.functions.Function


/**
 *  This class is used to map the affair to course, so let it more easy to handle the display.
 *
 * Created by anriku on 2018/8/18.
 */
class AffairMapToCourse : Function<List<Affair>, List<Course>> {

    companion object {
        private const val TAG = "AffairMapToCourse"
    }

    override fun apply(t: List<Affair>): List<Course> {
        val coursesList = mutableListOf<Course>()
        for (affairs in t) {
            affairs.date?.let {
                for (affair in it) {
                    val course = Course()
                    course.customType = Course.AFFAIR
                    course.courseId = affairs.id
                    course.beginLesson = affair.classX * 2 + 1
                    course.hashLesson = affair.classX
                    course.hashDay = affair.day
                    course.week = affair.week
                    course.course = affairs.title
                    course.classroom = affairs.content
                    course.type = "提醒"
                    course.period = 2
                    course.affairTime = affairs.time
                    course.affairDates = affairs.date
                    coursesList.add(course)
                    LogUtils.d(TAG, course.toString())
                }
            }
        }
        return coursesList
    }

}