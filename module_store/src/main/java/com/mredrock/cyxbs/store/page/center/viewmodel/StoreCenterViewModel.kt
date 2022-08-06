package com.mredrock.cyxbs.store.page.center.viewmodel

import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.network.ApiService
import com.mredrock.cyxbs.store.utils.Date
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/7
 */
class StoreCenterViewModel: BaseViewModel() {
    // 邮票中心界面数据
    private val _stampCenterData = MutableLiveData<StampCenter>()
    val stampCenterData: LiveData<StampCenter>
        get() = _stampCenterData

    // 网络请求是否刷新成功
    private val _refreshIsSuccessful = MutableLiveData<Boolean>()
    val refreshIsSuccessful: LiveData<Boolean>
        get() = _refreshIsSuccessful

    // 是否展示邮票中心界面 TabLayout 的小圆点, 产品给的需求是每天只显示一遍
    var isShowTabLayoutBadge: Boolean
        get() {
            val shared = appContext.getSp("store")
            val nowadays = Date.getTime(java.util.Date())
            val lastDay = shared.getString("show_tabLayout_badge_date", "")
            return nowadays != lastDay
        }
        set(value) {
            if (!value) {
                val shared = appContext.getSp("store")
                shared.edit {
                    val nowadays = Date.getTime(java.util.Date())
                    putString("show_tabLayout_badge_date", nowadays)
                }
            }
        }

    fun refresh() {
        ApiService::class.api
            .getStampCenter()
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                _refreshIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                _refreshIsSuccessful.postValue(true)
                _stampCenterData.postValue(it)
            }
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
     * 2、如果在 Fragment 的 onResume() 中再设置 RecyclerView 的 Adapter 会使等待时间过长, 影响体验
     * ```
     */
    var loadStampTaskRecyclerView: (() -> Unit)? = null
}