package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.AuthenticationStatus
import com.mredrock.cyxbs.mine.network.model.Status
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.util.apiService
import io.reactivex.Observable

class IdentityViewModel: BaseViewModel()  {


    val authenticationStatus = MutableLiveData<AuthenticationStatus>()
    val customization = MutableLiveData<AuthenticationStatus>()
    val allIdentifies = MutableLiveData<AuthenticationStatus>()
    val  redrockApiStatusUpdate = MutableLiveData<RedrockApiStatus>()

    val onErrorAction =MutableLiveData<String>()
    fun getAuthenticationStatus(redid:String){
        apiService.getAuthenticationStatus(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
              authenticationStatus.value=it

                },
                onError = {
                    Log.e("wxtasadasg","(MineViewModel.kt:30)->>失败了$it ")
                },
                onComplete = {
                    Log.e("wxtasadasg","(MineViewModel.kt:30)-> onComplete ")
                }
            )
            .lifeCycle()
    }

    fun getCustomization(redid:String){
        apiService.getAuthenticationStatus(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {

                   customization.value = it

                },
                onError = {
                    Log.e("wxtasadasg","(MineViewModel.kt:30)->>失败了$it ")
                },
                onComplete = {
                    Log.e("wxtasadasg","(MineViewModel.kt:30)-> onComplete ")
                }
            )
            .lifeCycle()
    }

    fun getAllIdentify(redid:String?){
        Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口方法 ")

        apiService.getAuthenticationStatus(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口正常了")
                 allIdentifies.value = it

                },
                onError = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)-发生了错误")
                onErrorAction.value="没有身份就是它的身份"
                },
                onComplete = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口 完成了")

                }
            )
            .lifeCycle()

    }





    fun updateStatus(identityId:String){
        apiService.uploadDisplayIdentity(identityId)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    redrockApiStatusUpdate.value = it

                },
                onError = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口失败了 ")
                },
                onComplete = {
                    Log.e("wxtasadasdasdasg","(MineViewModel.kt:30)->身份接口 完成了")

                }
            )
            .lifeCycle()
    }

}