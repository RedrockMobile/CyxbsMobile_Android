package com.mredrock.cyxbs.lib.utils.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.reflect.KClass

/**
 * 网络请求底层逻辑实现类
 *
 * 主要作用是分离网络请求底层实现逻辑，以后动模块的话好改动
 *
 * @author 985892345
 * 2022/12/21 14:49
 */
interface INetworkService {
  
  /**
   * 带 token 的请求
   */
  fun <T : Any> getApiService(clazz: KClass<T>): T
  
  fun createSelfRetrofit(
    retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null,
    okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null
  ): Retrofit
}