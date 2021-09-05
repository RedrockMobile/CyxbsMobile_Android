package com.mredrock.cyxbs.store.page.center.viewmodel

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
                    refreshIsSuccessful.postValue(false)
                },
                onNext = {
                    refreshIsSuccessful.postValue(true)
                    stampCenterData.postValue(it)
                }
            )
    }

    /*
    * ================================================================================================================
    * 下面放非网络请求, 只用于 Fragment 与 Activity 之间的回调
    * */

    /**
     * 该回调用于 StoreCenterActivity 与 StampTaskFragment 之间的通信, 在 ViewPager2 滑到一半左右时,
     * 通知 StampTaskFragment 设置 RecyclerView 的 adapter
     *
     * 为什么要写个回调:
     * ```
     * 1、因为 ViewPager2 需要设置预加载(不设置的话有点卡), 而 RecyclerView 有入场动画, 在设置了预加载后
     * 就会使 RecyclerView 的入场动画不在屏幕内就被加载
     * 2、如果在 Fragment 的 onResume() 中再设置 RecyclerView 的 Adapter 会使加载的时间增长, 影响体验
     * ```
     *
     */
    var loadStampTaskRecyclerView: (() -> Unit)? = null
}