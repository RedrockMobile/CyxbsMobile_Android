package com.mredrock.cyxbs.affair.ui.viewmodel.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.affair.bean.NotificationResultBean
import com.mredrock.cyxbs.affair.net.AffairApiService
import com.mredrock.cyxbs.api.affair.NotificationBean
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.launchCatch
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class NoClassAffairViewModel : BaseViewModel() {

    private val _titleCandidates = MutableLiveData<List<String>>()
    val titleCandidates: LiveData<List<String>>
        get() = _titleCandidates

    private val _hotLocation = MutableLiveData<List<String>>()
    val hotLocation: LiveData<List<String>>
        get() = _hotLocation

    //没课约专属,发送通知
    private val _mutableNotification = MutableLiveData<NotificationResultBean>()
    val notification get() = _mutableNotification

    //没课约最后发送通知
    fun sendNotification(notificationBean: NotificationBean){
           AffairApiService.INSTANCE.sendNotification(notificationBean)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .safeSubscribeBy {
                   _mutableNotification.value = it
               }
    }

    fun getHotLoc(){
        AffairApiService.INSTANCE.getHotLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                // 网络请求失败就发送这个默认显示
                emitter.onSuccess(listOf("二教", "三教", "四教", "五教", "八教", "九教", "风华运动场", "太极运动场", "风雨运动场", "校外"))
            }.safeSubscribeBy {
                _hotLocation.value = it
            }
    }

    init {
        //虽然是没课约，但是候选的关键词都是一样的，如果后续不一样，接口再分割。
        AffairApiService.INSTANCE.getTitleCandidate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                // 网络请求失败就发送这个默认显示
                emitter.onSuccess(listOf("自习", "值班", "考试", "英语", "开会", "作业", "补课", "实验", "复习", "学习"))
            }.safeSubscribeBy {
                _titleCandidates.value = it
            }
    }
}