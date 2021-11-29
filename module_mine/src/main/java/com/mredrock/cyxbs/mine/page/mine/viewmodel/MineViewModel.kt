package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.ApiService
import com.mredrock.cyxbs.mine.network.model.PersonalCount
import com.mredrock.cyxbs.mine.network.model.UserInfo
import com.mredrock.cyxbs.mine.util.apiService
import okhttp3.MultipartBody
import retrofit2.http.Part

class MineViewModel: BaseViewModel() {


    val _userInfo = MutableLiveData<UserInfo>()
    val _isUserInfoFail  =MutableLiveData<Boolean>()
    val _isChangeSuccess = MutableLiveData<Boolean>()
    val redRockApiStatusDelete = MutableLiveData<RedrockApiStatus>()
       val _PersonalCont=MutableLiveData<PersonalCount>()
    val _redRockApiChangeUsercount = MutableLiveData<RedrockApiStatus>()
   fun  getUserInfo(redid:String?){
       if (redid!=null){
           apiService.getPersonInfo(redid)
               .setSchedulers()
               .doOnErrorWithDefaultErrorHandler { true }
               .safeSubscribeBy(
                   onNext = {
                       _userInfo.value=it
                   },
                   onError = {

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

                       _userInfo.value=it
                   },
                   onError = {

                       _isUserInfoFail.value=true
                   },
                   onComplete = {

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
                    Log.i("背景图片",it.status.toString())
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
                   Log.e("wxtag身份","(MineViewModel.kt:92)->>删除身份成功 ")
                    redRockApiStatusDelete.value = it

                },
                onError = {

                },
                onComplete = {


                }
            )
            .lifeCycle()
    }

    fun getPersonalCount(redid: String?){
        apiService.getPersonalCount(redid)
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                   _PersonalCont.value=it
                },
                onError = {

                },
                onComplete = {

                }
            )
            .lifeCycle()
    }

    fun changeFocusStatus(redid: String?){
        ApiGenerator.getApiService(ApiService::class.java)
            .changeFocusStatus(redid)
            .setSchedulers()
            .doOnError {
                toastEvent.value = R.string.mine_person_change_focus_status_failed

            }
            .safeSubscribeBy {
                _redRockApiChangeUsercount.value=it
            }
    }

}