package com.mredrock.cyxbs

import com.mredrock.cyxbs.common.BaseApp
import com.taobao.sophix.SophixManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        //若以后还会有这种非必须在application启动时初始化的第三方SDK请写在InitTask中然后添加到这里的just里面
        Observable.just(
                initBugly(context)
        ).subscribeOn(Schedulers.computation()).doOnNext { it() }.subscribe()
        SophixManager.getInstance().queryAndLoadNewPatch()
    }
}