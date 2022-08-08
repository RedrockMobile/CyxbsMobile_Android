package com.mredrock.cyxbs.lib.utils.network

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:33
 */

// TODO 因为目前 ApiGenerator 未迁移过来，所以地址暂未改
//const val DEBUG_URL = "https://www.wanandroid.com"
//const val RELEASE_URL = DEBUG_URL
//
//fun getBaseUrl() = if (BuildConfig.DEBUG) DEBUG_URL else RELEASE_URL
fun getBaseUrl() = com.mredrock.cyxbs.common.config.getBaseUrl()