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
    val mFirstPageStampGetRecord by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<List<StampGetRecord>>() }
    // 第一页获取记录请求是否成功
    val mFirstPageGetRecordIsSuccessful by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Boolean>() }
    // 下一页获取记录请求是否成功
    val mNestPageGetRecordIsSuccessful by lazy(LazyThreadSafetyMode.NONE) { MutableLiveData<Boolean>() }
    // 没有更多获取记录数据时的回调
    var mHaveNotMoreNestGetRecord: (() -> Unit)? = null

    fun getExchangeRecord() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getExchangeRecord()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is RedrockApiIllegalStateException) { // 如果请求来的数据中 data 为空, 将报这个错误
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

    private var nowPage = 1
    fun getFirstPageGetRecord() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getStampGetRecord(1, 30)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onError = {
                    if (it is RedrockApiIllegalStateException) { // 如果请求来的数据中 data 为空, 将报这个错误
                        mFirstPageGetRecordIsSuccessful.postValue(true)
                        mFirstPageStampGetRecord.postValue(listOf())
                    }else {
                        mFirstPageGetRecordIsSuccessful.postValue(false)
                    }
                },
                onNext = {
                    mFirstPageGetRecordIsSuccessful.postValue(true)
                    mFirstPageStampGetRecord.postValue(it)
                }
            )
    }

    fun getNextPageGetRecord() {
        ApiGenerator.getApiService(ApiService::class.java)
            .getStampGetRecord(nowPage + 1, 30)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    if (it.isNotEmpty()) {
                        val list = mFirstPageStampGetRecord.value
                        if (list is MutableList) { // 这里不是第一次加载, list 不会为空
                            nowPage++
                            list.addAll(it)
                            mNestPageGetRecordIsSuccessful.postValue(true)
                            mFirstPageStampGetRecord.postValue(list)
                        }
                    }else {
                        mHaveNotMoreNestGetRecord?.invoke() // 没有更多数据时
                    }
                },
                onError = {
                    mNestPageGetRecordIsSuccessful.postValue(false)
                }
            )
    }
}