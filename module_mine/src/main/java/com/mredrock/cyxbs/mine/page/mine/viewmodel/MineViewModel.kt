package com.mredrock.cyxbs.mine.page.mine.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.doOnErrorWithDefaultErrorHandler
import com.mredrock.cyxbs.common.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.mine.network.model.UserInfo
import com.mredrock.cyxbs.mine.util.apiService

class MineViewModel: BaseViewModel() {


    val _userInfo = MutableLiveData<UserInfo>()



   fun  getUserInfo(){
        apiService.getPersonInfo()
            .setSchedulers()
            .doOnErrorWithDefaultErrorHandler { true }
            .safeSubscribeBy(
                onNext = {
                  Log.e("测试的","(MineViewModel.kt:28)->>成功了 ")
                    _userInfo.postValue(it)
                },
                onError = {
                    Log.i("测试的",it.message)
                },
                onComplete = {
                    Log.i("测试的"," 完了完了")
                }
            )
            .lifeCycle()
    }





}