package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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

    val _isChangeSuccess = MutableLiveData<Boolean>()


   fun  getUserInfo(){
        apiService.getPersonInfo()
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                   Log.e("wxtasadasg","(MineViewModel.kt:30)->>成功了 ")
                    _userInfo.postValue(it)
                },
                onError = {

                },
                onComplete = {

                }
            )
            .lifeCycle()
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



}