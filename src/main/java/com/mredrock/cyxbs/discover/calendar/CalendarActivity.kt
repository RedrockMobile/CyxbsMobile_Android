package com.mredrock.cyxbs.discover.calendar

import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.calendar.R
import com.mredrock.cyxbs.common.config.DISCOVER_CALENDER
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.discover.calendar.network.ApiService
import kotlinx.android.synthetic.main.calendar_activity_main.*
import org.jetbrains.anko.toast

@Route(path = DISCOVER_CALENDER)
class CalendarActivity : BaseActivity() {


    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_activity_main)
        common_toolbar.init("校 历")
        init()
    }

    override val isFragmentActivity = true
    private fun init() {
        apiService
                .getCalendar()
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    toast("无法获取校历")
                    Log.d("Calender", it.localizedMessage)
                    false
                }
                .safeSubscribeBy { it ->
                    if (it.isNotEmpty()) {
                        Glide.with(this).load(it.get(0).address).into(iv_calendar1)
                        Glide.with(this).load(it.get(1).address).into(iv_calendar2)
                    }
                }
    }
}