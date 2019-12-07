package com.mredrock.cyxbs.course.utils

/**
 * Created by anriku on 2018/10/11.
 */

class CourseTimeParse(private val mClassX: Int, private val mPeriod: Int = 2) {


    fun parseStartCourseTime(): Time = when (mClassX) {
        0 -> Time("8", "00")
        1 -> Time("8", "55")
        2 -> Time("10", "15")
        3 -> Time("11", "10")
        4 -> Time("14", "00")
        5 -> Time("14", "55")
        6 -> Time("16", "15")
        7 -> Time("17", "10")
        8 -> Time("19", "00")
        9 -> Time("19", "55")
        10 -> Time("20", "50")
        11 -> Time("21", "45")
        else -> Time("00", "00")
    }

    fun parseEndCourseTime(): Time = when (mClassX + mPeriod  - 1) {
        0 -> Time("8", "45")
        1 -> Time("9", "40")
        2 -> Time("11", "00")
        3 -> Time("11", "55")
        4 -> Time("14", "45")
        5 -> Time("15", "40")
        6 -> Time("17", "00")
        7 -> Time("17", "55")
        8 -> Time("19", "45")
        9 -> Time("20", "40")
        10 -> Time("21", "35")
        11 -> Time("22", "30")
        else -> Time("00", "00")
    }

    class Time(val hour: String, val minute: String) {

        override fun toString(): String {
            return "$hour:$minute"
        }
    }
}