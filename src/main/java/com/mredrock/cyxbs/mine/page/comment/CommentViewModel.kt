package com.mredrock.cyxbs.mine.page.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.mine.network.model.Comment
import com.mredrock.cyxbs.mine.network.model.RelateMeItem
import com.mredrock.cyxbs.mine.util.apiService

/**
 * Created by zia on 2018/9/13.
 */
class CommentViewModel : ViewModel() {

    val errorEvent = MutableLiveData<String>()
    val dataEvent = MutableLiveData<List<RelateMeItem>>()

    var page = 1
    private val pageSize = 6

    var fakeComments: MutableLiveData<List<Comment>> = MutableLiveData()

    private val stuNum = ServiceManager.getService(IUserService::class.java).getStuNum()
    private val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")

    fun loadData(type: Int) {
        apiService.getRelateMeList(stuNum, idNum
                ?: return, page++, pageSize, type)
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