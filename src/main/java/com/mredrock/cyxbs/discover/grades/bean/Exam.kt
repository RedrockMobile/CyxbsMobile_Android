package com.mredrock.cyxbs.grades.bean

class Exam : Comparable<Exam> {
    var week: String? = null
    var weekday: String? = null
    var student: String? = null
    var xh: String? = null
    var courseNo: String? = null
    var course: String? = null
    var classroom: String? = null
    var examPrivillage: String? = null
    var begin_time: String? = null
    var end_time: String? = null
    var seat: String? = null
    var chineseWeekday: String? = null
    var date: String? = null
    var time: String? = null

    override fun compareTo(exam: Exam): Int {
        var weekDifference = 0
        var weekDayDifference = 0

        if (exam.week != null && week != null) {
            weekDifference = Integer.parseInt(week!!) - Integer.parseInt(exam.week!!)
        }
        if (weekDifference != 0) {
            return weekDifference
        }

        if (exam.weekday != null && weekday != null) {
            weekDayDifference = Integer.parseInt(weekday!!) - Integer.parseInt(exam.weekday!!)
        }
        return if (weekDayDifference != 0) {
            weekDayDifference
        } else Integer.parseInt(begin_time!!.replace(":", "")) - Integer.parseInt(exam.begin_time!!.replace(":", ""))


    }

}