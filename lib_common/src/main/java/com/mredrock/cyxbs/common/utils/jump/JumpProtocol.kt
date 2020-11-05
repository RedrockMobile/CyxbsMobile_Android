package com.mredrock.cyxbs.common.utils.jump

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import java.lang.NullPointerException

/**
 * @author Jovines
 * create 2020-09-18 4:34 PM
 * description: 统一跳转协议封装
 */
object JumpProtocol {

    var mScheme: String? = null

    fun register(scheme: String) {
        mScheme = scheme
    }

    fun parse(uri: String): UriBean {
        val parse = Uri.parse(uri)
        val mutableMap = mutableMapOf<String, String>()
        parse.queryParameterNames.filter { !it.isNullOrEmpty() }.forEach {
            mutableMap[it] = parse.getQueryParameter(it) ?: ""
        }
        return UriBean(
                parse.scheme ?: "",
                parse.host ?: "",
                parse.path ?: "",
                mutableMap
        )
    }

    fun start(uri: String) {
        mScheme ?: throw NullPointerException("应用注册scheme不能为空")
        val (scheme, host, path, queryMap) = parse(uri)
        when (scheme) {
            mScheme -> {
                val build = ARouter.getInstance().build(path)
                for (mutableEntry in queryMap) {
                    build.withString(mutableEntry.key, mutableEntry.value)
                }
                build.navigation(BaseApp.context)
            }
            "http", "https" -> {
                Toast.makeText(BaseApp.context, "跳转web", Toast.LENGTH_SHORT).show()
            }
        }
    }


    data class UriBean(
            val scheme: String,
            val host: String,
            val path: String,
            val queryMap: MutableMap<String, String>
    )
}