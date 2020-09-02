package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleDetailViewAdapter
import com.mredrock.cyxbs.course.component.ScheduleDetailView
import com.mredrock.cyxbs.course.network.Course

/**
 * This class is used as Dialog wrapper class, this class use singleton pattern to let there is only
 * one Dialog can access.
 *
 * Created by anriku on 2018/8/23.
 */
@SuppressLint("InflateParams")
class ScheduleDetailBottomSheetDialogHelper(val context:Context) {

    private lateinit var mScheduleDetailViewAdapter: ScheduleDetailViewAdapter

    //保证每次只有一个Dialog弹出，这里没有复用dialog而是每次都新建一个dialog是因为课程详细
    //信息内容高度是不定的，复用的话会造成弹出之后尺寸才变化，而且是一闪，体验不大好
    private var preDialog: RedRockBottomSheetDialog? = null

    fun showDialog(schedules: MutableList<Course>) {
        val dialog = object : RedRockBottomSheetDialog(context){
            override fun dismiss() {
                super.dismiss()
                preDialog = null
            }
        }.apply {
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.course_dialog_schedule_detail, null)
            setContentView(dialogView)
        }
        mScheduleDetailViewAdapter = ScheduleDetailViewAdapter(dialog, schedules)
        val mScheduleDetailView = dialog.findViewById<ScheduleDetailView>(R.id.schedule_detail_view)
        mScheduleDetailView?.scheduleDetailViewAdapter = mScheduleDetailViewAdapter
        preDialog?.dismiss()
        dialog.show()
        preDialog = dialog
    }
}