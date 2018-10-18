package com.mredrock.cyxbs.mine.page.aboutme

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.mine.network.model.RelateMeItem
import com.mredrock.cyxbs.mine.util.apiService
import com.mredrock.cyxbs.mine.util.user

/**
 * Created by zia on 2018/9/13.
 */
class AboutMeViewModel : ViewModel() {

    val errorEvent = MutableLiveData<String>()
    val dataEvent = MutableLiveData<List<RelateMeItem>>()

    var page = 1
    private val pageSize = 6

    fun loadData(type: Int) {
        apiService.getRelateMeList(user!!.stuNum!!, user!!.idNum!!, page++, pageSize, type)
                .mapOrThrowApiException()
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            dataEvent.postValue(it)
                        },
                        onError = {
                            errorEvent.postValue(it.message)
                        }
                )
    }


    fun cleanPage() {
        page = 1
    }

}