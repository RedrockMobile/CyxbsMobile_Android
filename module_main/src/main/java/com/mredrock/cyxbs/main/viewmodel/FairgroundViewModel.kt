package com.mredrock.cyxbs.main.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.main.bean.MessageBean
import com.mredrock.cyxbs.main.network.FairgroundApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            getDays()
            getMessage()
        }
    }

    @SuppressLint("CheckResult")
    suspend fun getDays() {
        FairgroundApiService
            .INSTANCE
            .getDays()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribeBy {

                _days.postValue(it.data.days)
            }
    }

    private suspend fun getMessage() {
        FairgroundApiService
            .INSTANCE
            .getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                toast("请求失败")
                Log.e("JJJ", "getDays: ", it)
            }
            .safeSubscribeBy {
                _message.postValue(it.data)
            }
    }
}