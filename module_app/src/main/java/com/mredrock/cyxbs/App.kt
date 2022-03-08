package com.mredrock.cyxbs

import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.init.InitARouter
import com.mredrock.cyxbs.init.InitBugly
import com.mredrock.cyxbs.init.crash.InitCrash
import com.mredrock.cyxbs.init.umeng.InitUMeng
import com.taobao.sophix.SophixManager
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created By jay68 on 2018/8/8.
 */
class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        /*initTasks()
        //若以后还会有这种非必须在application启动时初始化的第三方SDK请写在InitTask中然后添加到这里的just里面
        Observable.just(
            initBugly(appContext)
        ).subscribeOn(Schedulers.computation()).doOnNext { it() }.subscribe()
        SophixManager.getInstance().queryAndLoadNewPatch()*/
    }

    private fun initTasks() {
        mInitTasks.forEach {
            it.init(this)
        }
    }

    private val mInitTasks = listOf(
        InitARouter,
        InitUMeng,
        InitCrash,
        InitBugly
    )
}
