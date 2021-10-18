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

    val isFinsh = MutableLiveData<Boolean>()
    fun getAuthenticationStatus(redid:String){
        apiService.getAuthenticationStatus(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(

                onNext = {
                    Log.e("身份设置","(MineViewModel.kt:30)->>修改身份的网络请求$it ")
                       authenticationStatus.value=it


                },
                onError = {
                    Log.e("身份设置","(MineViewModel.kt:30)->>失败了$it ")
                },
                onComplete = {
                    Log.e("身份设置","(MineViewModel.kt:30)-> onComplete ")
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
                    Log.e("身份设ds置","(MineViewModel.kt:30)->>成功了$it ")
                   customization.value = it

                },
                onError = {
                    Log.e("身份设ds置","(MineViewModel.kt:30)->>失败了$it ")
                },
                onComplete = {
                    Log.e("身份设ds置","(MineViewModel.kt:30)-> onComplete ")
                }
            )
            .lifeCycle()
    }

    fun getAllIdentify(redid:String?){
        Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口方法$redid ")

        apiService.getAllIdentify(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口正常了")
                 allIdentifies.value = it

                },
                onError = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)-发生了错误$it")
                onErrorAction.value="没有身份就是它的身份"
                },
                onComplete = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口 完成了")

                }
            )
            .lifeCycle()

    }





    fun updateStatus(identityId:String,type:String,redid: String){
        apiService.uploadDisplayIdentity(identityId)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    Log.e("wxtadsgdgdg","(IdentityViewModel.kt:99)->> 身份设置成功${identityId}")
                    redrockApiStatusUpdate.value=it
                    if (type.equals("个性身份")){
                        getCustomization(redid)

                    }else{
                        getAuthenticationStatus(redid)
                    }
                    getShowIdentify(redid)
                },
                onError = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口失败了$it ")
                },
                onComplete = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口 完成了")

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
                    Log.e("wxtadsgdgdg","(IdentityViewModel.kt:99)->> 获取身份成功${it.data.id}")
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