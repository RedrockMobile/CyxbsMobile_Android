package com.mredrock.cyxbs.declare.pages.main.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.declare.pages.main.DeclareDetailBean
import com.mredrock.cyxbs.declare.pages.main.HomeDataBean
import com.mredrock.cyxbs.declare.pages.main.net.ApiService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
class DeclareHomeViewModel : BaseViewModel() {
    val homeLiveData: LiveData<List<HomeDataBean>>
        get() = _mutableHomeLiveData
    private val _mutableHomeLiveData = MutableLiveData<List<HomeDataBean>>()

    val detailLiveData: LiveData<DeclareDetailBean>
        get() = _mutableDetailLiveData
    private val _mutableDetailLiveData = MutableLiveData<DeclareDetailBean>()

    fun getHomeData() {
        ApiService::class.api
            .getHomeData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("${this.throwable.message}")
            }
            .safeSubscribeBy {
                _mutableHomeLiveData.postValue(it)
            }
    }

    fun getDeclareDetail(id: Int) {
        ApiService::class.api
            .getDetailDeclareData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("${this.throwable.message}")
            }
            .safeSubscribeBy {
                _mutableDetailLiveData.postValue(it)
            }
    }
}