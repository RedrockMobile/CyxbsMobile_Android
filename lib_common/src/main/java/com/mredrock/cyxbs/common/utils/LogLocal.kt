package com.mredrock.cyxbs.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DIR_LOG
import com.mredrock.cyxbs.common.config.OKHTTP_LOCAL_LOG
import java.util.*

@SuppressLint("StaticFieldLeak")
object LogLocal {
    private var logLocalHelper: LogLocalHelper? = null

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                if (logLocalHelper == null) {
                    logLocalHelper = LogLocalHelper(pid.toString(), filePath, OKHTTP_LOCAL_LOG)
                }
                logLocalHelper?.write(msg.obj as String)
            }
        }
    }
    private val filePath: String = "${Environment.getExternalStorageDirectory()}${DIR_LOG}/"
    private val pid = Process.myPid()

    fun log(tag: String = "tag", msg: String, throwable: Throwable? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BaseApp.appContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                BaseApp.appContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                val message = Message()
                message.what = 0
                message.obj = "$tag $msg"
                handler.sendMessage(message)
                Log.d("LogLocal", filePath)
            }
        } else {
            Log.d("LogLocal", "没有权限")
        }
    }
}