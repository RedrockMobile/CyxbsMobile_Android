package com.mredrock.cyxbs.mine.util

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.mine.network.ApiService

/**
* Created by zia on 2018/8/20.
*/
val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }
