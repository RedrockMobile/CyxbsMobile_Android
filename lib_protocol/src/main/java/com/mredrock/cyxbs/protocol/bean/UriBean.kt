package com.mredrock.cyxbs.protocol.bean

data class UriBean(
        val scheme: String,
        val host: String,
        val path: String,
        val queryMap: MutableMap<String, String>
)