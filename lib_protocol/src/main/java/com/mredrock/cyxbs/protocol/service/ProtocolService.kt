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

    private var mScheme = "cyxbs"

    private var context: Context? = null
    override fun init(context: Context?) {
        this.context = context
    }

    override fun getScheme() = mScheme

    /**
     * 跳转到内部自带 WebContainerActivity，或者带有 mScheme 的链接，跳转到对应的界面
     */
    override fun jump(uri: String) {
        mScheme
        val (scheme, host, path, queryMap) = parse(uri)
        when (scheme) {
            mScheme -> {
                val build = ARouter.getInstance().build(path)
                build.withBoolean("is_from_receive", true)
                for (mutableEntry in queryMap) {
                    build.withString(mutableEntry.key, mutableEntry.value)
                }
                build.navigation(BaseApp.appContext)
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