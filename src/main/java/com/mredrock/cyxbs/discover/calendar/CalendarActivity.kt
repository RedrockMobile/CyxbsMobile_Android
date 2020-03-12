package com.mredrock.cyxbs.discover.calendar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.mredrock.cyxbs.calendar.R
import com.mredrock.cyxbs.common.config.DISCOVER_CALENDAR
import com.mredrock.cyxbs.common.config.END_POINT_REDROCK
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.calendar_activity_main.*


@Route(path = DISCOVER_CALENDAR)

//todo 图片显示糊的处理
class CalendarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        Glide.with(this)
                .load("$END_POINT_REDROCK/234/newapi/schoolCalendar")
                .into(object : DrawableImageViewTarget(iv_calendar) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val width: Int = resource.intrinsicWidth
                        val height: Int = resource.intrinsicHeight
                        var ivWidth: Int = iv_calendar.width
                        if (ivWidth == 0) {
                            ivWidth = iv_calendar.resources.displayMetrics.widthPixels
                        }
                        val ivHeight = (height / width * ivWidth)
                        val lp: ViewGroup.LayoutParams = iv_calendar.getLayoutParams()
                        lp.height = ivHeight
                        iv_calendar.layoutParams = lp
                        iv_calendar.setImageDrawable(resource)
                    }
                })
    }

    override val isFragmentActivity = false
}