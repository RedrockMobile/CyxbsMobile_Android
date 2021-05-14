package com.mredrock.cyxbs.protocol.service

import android.content.Context
import android.net.Uri
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.protocol.PROTOCOL_SERVICE
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.protocol.activity.WebContainerActivity
import com.mredrock.cyxbs.protocol.bean.UriBean


@Route(path = PROTOCOL_SERVICE, name = PROTOCOL_SERVICE)
class ProtocolService : IProtocolService {

    var mScheme: String? = null

    private var context: Context? = null
    override fun init(context: Context?) {
        this.context = context
    }


    override fun register(schema: String) {
        mScheme = schema
    }

    override fun getScheme() = mScheme ?: throw Exception("该app还未注册scheme")

    override fun jump(uri: String) {
        mScheme ?: throw NullPointerException("应用注册scheme不能为空")
        val (scheme, host, path, queryMap) = parse(uri)
        when (scheme) {
            mScheme -> {
                val build = ARouter.getInstance().build(path)
                build.withBoolean("isFromReceive", true)
                for (mutableEntry in queryMap) {
                    build.withString(mutableEntry.key, mutableEntry.value)
                }
                build.navigation(BaseApp.context)
            }
            "http", "https" -> {
                context?.let { WebContainerActivity.loadWebPage(it, uri) }
            }
        }
    }


    private fun parse(uri: String): UriBean {
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
}