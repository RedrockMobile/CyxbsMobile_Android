package com.mredrock.cyxbs.update.model

import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.update.AppUpdateStatus
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.utils.get.getAppVersionCode
import com.mredrock.cyxbs.lib.utils.utils.get.getAppVersionName
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Create By Hosigus at 2020/5/2
 */
object AppUpdateModel {
    
    val APP_VERSION_CODE: Long = getAppVersionCode()
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
                // 当官网更新查询失败时会调用github的release更新查询
                // 请注意github发布release时tag请按v+versionName+'-'+versionCode这样的格式，
                // 例如versionName为6.8.0,versionCode为84,那么发布release的tag必须为v6.8.0-84,github查询更新才能更新成功,否则会更新失败
                ApiGenerator.getCommonApiService(AppUpdateApiService::class)
                    .getUpdateInfoByGithub().map {
                        it.run {
                            if (tag.matches("v\\d+\\.\\d+\\.\\d+-\\d+".toRegex())){
                                val strings = tag.split("-")
                                val versionName = strings[0].removeRange(0,1)
                                val versionCode = strings[1].toLong()
                                UpdateInfo(assets[0].downloadUrl, body, versionCode, versionName)
                                //github更新失败，这里抛出的异常将走到doOnError
                            }else throw RuntimeException("release的tag格式不正确，请修改重新上传。")
                        }
                    }
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                status.value = AppUpdateStatus.ERROR
            }.unsafeSubscribeBy {
                updateInfo = it
                status.value = when {
                    it.versionCode == APP_VERSION_CODE -> {
                        val name = getAppVersionName()
                        if (name != it.versionName) {
                            // 名字不相等，说明安装的版本有问题，可能是测试版
                            AppUpdateStatus.DATED
                        } else AppUpdateStatus.VALID
                    }
                    it.versionCode < APP_VERSION_CODE -> {
                        AppUpdateStatus.VALID
                    }
                    else -> AppUpdateStatus.DATED
                }
            }
    }
}