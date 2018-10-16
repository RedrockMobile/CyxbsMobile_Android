package com.mredrock.cyxbs.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.main.network.ApiService
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created By jay68 on 2018/8/10.
 */
class SplashViewModel : BaseViewModel() {
    var startPage: LiveData<StartPage?> = MutableLiveData()

    fun getStartPage() {
        ApiGenerator.getApiService(ApiService::class.java)
                .getStartPage()
                .mapOrThrowApiException()
                .map {
                    it.forEach { startPage ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        try {
                            val now = System.currentTimeMillis() / 1000
                            val date = dateFormat.parse(startPage.start)
                            if (Math.abs(date.time / 1000 - now) < 24 * 60 * 60) {
                                return@map startPage
                            }
                        } catch (e: Throwable) {
                            LogUtils.e("SplashViewModel", "parse time failed", e)
                        }
                    }
                    throw RedrockApiException("no start page found.")
                }
                .setSchedulers()
                .safeSubscribeBy { (startPage as MutableLiveData).value = it }
                .lifeCycle()
    }
}