package com.mredrock.cyxbs.store.page.record.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.network.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 14:47
 */
class RecordViewModel : BaseViewModel() {
    // 兑换记录
    val mExchangeRecord = MutableLiveData<List<ExchangeRecord>>()
    // 兑换记录请求是否成功
    val mExchangeRecordIsSuccessful = MutableLiveData<Boolean>()

    // 获取记录
    val mPageStampGetRecord = MutableLiveData<List<StampGetRecord>>()
    // 第一页获取记录请求是否成功
    val mFirstPageGetRecordIsSuccessful = MutableLiveData<Boolean>()
    // 下一页获取记录请求是否成功
    val mNestPageGetRecordIsSuccessful = MutableLiveData<Boolean>()

    fun getExchangeRecord() {
        ApiService::class.api
            .getExchangeRecord()
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                mExchangeRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                mExchangeRecordIsSuccessful.postValue(true)
                mExchangeRecord.postValue(it)
            }
    }

    private var nowPage = 1
    fun getFirstPageGetRecord() {
        ApiService::class.api
            .getStampGetRecord(1, 30)
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                mFirstPageGetRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                mFirstPageGetRecordIsSuccessful.postValue(true)
                mPageStampGetRecord.postValue(it)
            }
    }

    fun getNextPageGetRecord() {
        ApiService::class.api
            .getStampGetRecord(nowPage + 1, 30)
            .mapOrThrowApiException()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                mNestPageGetRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                mNestPageGetRecordIsSuccessful.postValue(true)
                if (it.isNotEmpty()) {
                    val oldList = mPageStampGetRecord.value ?: emptyList()
                    mPageStampGetRecord.postValue(
                        oldList.toMutableList()
                            .apply { addAll(it) }
                    )
                }
            }
    }
}