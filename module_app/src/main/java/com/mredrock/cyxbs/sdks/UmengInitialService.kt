package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.BuildConfig
import com.mredrock.cyxbs.spi.SdkService
import com.mredrock.cyxbs.spi.SdkManager
import com.umeng.commonsdk.UMConfigure
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *@author ZhiQiang Tu
 *@time 2022/3/24  19:42
 *@signature 我将追寻并获取我想要的答案
 */
@AutoService(SdkService::class)
class UmengInitialService : SdkService {

    override fun initialWithoutConstraint(manager: SdkManager) {
        val context = manager.application.applicationContext
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }
        UMConfigure.preInit(context, BuildConfig.UM_APP_KEY, "official")

        listOf(Unit).toObservable()
            .map {
                UMConfigure.init(
                    context,
                    BuildConfig.UM_APP_KEY,
                    "official",
                    UMConfigure.DEVICE_TYPE_PHONE,
                    ""
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()

    }

}