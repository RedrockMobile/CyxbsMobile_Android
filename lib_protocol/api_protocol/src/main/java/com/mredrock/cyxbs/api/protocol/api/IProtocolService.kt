package com.mredrock.cyxbs.api.protocol.api

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface IProtocolService : IProvider {
    companion object{
        const val USER_PROTOCOL_URL =
            "https://fe-prod.redrock.cqupt.edu.cn/redrock-cqapp-protocol/user-agreement/index.html"
        const val PRIVACY_POLICY_URL =
            "https://fe-prod.redrock.cqupt.edu.cn/redrock-cqapp-protocol/privacy-notice/index.html"
    }
    fun getScheme(): String
    fun jump(uri: String)
    fun startLegalNoticeActivity(context: Context, url:String, title:String)
}