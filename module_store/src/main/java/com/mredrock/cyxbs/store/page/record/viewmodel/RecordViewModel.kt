package com.mredrock.cyxbs.store.page.record.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.RedrockApiIllegalStateException
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.network.ApiService

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:47
 */
class RecordViewModel : BaseViewModel() {
    // 兑换记录
    val mExchangeRecord by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<List<ExchangeRecord>>() }
    // 兑换记录请求是否成功
    val mExchangeRecordIsSuccessful by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Boolean>() }

    // 获取记录
    val mStampGetRecord by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<List<StampGetRecord>>() }
    // 获取记录请求是否成功
    val mStampGetRecordIsSuccessful by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Boolean>() }

    fun getExchangeRecord() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getExchangeRecord()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is RedrockApiIllegalStateException) {
                        mExchangeRecordIsSuccessful.postValue(true)
                        mExchangeRecord.postValue(listOf())
                    }else {
                        mExchangeRecordIsSuccessful.postValue(false)
                    }
                },
                onNext = {
                    mExchangeRecordIsSuccessful.postValue(true)
                    mExchangeRecord.postValue(it)
                }
            )
    }

    fun getStampRecord() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getStampGetRecord(1, 100)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is RedrockApiIllegalStateException) {
                        mStampGetRecordIsSuccessful.postValue(true)
                        mStampGetRecord.postValue(listOf())
                    }else {
                        mStampGetRecordIsSuccessful.postValue(false)
                    }
                },
                onNext = {
                    mStampGetRecordIsSuccessful.postValue(true)
                    mStampGetRecord.postValue(it)
                }
            )
    }
}