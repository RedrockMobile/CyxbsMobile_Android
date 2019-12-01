package com.mredrock.cyxbs.mine.page.ask

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AskPosted

/**
 * Created by zia on 2018/9/10.
 */
class AskViewModel : BaseViewModel() {
    val errorEvent = MutableLiveData<String>()
//    val askOverEvent = MutableLiveData<List<MyAskQuestion>>()
//    val askWaitEvent = MutableLiveData<List<MyAskQuestion>>()

//    private var overPage = 1
//    private var waitPage = 1
//    private val pageSize = 6

//    fun loadAskOverList() {
//        apiService.getMyAskOver(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
//                .normalWrapper(this)
//                .safeSubscribeBy(
//                        onNext = {
//                            askOverEvent.postValue(it)
//                        },
//                        onError = {
//                            if (it.message != null) {
//                                errorEvent.postValue(it.message)
//                            }
//                        }
//                )
//                .lifeCycle()
//    }
//
//    fun loadAskWaitList() {
//        apiService.getMyAskWait(user!!.stuNum!!, user!!.idNum!!, waitPage++, pageSize)
//                .normalWrapper(this)
//                .safeSubscribeBy(
//                        onNext = {
//                            askWaitEvent.postValue(it)
//                        },
//                        onError = {
//                            if (it.message != null) {
//                                errorEvent.postValue(it.message)
//                            }
//                        }
//                )
//                .lifeCycle()
//    }

    fun cleanPage(){
    }

    val askPosted = MutableLiveData<List<AskPosted>>()

}