package com.mredrock.cyxbs.ufield.lxh.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.mredrock.cyxbs.ufield.lxh.bean.DetailMsg
import com.mredrock.cyxbs.ufield.lxh.network.LxhService

class MessageViewModel : BaseViewModel() {
    private val _watchMsg = MutableLiveData<List<DetailMsg>>()
    private val _joinMsg = MutableLiveData<List<DetailMsg>>()
    private val _publishMsg = MutableLiveData<List<DetailMsg>>()
    private val _checkMsg = MutableLiveData<List<DetailMsg>>()

    val watchMsg: LiveData<List<DetailMsg>> get() = _watchMsg
    val joinMsg: LiveData<List<DetailMsg>> get() = _joinMsg
    val publishMsg: LiveData<List<DetailMsg>> get() = _publishMsg
    val checkMsg: LiveData<List<DetailMsg>> get() = _checkMsg

    fun getAllMsg() {
        LxhService.INSTANCE.getAllMsg()
            .setSchedulers()
            .mapOrThrowApiException()
            .doOnError {
                toast("服务君似乎打盹了呢~")
            }.safeSubscribeBy {
                _watchMsg.postValue(it.watchMsg)
                _joinMsg.postValue(it.joinMsg)
                _publishMsg.postValue(it.publishMsg)
                _checkMsg.postValue(it.checkMsg)
            }
    }
}