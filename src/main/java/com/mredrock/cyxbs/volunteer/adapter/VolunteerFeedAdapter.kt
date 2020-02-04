package com.mredrock.cyxbs.volunteer.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseFeedFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import kotlinx.android.synthetic.main.volunteer_discover_feed.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sp

class VolunteerFeedAdapter(private val volunteerTime: VolunteerTime) : BaseFeedFragment.Adapter {
    override fun getView(context: Context, parent: ViewGroup): View {
        val view = context.layoutInflater.inflate(R.layout.volunteer_discover_feed, parent, false)
        view.tv_volunteer_feed_total_time.text = SpannableStringBuilder(volunteerTime.hours?.toInt().toString().plus("时"))
                .apply {
                    setSpan(AbsoluteSizeSpan(context.sp(36)), 0, this.length - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    setSpan(AbsoluteSizeSpan(context.sp(8)), this.length - 1, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
        if (volunteerTime.hours == 0.0) {
            view.tv_volunteer_feed_activity_time.text = "还没有志愿时长"
            return view
        }
        view.tv_volunteer_feed_activity_name.text = volunteerTime.record?.get(0)?.title
        view.tv_volunteer_feed_activity_date.text = volunteerTime.record?.get(0)?.start_time?.substring(0, 10)
        view.tv_volunteer_feed_activity_time.text = volunteerTime.record?.get(0)?.hours.plus("小时")
        view.tv_volunteer_feed_activity_address.text = volunteerTime.record?.get(0)?.server_group
        return view
    }
}