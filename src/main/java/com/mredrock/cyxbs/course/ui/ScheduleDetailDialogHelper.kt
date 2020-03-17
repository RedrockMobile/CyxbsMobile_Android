package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
class ScheduleDetailDialogHelper constructor(context: Context) :
        BaseDialogHelper(context, R.layout.course_dialog_schedule_detail) {

    private lateinit var mScheduleDetailViewAdapter: ScheduleDetailViewAdapter

    companion object {
        private const val TAG = "ScheduleDetailDialogHelper"
    }

    fun showDialog(schedules: MutableList<Course>) {
        mScheduleDetailViewAdapter = ScheduleDetailViewAdapter(dialog, schedules)
        val mScheduleDetailView = dialog.findViewById<ScheduleDetailView>(R.id.schedule_detail_view)
        mScheduleDetailView?.scheduleDetailViewAdapter = mScheduleDetailViewAdapter
        dialog.behavior.peekHeight = 0
        dialog.show()
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}