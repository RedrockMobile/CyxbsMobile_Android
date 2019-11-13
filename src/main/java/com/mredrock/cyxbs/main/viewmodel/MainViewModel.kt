package com.mredrock.cyxbs.main.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiException
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.main.network.ApiService
import com.mredrock.cyxbs.main.ui.MainActivity
import kotlinx.android.synthetic.main.main_activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainViewModel : BaseViewModel() {
    val startPage: LiveData<StartPage?> = MutableLiveData()
    var isCourseTop = true

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
                            val gap = now - date.time / 1000
                            if (gap < 24 * 60 * 60 && gap > 0) {
                                return@map startPage
                            }
                        } catch (e: Throwable) {
                            LogUtils.e("SplashViewModel", "parse time failed", e)
                        }
                    }
                    throw RedrockApiException("no start page found.")
                }
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    (startPage as MutableLiveData).value = null
                }, onNext = {
                    (startPage as MutableLiveData).value = it
                })
                .lifeCycle()
    }

    fun initBottomSheetBehavior(activity: MainActivity) {
        activity.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    nav_main.translationY = nav_main.height * p1
                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_DRAGGING && !isCourseTop) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            })
        }
    }
}