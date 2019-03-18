package com.mredrock.cyxbs.mine.page.daily

import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.PointDetail
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalWrapper
import com.mredrock.cyxbs.mine.util.user

/**
 * Create By Hosigus at 2019/3/16
 */
class PointDetailViewModel: BaseViewModel() {
    val errorEvent = MutableLiveData<String>()
    val recordEvent = MutableLiveData<List<PointDetail>>()

    private var overPage = 1
    private val pageSize = 6

    fun loadIntegralRecords() {
        apiService.getIntegralRecords(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
                .normalWrapper(this)
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
                .lifeCycle()
    }

    fun cleanPage(){
        overPage = 1
    }
}