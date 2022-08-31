package com.mredrock.cyxbs.update.model

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.utils.getAppVersionCode
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Create By Hosigus at 2020/5/2
 */
object AppUpdateModel {
    
    val APP_VERSION_CODE: Long = getAppVersionCode(appContext)
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
        ApiGenerator.getCommonApiService(AppUpdateApiService::class)
            .getUpdateInfo()
            .onErrorResumeNext {
                getSecondUpdateRetrofit().create(AppUpdateApiService::class.java)
                    .getUpdateInfo()
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                status.value = AppUpdateStatus.ERROR
            }.unsafeSubscribeBy {
                updateInfo = it
                status.value = when {
                    it.versionCode <= APP_VERSION_CODE -> AppUpdateStatus.VALID
                    else -> AppUpdateStatus.DATED
                }
            }
    }

    /**
     * 当默认域名请求错误
     * 更换备用域名尝试更新
     */
    private fun getSecondUpdateRetrofit() = Retrofit.Builder()
        .baseUrl("http://hongyan.cqupt.edu.cn")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .build()
}