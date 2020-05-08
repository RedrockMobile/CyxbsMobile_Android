package com.mredrock.cyxbs.update.model

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.service.update.AppUpdateStatus
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.getAppVersionCode
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Create By Hosigus at 2020/5/2
 */
object AppUpdateModel {
    val APP_VERSION_CODE: Long = getAppVersionCode(BaseApp.context)
    val status: MutableLiveData<AppUpdateStatus> = MutableLiveData()
    var updateInfo: UpdateInfo? = null
        private set

    init {
        status.value = AppUpdateStatus.UNCHECK
    }

    fun checkUpdate() {
        if (status.value == AppUpdateStatus.CHECKING) {
            return
        }
        status.value = AppUpdateStatus.CHECKING
        Observable.create<UpdateInfo> { obEmitter ->
            ApiGenerator.getApiService(AppUpdateApiService::class.java)
                    .getUpdateInfo()
                    .safeSubscribeBy(onError = {
                        ApiGenerator.getApiService(getSecondUpdateRetrofit(), AppUpdateApiService::class.java)
                                .getUpdateInfo()
                                .safeSubscribeBy(onError = {
                                    obEmitter.onError(it)
                                }, onNext = {
                                    obEmitter.onNext(it)
                                })
                    }, onNext = {
                        obEmitter.onNext(it)
                    })
            obEmitter.onComplete()
        }
                .setSchedulers()
                .safeSubscribeBy(onError = {
                    // todo 建议统一异常上报接口，可以放DefaultErrorHandler里，然后把这个报上去
                    status.value = AppUpdateStatus.ERROR
                }, onNext = {
                    updateInfo = it
                    // todo 这里可以加更新信息的判断，包括状态 ${AppUpdateStatus.TEST} 等
                    status.value = when {
                        it.versionCode <= APP_VERSION_CODE -> AppUpdateStatus.VALID
                        else -> AppUpdateStatus.DATED
                    }
                })
    }

    /**
     * 当默认域名请求错误
     * 更换备用域名尝试更新
     */
    private fun getSecondUpdateRetrofit() = Retrofit.Builder()
//            .baseUrl("http://hongyan.cqupt.edu.cn")
            .baseUrl("http://file.hosigus.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

}