package com.mredrock.cyxbs.widget.bean

import java.io.Serializable


class AffairStatus : Serializable, ArrayList<AffairStatusItem>()

data class AffairStatusItem(
        val affairDates: List<AffairDate>,
        val affairTime: String,
        val begin_lesson: Int,
        val classNumber: List<Any>,
        val classroom: String,
        val course: String,
        val course_id: Int,
        val customType: Int,
        val hash_day: Int,
        val hash_lesson: Int,
        val period: Int,
        val type: String,
        val week: List<Int>,
        val weekBegin: Int,
        val weekEnd: Int
) : Serializable

data class AffairDate(
        val `class`: Int,
        val day: Int,
        val week: List<Int>
) : Serializable