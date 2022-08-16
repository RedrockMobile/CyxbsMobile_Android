package com.mredrock.cyxbs.lib.utils.network

import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.extensions.mapOrThrowApiException
import com.mredrock.cyxbs.lib.utils.extensions.mapOrCatchApiException
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
 *     .mapOrCatchApiException {      // 当 errorCode 的值不为成功时抛错，并处理错误
 *         // 处理 ApiException 错误，注意：这里并不会处理断网时的 HttpException
 *     }
 *     .safeSubscribeBy {            // 如果是网络连接错误，则这里会默认处理
 *         // 成功的时候
 *     }
 * ```
 *
 * # 其他注意事项
 * ## Rxjava 或 Flow 的下游没任何输出（怎么处理断网时的 HttpException）
 *
 * 大概率是你直接用了 [mapOrCatchApiException]，而它只会处理 [ApiException]，如果你要处理其他网络错误，
 * 请把 [mapOrCatchApiException] 替换为 [mapOrThrowApiException]：
 * ```
 *     .mapOrThrowApiException()
 *     .doOnError {                    // Flow 操作符为 catch
 *         if (it is ApiException) {
 *             // 处理 ApiException 错误
 *         } else {
 *             // 处理其他网络错误
 *         }
 *     }
 * ```
 *
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
}

/**
 * 实现该接口后后直接使用写吗这种写法：
 * ```
 * ApiService::class.api
 *   .getXXX()
 * ```
 */
interface IApi {
  companion object {
    internal val MAP = HashMap<KClass<out IApi>, IApi>()
    internal val MAP_COMMON = HashMap<KClass<out IApi>, IApi>()
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