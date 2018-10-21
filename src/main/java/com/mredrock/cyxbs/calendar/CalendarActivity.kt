package com.mredrock.cyxbs.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.calender.R
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.calendar_activity_main.*
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.calendar.network.ApiService
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import org.jetbrains.anko.toast


class CalendarActivity : BaseActivity(){


     private val apiService= ApiGenerator.getApiService(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        common_toolbar.init("校 历")
        init()
    }
     override val isFragmentActivity = true
    private fun init(){
        apiService
                .getCalendar()
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toast("无法获取校历")
                    Log.d("Calender",it.localizedMessage)
                    false
                }
                .safeSubscribeBy {it->
                    if(it.isNotEmpty()) {
                        Glide.with(this).load(it.get(0).address).into(iv_calendar1)
                        Glide.with(this).load(it.get(1).address).into(iv_calendar2)
                    }
                }
    }
}