package com.mredrock.cyxbs.mine.util

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.ApiGeneratorForAnother
import com.mredrock.cyxbs.mine.network.ApiService

/**
* Created by zia on 2018/8/20.
*/
val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }

//临时使用，因为后端测试的baseurl与apiService不同，故作区分
val apiServiceForSign: ApiService by lazy { ApiGeneratorForAnother.getApiService(ApiService::class.java) }