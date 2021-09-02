package com.mredrock.cyxbs.store.page.center.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.network.ApiService
import com.mredrock.cyxbs.store.utils.Date

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/7
 */
class StoreCenterViewModel: BaseViewModel() {
    // 邮票中心界面数据
    val stampCenterData by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<StampCenter>() }

    // 网络请求是否刷新成功
    val refreshIsSuccessful by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Boolean>() }

    // 是否展示邮票中心界面 TabLayout 的小圆点, 产品给的需求是每天只显示一遍
    var isShowTabLayoutBadge: Boolean
        get() {
            val shared = context.sharedPreferences("store")
            val nowadays = Date.getTime(java.util.Date())
            val lastDay = shared.getString("show_tabLayout_badge_date", "")
            return nowadays != lastDay
        }
        set(value) {
            if (!value) {
                val shared = context.sharedPreferences("store")
                shared.editor {
                    val nowadays = Date.getTime(java.util.Date())
                    putString("show_tabLayout_badge_date", nowadays)
                }
            }
        }

    fun refresh() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getStampCenter()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    Log.d("ggg","(StoreCenterViewModel.kt:51)-->> ${it.message}")
                    refreshIsSuccessful.postValue(false)
                },
                onNext = {
                    refreshIsSuccessful.postValue(true)
                    stampCenterData.postValue(it)
                }
            )
    }
}