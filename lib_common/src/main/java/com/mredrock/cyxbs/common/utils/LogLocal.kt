package com.mredrock.cyxbs.common.utils

import android.annotation.SuppressLint
import android.os.*
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DIR_LOG
import com.mredrock.cyxbs.common.config.OKHTTP_LOCAL_LOG
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 以前廖老师写的本地收集报错文件
 *
 * 在 关于我们 底部文字长按即可触发本地收集报错文件
 *
 * 解密请看飞书知识库
 */
@SuppressLint("StaticFieldLeak")
object LogLocal {
    private var logLocalHelper: LogLocalHelper? = null
    private val filePath: String = "${BaseApp.appContext.filesDir.absolutePath}${DIR_LOG}/"
    private val pid = Process.myPid()

    fun log(tag: String = "tag", msg: String, throwable: Throwable? = null) {
                Observable.create<String> {
                    it.onNext("$tag $msg")
                }
                    .subscribeOn(Schedulers.io())
                    .map {
                    if (logLocalHelper == null) {
                        logLocalHelper = LogLocalHelper(pid.toString(), filePath, OKHTTP_LOCAL_LOG)
                    }
                    logLocalHelper?.write(it)
                        Unit
                }.safeSubscribeBy {}
    }
}