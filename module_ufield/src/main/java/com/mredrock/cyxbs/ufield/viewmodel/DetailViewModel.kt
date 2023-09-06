package com.mredrock.cyxbs.ufield.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.ufield.bean.ActivityBean
import com.mredrock.cyxbs.ufield.network.ActivityDetailApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/24 10:49
 */
class DetailViewModel(id:Int) :BaseViewModel(){
private val _detailData= MutableLiveData<ActivityBean>()
    val detailData: LiveData<ActivityBean>
        get() = _detailData
private val _wantToSee=MutableLiveData<Boolean>()

    val wantToSee:LiveData<Boolean>
        get() = _wantToSee
    init {
        getActivityData(id)
    }
    private fun getActivityData(id:Int){
        ActivityDetailApiService::class.api
            .getActivityData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("请求失败")
            }
            .safeSubscribeBy {
                _detailData.postValue(it)
            }

    }
    fun wantToSee(id:Int){
        ActivityDetailApiService::class.api
            .wantToSee(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("请求失败")
                _wantToSee.postValue(false)
            }
            .safeSubscribeBy {
               _wantToSee.postValue(true)
            }
    }

}