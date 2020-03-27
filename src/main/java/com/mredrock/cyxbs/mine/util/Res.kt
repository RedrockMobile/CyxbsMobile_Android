package com.mredrock.cyxbs.mine.util

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.ApiGeneratorForSign
import com.mredrock.cyxbs.mine.network.ApiService

/**
* Created by zia on 2018/8/20.
*/
val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }

//临时使用，因为后端测试的baseurl与apiService不同，故作区分
val apiServiceForSign: ApiService by lazy { ApiGeneratorForSign.getApiService(ApiService::class.java) }