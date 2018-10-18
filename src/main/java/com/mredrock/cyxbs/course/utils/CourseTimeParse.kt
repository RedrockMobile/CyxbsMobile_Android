package com.mredrock.cyxbs.course.utils

/**
 * Created by anriku on 2018/10/11.
 */

class CourseTimeParse(private val mClassX: Int, private val mPeriod: Int = 2) {


    fun parseStartCourseTime(): Time = when (mClassX) {
        0 -> Time("8", "00")
        1 -> Time("10", "15")
        2 -> Time("14", "00")
        3 -> Time("16", "15")
        4 -> Time("19", "00")
        5 -> Time("21", "15")
        else -> Time("00", "00")
    }

    fun parseEndCourseTime(): Time {
        val endClassX = mClassX + mPeriod / 2 - 1
        return when (endClassX) {
            0 -> Time("9", "40")
            1 -> Time("11", "55")
            2 -> Time("15", "40")
            3 -> Time("17", "55")
            4 -> Time("20", "40")
            5 -> Time("22", "55")
            else -> Time("00", "00")
        }
    }

    class Time(val hour: String, val minute: String) {

        override fun toString(): String {
            return "$hour:$minute"
        }
    }
}