package com.mredrock.cyxbs.discover.grades.bean
import java.io.Serializable

class Exam : Comparable<Exam>,Serializable {
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
    var type: String? = null

    override fun compareTo(other: Exam): Int {
        var weekDifference = 0
        var weekDayDifference = 0

        if (other.week != null && week != null) {
            weekDifference = Integer.parseInt(week!!) - Integer.parseInt(other.week!!)
        }
        if (weekDifference != 0) {
            return weekDifference
        }

        if (other.weekday != null && weekday != null) {
            weekDayDifference = Integer.parseInt(weekday!!) - Integer.parseInt(other.weekday!!)
        }
        return if (weekDayDifference != 0) {
            weekDayDifference
        } else Integer.parseInt(begin_time!!.replace(":", "")) - Integer.parseInt(other.begin_time!!.replace(":", ""))
    }

    override fun toString(): String {
        return "Exam(week=$week, weekday=$weekday, student=$student, xh=$xh, courseNo=$courseNo, course=$course, classroom=$classroom, examPrivillage=$examPrivillage, begin_time=$begin_time, end_time=$end_time, seat=$seat, chineseWeekday=$chineseWeekday, date=$date, time=$time, type=$type)"
    }


}