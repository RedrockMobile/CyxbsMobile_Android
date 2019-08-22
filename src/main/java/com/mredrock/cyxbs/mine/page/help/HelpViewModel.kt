package com.mredrock.cyxbs.mine.page.help

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.MyHelpQuestion
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.normalWrapper
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/10.
 */
class HelpViewModel : BaseViewModel() {

    val errorEvent = MutableLiveData<String>()
    val adoptOverEvent = MutableLiveData<List<MyHelpQuestion>>()
    val adoptWaitEvent = MutableLiveData<List<MyHelpQuestion>>()

    private var overPage = 1
    private var waitPage = 1
    private val pageSize = 6

    fun loadAdoptOver() {
        apiService.getMyHelpOver(user!!.stuNum!!, user!!.idNum!!, overPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            adoptOverEvent.postValue(it)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun loadAdoptWait() {
        apiService.getMyHelpWait(user!!.stuNum!!, user!!.idNum!!, waitPage++, pageSize)
                .normalWrapper(this)
                .safeSubscribeBy(
                        onNext = {
                            adoptWaitEvent.postValue(it)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                )
                .lifeCycle()
    }

    fun cleanPage(){
        overPage = 1
        waitPage = 1
    }
}