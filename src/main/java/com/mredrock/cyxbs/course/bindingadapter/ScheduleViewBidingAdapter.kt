package com.mredrock.cyxbs.course.bindingadapter

import android.databinding.BindingAdapter
import com.mredrock.cyxbs.course.adapters.ScheduleViewAdapter
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.adapters.NoCourseInviteScheduleViewAdapter
import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/9/11.
 */

object ScheduleViewBidingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["schedules", "nowWeek"])
    fun setScheduleData(scheduleView: ScheduleView, schedules: List<Course>?, nowWeek: Int) {
        schedules?.let {
            val scheduleViewAdapter = ScheduleViewAdapter(scheduleView.context, nowWeek, it)
            scheduleView.adapter = scheduleViewAdapter
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["nowWeek", "studentsCourseMap", "nameList"])
    fun setNoCourseInvite(scheduleView: ScheduleView, nowWeek: Int, studentsCourseMap: Map<Int, List<Course>>?,
                          nameList: List<String>) {

        studentsCourseMap?.let {
            val noCourScheduleViewAdapter = NoCourseInviteScheduleViewAdapter(scheduleView.context,
                    nowWeek, it, nameList)
            scheduleView.adapter = noCourScheduleViewAdapter
        }
    }

}