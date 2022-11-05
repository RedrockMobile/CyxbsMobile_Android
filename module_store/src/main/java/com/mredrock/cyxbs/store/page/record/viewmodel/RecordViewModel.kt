package com.mredrock.cyxbs.store.page.record.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
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
    private val _exchangeRecord = MutableLiveData<List<ExchangeRecord>>()
    val exchangeRecord: LiveData<List<ExchangeRecord>>
        get() = _exchangeRecord
    
    // 兑换记录请求是否成功
    private val _exchangeRecordIsSuccessful = MutableLiveData<Boolean>()
    val exchangeRecordIsSuccessful: LiveData<Boolean>
        get() = _exchangeRecordIsSuccessful

    // 获取记录
    private val _pageStampGetRecord = MutableLiveData<List<StampGetRecord>>()
    val pageStampGetRecord: LiveData<List<StampGetRecord>>
        get() = _pageStampGetRecord
    
    // 第一页获取记录请求是否成功
    private val _firstPageGetRecordIsSuccessful = MutableLiveData<Boolean>()
    val firstPageGetRecordIsSuccessful: LiveData<Boolean>
        get() = _firstPageGetRecordIsSuccessful
    
    // 下一页获取记录请求是否成功
    private val _nestPageGetRecordIsSuccessful = MutableLiveData<Boolean>()
    val nestPageGetRecordIsSuccessful: LiveData<Boolean>
        get() = _nestPageGetRecordIsSuccessful

    fun getExchangeRecord() {
        ApiService::class.api
            .getExchangeRecord()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                _exchangeRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                _exchangeRecordIsSuccessful.postValue(true)
                _exchangeRecord.postValue(it)
            }
    }

    private var nowPage = 1
    fun getFirstPageGetRecord() {
        ApiService::class.api
            .getStampGetRecord(1, 30)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                _firstPageGetRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                _firstPageGetRecordIsSuccessful.postValue(true)
                _pageStampGetRecord.postValue(it)
            }
    }

    fun getNextPageGetRecord() {
        ApiService::class.api
            .getStampGetRecord(nowPage + 1, 30)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                _nestPageGetRecordIsSuccessful.postValue(false)
            }.safeSubscribeBy {
                _nestPageGetRecordIsSuccessful.postValue(true)
                if (it.isNotEmpty()) {
                    val oldList = pageStampGetRecord.value ?: emptyList()
                    _pageStampGetRecord.postValue(
                        oldList.toMutableList()
                            .apply { addAll(it) }
                    )
                }
            }
    }
}