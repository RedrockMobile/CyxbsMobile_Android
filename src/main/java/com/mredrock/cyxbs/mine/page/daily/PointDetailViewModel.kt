package com.mredrock.cyxbs.mine.page.daily

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.PointDetail
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.extension.normalWrapper
import com.mredrock.cyxbs.mine.util.user

/**
 * Create By Hosigus at 2019/3/16
 */
class PointDetailViewModel : BaseViewModel() {
    val errorEvent = MutableLiveData<String>()
    val recordEvent = MutableLiveData<List<PointDetail>>()
    val accountEvent = MutableLiveData<Int>()

    private var overPage = 1
    private val pageSize = 6

    fun loadAllData() {

        apiService.getScoreStatus(user!!.stuNum!!, user!!.idNum!!)
                .normalWrapper(this)
                .map { it.integral }
                .safeSubscribeBy {
                    accountEvent.value = it
                }

        loadRecord()
    }

    fun loadRecord() {
        apiService.getIntegralRecords(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
                .mapOrThrowApiException()
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { false }
                .safeSubscribeBy(
                        onNext = {
                            recordEvent.postValue(it)
                        },
                        onError = {
                            if (it.message != null) {
                                errorEvent.postValue(it.message)
                            }
                        }
                )
    }

    fun cleanPage() {
        overPage = 1
    }
}