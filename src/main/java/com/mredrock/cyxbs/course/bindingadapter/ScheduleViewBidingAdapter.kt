package com.mredrock.cyxbs.course.bindingadapter

import android.databinding.BindingAdapter
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.course.adapters.ScheduleViewAdapter
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.adapters.NoCourseInviteScheduleViewAdapter
import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/9/11.
 */

object ScheduleViewBidingAdapter {

    /**
     * @param scheduleView [ScheduleView]
     * @param schedules 要显示的内容。如果是用户课表就包含课表和备忘。如果是他人课表就只有备忘。
     * @param nowWeek 表示是第几周
     * @param isBanTouchView 是否禁用空白处点击添加备忘的功能。如果是用户课表就为false。如果是他人课表就为true。
     */
    @JvmStatic
    @BindingAdapter(value = ["schedules", "nowWeek", "isBanTouchView"])
    fun setScheduleData(scheduleView: ScheduleView, schedules: List<Course>?, nowWeek: Int, isBanTouchView: Boolean) {
        schedules?.let {
            val scheduleViewAdapter = ScheduleViewAdapter(scheduleView.context, nowWeek, it, isBanTouchView)
            scheduleView.adapter = scheduleViewAdapter
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["nowWeek", "studentsCourseMap", "nameList"])
    fun setNoCourseInvite(scheduleView: ScheduleView, nowWeek: Int, commonNoCourseMap: Map<Int, List<Course>>?,
                          nameList: List<String>) {

        commonNoCourseMap?.let {
            val noCourScheduleViewAdapter = NoCourseInviteScheduleViewAdapter(scheduleView.context,
                    nowWeek, it, nameList)
            scheduleView.adapter = noCourScheduleViewAdapter
        }
    }

}