package com.mredrock.cyxbs.volunteer.widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.utils.extensions.MarqueeTextView
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffair
import com.mredrock.cyxbs.volunteer.utils.DateUtils
import com.mredrock.cyxbs.volunteer.utils.DateUtils.stamp2Date

/**
 * Created by yyfbe, Date on 2020/9/5.
 */
class VolunteerAffairBottomSheetDialog(context: Context) : RedRockBottomSheetDialog(context) {
    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.volunteer_layout_volunteer_affair_detail, null, false)
        setContentView(dialogView)
    }
    private val tv_volunteer_affair_detail_title by lazy { findViewById<MarqueeTextView>(R.id.tv_volunteer_affair_detail_title) }
    private val tv_volunteer_affair_detail_description by lazy { findViewById<TextView>(R.id.tv_volunteer_affair_detail_description) }
    private val tv_volunteer_affair_detail_sign_up_end_time by lazy { findViewById<TextView>(R.id.tv_volunteer_affair_detail_sign_up_end_time) }
    private val tv_volunteer_affair_detail_service_time by lazy { findViewById<TextView>(R.id.tv_volunteer_affair_detail_service_time) }
    private val tv_volunteer_affair_detail_time_value by lazy { findViewById<TextView>(R.id.tv_volunteer_affair_detail_time_value) }

    fun refresh(volunteerAffair: VolunteerAffair) {
        tv_volunteer_affair_detail_title.text = volunteerAffair.name
        tv_volunteer_affair_detail_description.text = volunteerAffair.description
        tv_volunteer_affair_detail_sign_up_end_time.text = DateUtils.stamp2Date(volunteerAffair.lastDate)
        tv_volunteer_affair_detail_service_time.text = stamp2Date(volunteerAffair.date)
        tv_volunteer_affair_detail_time_value.text = volunteerAffair.hour
    }
}