package com.mredrock.cyxbs.discover.calendar

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.calendar.R
import com.mredrock.cyxbs.common.config.DISCOVER_CALENDAR
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.getScreenHeight
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import kotlinx.android.synthetic.main.calendar_activity_main.*

@Route(path = DISCOVER_CALENDAR)

//todo 图片显示糊的处理
class CalendarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        common_toolbar.init("校 历")
        val request = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(getScreenWidth(), getScreenHeight())
                .format(DecodeFormat.PREFER_ARGB_8888)
                .fitCenter()
        Glide.with(this)
                .load("$END_POINT_REDROCK/234/newapi/schoolCalendar")
                .apply(request)
                .into(iv_calendar)
    }

    override val isFragmentActivity = false
}