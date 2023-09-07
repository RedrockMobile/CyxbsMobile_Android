package com.mredrock.cyxbs.main.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.main.bean.MessageBean
import com.mredrock.cyxbs.main.network.FairgroundApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/26 15:03
 */
class FairgroundViewModel : BaseViewModel() {

    private val _days = MutableLiveData<String>()
    private val _message = MutableLiveData<MessageBean?>()
    val days: LiveData<String>
        get() = _days
    val message: LiveData<MessageBean?>
        get() = _message

    init {
        getDays()
        getMessage()
    }

    @SuppressLint("CheckResult")
    fun getDays() {
        FairgroundApiService::class.api
            .getDays()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("请求失败")
            }
            .safeSubscribeBy {
                _days.postValue(it.days)
            }
    }

    private fun getMessage() {
        FairgroundApiService::class.api
            .getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("请求失败")
            }
            .safeSubscribeBy {
                _message.postValue(it)
            }
    }
}