package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
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
class ScheduleDetailDialog constructor(context: Context) {

    private lateinit var mScheduleDetailViewAdapter: ScheduleDetailViewAdapter
    private val mDialog: Dialog

    companion object {
        private const val TAG = "ScheduleDetailDialog"
    }

    init {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(R.layout.course_dialog_schedule_detail, null)
        mDialog = Dialog(context).apply {
            setCancelable(true)
            setContentView(dialogView)
            window.setWindowAnimations(R.style.CourseDialogFragmentAnimation)
        }
    }

    /**
     * This function is used to let the Dialog show
     */
    fun showDialog(schedules: MutableList<Course>) {
        mScheduleDetailViewAdapter = ScheduleDetailViewAdapter(mDialog, schedules)
        val mScheduleDetailView = mDialog.findViewById<ScheduleDetailView>(R.id.schedule_detail_view)
        mScheduleDetailView.scheduleDetailViewAdapter = mScheduleDetailViewAdapter
        mDialog.show()
    }
}