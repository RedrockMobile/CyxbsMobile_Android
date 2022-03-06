package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AllStatus
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.network.model.PersonalStatu
import com.mredrock.cyxbs.mine.network.model.Status
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.util.apiService
import io.reactivex.Observable

class IdentityViewModel: BaseViewModel()  {


    val authenticationStatus = MutableLiveData<AuthenticationStatus>()
    val customization = MutableLiveData<AuthenticationStatus>()
    val allIdentifies = MutableLiveData<AllStatus>()
    val  redrockApiStatusUpdate = MutableLiveData<RedrockApiStatus>()
    val showStatu=MutableLiveData<PersonalStatu>()
    val onErrorAction =MutableLiveData<String>()

    /**
     * 这个变量作为一个标记位 为了消除粘性事件带来的bug
     */
    var isUpdata=false
    val isFinsh = MutableLiveData<Boolean>()
    fun getAuthenticationStatus(redid:String){
        apiService.getAuthenticationStatus(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(

                onNext = {
                       authenticationStatus.value=it
                },
                onError = {
                },
                onComplete = {
                }
            )
            .lifeCycle()
    }

    fun getCustomization(redid:String){
        apiService.getCustomization(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                   customization.value = it

                },
                onError = {
                },
                onComplete = {
                }
            )
            .lifeCycle()
    }

    fun getAllIdentify(redid:String?){
        redid?.let {

            apiService.getAllIdentify(it)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler { true }
                .safeSubscribeBy(
                    onNext = {
                        Log.e("wxtag身份","(IdentityViewModel.kt:70)->> 获取身份成功")
                        allIdentifies.value = it

                    },
                    onError = {
                        Log.e("wxtag身份","(IdentityViewModel.kt:70)->> 获取身份失败$it")
                        onErrorAction.value="没有身份就是它的身份"
                    },
                    onComplete = {

                    }
                )
                .lifeCycle()
        }

    }





    fun updateStatus(identityId:String?,type:String?,redid: String){
        apiService.uploadDisplayIdentity(identityId)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    redrockApiStatusUpdate.value=it
                    if (type.equals("个性身份")){
                        getCustomization(redid)

                    }else{
                        getAuthenticationStatus(redid)
                    }
                   getShowIdentify(redid)
                },
                onError = {
                },
                onComplete = {
                        isUpdata=true
                }
            )
            .lifeCycle()
    }


    fun getShowIdentify(id:String){
        apiService.getShowIdentify(id)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    Log.i("身份设置","getShowIdentify")
                showStatu.value=it
                },
                onError = {

                },
                onComplete = {


                }
            )
            .lifeCycle()
    }


}