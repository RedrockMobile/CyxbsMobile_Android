package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.NameListRecAdapter
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.utils.CourseTimeParse


/**
 * Created by anriku on 2019/3/10.
 */
class NoCourseInviteDetailDialogHelper(context: Context) : BaseDialogHelper(context,
        R.layout.course_no_course_invite_detail_dialog) {

    private val mDayOfWeek: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_course_day_of_week_strings)
    }

    @SuppressLint("SetTextI18n")
    fun showDialog(row: Int, column: Int, length: Int, people: List<String>) {
        dialog.findViewById<TextView>(R.id.tv_day_of_week).apply {
            text = mDayOfWeek[column]
        }
        dialog.findViewById<TextView>(R.id.tv_course_time).apply {
            text = "${2 * row + 1}~${2 * row + 1 + length}"
        }
        dialog.findViewById<TextView>(R.id.tv_specific_time).apply {
            val courseTimeParse = CourseTimeParse(row, length)
            text = "${courseTimeParse.parseStartCourseTime()}~${courseTimeParse.parseEndCourseTime()}"
        }
        dialog.findViewById<TextView>(R.id.tv_people_count).apply {
            text = "共计${people.size}人"
        }
        dialog.findViewById<RecyclerView>(R.id.rv).apply {
            adapter = NameListRecAdapter(context, people)
        }

        dialog.show()
    }

}