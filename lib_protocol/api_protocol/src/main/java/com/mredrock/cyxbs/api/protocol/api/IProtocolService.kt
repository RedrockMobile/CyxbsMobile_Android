package com.mredrock.cyxbs.api.protocol.api

import com.alibaba.android.arouter.facade.template.IProvider

interface IProtocolService : IProvider {
    fun getScheme(): String
    fun jump(uri: String)
    fun register(schema: String)
}