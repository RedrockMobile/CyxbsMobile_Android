package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.NameListRecAdapter
import com.mredrock.cyxbs.course.utils.CourseTimeParse


/**
 * Created by anriku on 2019/3/10.
 */
class NoCourseInviteDetailBottomSheetDialogHelper(val context: Context) {

    private val mDayOfWeek: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getStringArray(R.array.course_course_day_of_week_strings)
    }

    //保证每次只有一个Dialog弹出，这里没有复用dialog而是每次都新建一个dialog是因为没课约详细
    //信息内容高度是不定的，复用的话会造成弹出之后尺寸才变化，而且是一闪，体验不大好
    private var preDialog: RedRockBottomSheetDialog? = null

    fun showDialog(row: Int, column: Int, length: Int, people: List<String>) {
        val dialog = object : RedRockBottomSheetDialog(context){
            override fun dismiss() {
                super.dismiss()
                preDialog = null
            }
        }.apply {
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.course_no_course_invite_detail_dialog, null)
            setContentView(dialogView)
        }
        dialog.findViewById<TextView>(R.id.tv_day_of_week)?.apply {
            text = mDayOfWeek[column]
        }
        dialog.findViewById<TextView>(R.id.tv_course_time)?.apply {
            val s = "${row + 1}~${row + length}"
            text = s
        }
        dialog.findViewById<TextView>(R.id.tv_specific_time)?.apply {
            val courseTimeParse = CourseTimeParse(row, length)
            val s = "${courseTimeParse.parseStartCourseTime()}~${courseTimeParse.parseEndCourseTime()}"
            text = s
        }
        dialog.findViewById<TextView>(R.id.tv_people_count)?.apply {
            val s = "共计${people.size}人"
            text = s
        }
        dialog.findViewById<RedRockAutoWarpView>(R.id.rv_people)?.apply {
            adapter = NameListRecAdapter(people)
        }
        preDialog?.dismiss()
        dialog.show()
        preDialog = dialog
    }
}