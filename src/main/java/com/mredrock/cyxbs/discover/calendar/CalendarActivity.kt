package com.mredrock.cyxbs.discover.calendar

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.calendar.R
import com.mredrock.cyxbs.common.config.BASE_NORMAL_IMG_URL
import com.mredrock.cyxbs.common.config.DISCOVER_CALENDAR
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.calendar_activity_main.*

@Route(path = DISCOVER_CALENDAR)
class CalendarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        common_toolbar.init("校 历")
        Glide.with(this).load(BASE_NORMAL_IMG_URL + "schoolCalendar").into(iv_calendar)
    }

    override val isFragmentActivity = false
}