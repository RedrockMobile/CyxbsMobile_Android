package com.mredrock.cyxbs.volunteer.widget

import android.content.Context
import android.view.LayoutInflater
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerAffairDetail
import com.mredrock.cyxbs.volunteer.utils.DateUtils
import com.mredrock.cyxbs.volunteer.utils.DateUtils.stamp2Date
import kotlinx.android.synthetic.main.volunteer_layout_volunteer_affair_detail.*

/**
 * Created by yyfbe, Date on 2020/9/5.
 */
class VolunteerAffairBottomSheetDialog(context: Context) : RedRockBottomSheetDialog(context) {
    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.volunteer_layout_volunteer_affair_detail, null, false)
        setContentView(dialogView)
    }


    fun refresh(volunteerAffairDetail: VolunteerAffairDetail) {
        tv_volunteer_affair_detail_title.text = volunteerAffairDetail.name
        tv_volunteer_affair_detail_description.text = volunteerAffairDetail.description
        tv_volunteer_affair_detail_sign_up_end_time.text = DateUtils.stamp2Date(volunteerAffairDetail.lastDate)
        tv_volunteer_affair_detail_service_time.text = stamp2Date(volunteerAffairDetail.date)
        tv_volunteer_affair_detail_time_value.text = volunteerAffairDetail.hour
    }
}