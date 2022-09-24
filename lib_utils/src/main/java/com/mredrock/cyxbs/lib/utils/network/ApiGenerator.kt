package com.mredrock.cyxbs.lib.utils.network

import com.mredrock.cyxbs.common.network.ApiGenerator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.reflect.KClass

/**
 * # 用法
 * ## 命名规则
 * XXXApiService 接口，命名规则，以 ApiService 结尾
 *
 * ## 接口模板
 * ```
 * interface XXXApiService {
 *
 *     @GET("/aaa/bbb")
 *     fun getXXX(): Single<ApiWrapper<Bean>>
 *     // 统一使用 ApiWrapper 或 ApiStatus 包装，注意 Bean 类要去掉 data 字段，不然会报 json 错误
 *
 *     companion object {
 *         val INSTANCE by lazy {
 *             ApiGenerator.getXXXApiService(XXXApiService::class)
 *         }
 *     }
 * }
 *
 * 或者：
 * interface XXXApiService : IApi
 *
 * XXXApiService::class.impl
 *     .getXXX()
 * ```
 *
 * ## 示例代码
 * ```
 * ApiService.INSTANCE.getXXX()
 *     .subscribeOn(Schedulers.io())  // 线程切换
 *     .observeOn(AndroidSchedulers.mainThread())
 *     .mapOrInterceptException {     // 当 errorCode 的值不为成功时抛错，并拦截异常
 *         // 这里面可以使用 DSL 写法，更多详细用法请看该方法注释
 *     }
 *     .safeSubscribeBy {            // 如果是网络连接错误，则这里会默认处理
 *         // 成功的时候
 *         // 如果是仓库层，请使用 unsafeSubscribeBy()
 *     }
 * ```
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:30
 */
object ApiGenerator {
  
  /**
   * 带 token 的请求
   */
  fun <T : Any> getApiService(clazz: KClass<T>): T {
    return ApiGenerator.getApiService(clazz.java)
  }
  
  /**
   * 不带 token 的请求
   */
  fun <T : Any> getCommonApiService(clazz: KClass<T>): T {
    return ApiGenerator.getCommonApiService(clazz.java)
  }
  
  
  fun createSelfRetrofit(
    tokenNeeded: Boolean,
    retrofitConfig: ((Retrofit.Builder) -> Retrofit.Builder)? = null,
    okHttpClientConfig: ((OkHttpClient.Builder) -> OkHttpClient.Builder)? = null
  ): Retrofit {
    return ApiGenerator.createSelfRetrofit(retrofitConfig, okHttpClientConfig, tokenNeeded)
  }
}

/**
 * 实现该接口后后直接使用这种写法：
 * ```
 * ApiService::class.api
 *   .getXXX()
 * ```
 */
interface IApi {
  companion object {
    val MAP = HashMap<KClass<out IApi>, IApi>()
    val MAP_COMMON = HashMap<KClass<out IApi>, IApi>()
  }
}

/**
 * 带 token 的请求
 */
@Suppress("UNCHECKED_CAST")
val <I : IApi> KClass<I>.api: I
  get() = IApi.MAP.getOrPut(this) {
    com.mredrock.cyxbs.lib.utils.network.ApiGenerator.getApiService(this)
  } as I

/**
 * 不带 token 的请求
 */
@Suppress("UNCHECKED_CAST")
val <I : IApi> KClass<I>.commonApi: I
  get() = IApi.MAP_COMMON.getOrPut(this) {
    com.mredrock.cyxbs.lib.utils.network.ApiGenerator.getCommonApiService(this)
  } as I