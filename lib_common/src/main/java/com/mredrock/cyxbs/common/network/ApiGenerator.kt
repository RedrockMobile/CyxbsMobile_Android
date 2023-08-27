package com.mredrock.cyxbs.common.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 已被废弃的 lib_common 模块中的网络请求工具类
 *
 * 网络请求的示例代码请看 lib_utils 模块中的 ApiGenerator
 *
 * Created by AceMurder on 2018/1/24.
 */
object ApiGenerator {


    fun <T> getApiService(clazz: Class<T>) =
        ApiGenerator.getApiService(clazz)

    fun <T> getApiService(retrofit: Retrofit, clazz: Class<T>) = retrofit.create(clazz)

    fun <T> getCommonApiService(clazz: Class<T>) =
        ApiGenerator.getCommonApiService(clazz)
    /**
     * 通过此方法对得到单独的 Retrofit
     * @param retrofitConfig 配置Retrofit.Builder，已配置有
     * @see GsonConverterFactory
     * @see RxJava3CallAdapterFactory
     * null-> 默认BaseUrl
     * @param okHttpClientConfig 配置OkHttpClient.Builder，已配置有
     * @see HttpLoggingInterceptor
     * null-> 默认Timeout
     * @param tokenNeeded 是否需要添加token请求
     */
    fun createSelfRetrofit(
        retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null,
        okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null,
        tokenNeeded: Boolean
    ): Retrofit=ApiGenerator.createSelfRetrofit(retrofitConfig, okHttpClientConfig, tokenNeeded)
}