package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.UserInfo
import com.mredrock.cyxbs.mine.util.apiService
import okhttp3.MultipartBody
import retrofit2.http.Part

class MineViewModel: BaseViewModel() {


    val _userInfo = MutableLiveData<UserInfo>()
    val _isUserInfoFail  =MutableLiveData<Boolean>()
    val _isChangeSuccess = MutableLiveData<Boolean>()
    val redRockApiStatusDelete = MutableLiveData<RedrockApiStatus>()

   fun  getUserInfo(redid:String?){
      Log.e("wxtasadasg","(MineViewModel.kt:26)->>调用了网络请求吗$redid ")
       if (redid!=null){
           apiService.getPersonInfo(redid)
               .setSchedulers()
              // .doOnErrorWithDefaultErrorHandler { true }
               .safeSubscribeBy(
                   onNext = {
                       Log.e("wxtasadasg","(MineViewModel.kt:26)->>成共了嘛 ")
                       _userInfo.value=it
                   },
                   onError = {
                       Log.e("wxtasadasg","(MineViewModel.kt:26)->>失败了")
                       _isUserInfoFail.value=true
                   },
                   onComplete = {

                   }
               )
               .lifeCycle()
       }else{
           apiService.getPersonInfo(redid)
               .setSchedulers()
               .doOnErrorWithDefaultErrorHandler { true }
               .safeSubscribeBy(
                   onNext = {
                       Log.e("wxtasadasg","(MineViewModel.kt:30)->>成功了 ")
                       _userInfo.value=it
                   },
                   onError = {
                       Log.e("wxtasadasg","(MineViewModel.kt:30)->>失败了 ")
                       _isUserInfoFail.value=true
                   },
                   onComplete = {
                       Log.e("wxtasadasg","(MineViewModel.kt:30)-> onComplete ")
                   }
               )
               .lifeCycle()
       }

    }

    fun changePersonalBackground(@Part file: MultipartBody.Part){
        apiService.changePersonalBackground(file)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    _isChangeSuccess.value =true

                },
                onError = {
                   _isChangeSuccess.value = false
                },
                onComplete = {

                }
            )
            .lifeCycle()
    }

    fun deleteStatus(identityId:String){
        apiService.deleteIdentity(identityId)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                    redRockApiStatusDelete.value = it

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