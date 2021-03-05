package com.mredrock.cyxbs.qa.pages.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.network.ApiServiceNew

/**
 * @date 2021-03-05
 * @author Sca RayleighZ
 */
class MyIgnoreViewModel : BaseViewModel(){

    private val _ignoreList = MutableLiveData<List<Ignore>>()
    val ignoreList get() = _ignoreList

    fun getIgnorePeople (){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .getIgnoreUid()
                .doOnErrorWithDefaultErrorHandler { true }
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            ignoreList.postValue(it)
                        },
                        onError = {
                            BaseApp.context.toast(it.toString())
                        }
                )
    }

    fun antiIgnore(uid: String, onSuccess: ()->Unit){
        ApiGenerator.getApiService(ApiServiceNew::class.java)
                .cancelIgnoreUid(uid)
                .doOnErrorWithDefaultErrorHandler { true }
                .setSchedulers()
                .safeSubscribeBy(
                        onNext = {
                            onSuccess()
                        },
                        onError = {
                            BaseApp.context.toast(it.toString())
                        }
                )
    }
}