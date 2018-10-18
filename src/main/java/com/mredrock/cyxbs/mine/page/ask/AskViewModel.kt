package com.mredrock.cyxbs.mine.page.ask

import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.MyQuestion
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalWrapper
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/10.
 */
class AskViewModel : BaseViewModel() {
    val errorEvent = MutableLiveData<String>()
    val askOverEvent = MutableLiveData<List<MyQuestion>>()
    val askWaitEvent = MutableLiveData<List<MyQuestion>>()

    private var overPage = 1
    private var waitPage = 1
    private val pageSize = 6

    fun loadAskOverList() {
        apiService.getMyAskOver(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            askOverEvent.postValue(it)
                        },
                        onError = {
                            if (it.message != null) {
                                errorEvent.postValue(it.message)
                            }
                        }
                )
                .lifeCycle()
    }

    fun loadAskWaitList() {
        apiService.getMyAskWait(user!!.stuNum!!, user!!.idNum!!, waitPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            askWaitEvent.postValue(it)
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
        waitPage = 1
    }
}