package com.mredrock.cyxbs.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.calender.R
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.calendar_activity_main.*

 class CalendarActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        common_toolbar.init("校 历")
        Glide.with(this).load("http://wx.yyeke.com/api/picture/xiaoli/f21409d56a92417197da254174a723d6.png").into(iv_calendar1)
        Glide.with(this).load("http://wx.yyeke.com/api/picture/xiaoli/348d282e56a549289d9b2b5facbeb8ad.png").into(iv_calendar2)
    }
     override val isFragmentActivity = true

}