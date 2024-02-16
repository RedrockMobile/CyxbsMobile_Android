package com.mredrock.cyxbs.lib.debug.crash

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.utils.network.INetworkConfigService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 用于 debug 时保存网络请求，因为异常触发时进程被摧毁，Pandora 记录的请求会被清空
 *
 * @author 985892345
 * @date 2024/2/16 19:24
 */
@AutoService(INetworkConfigService::class)
class CrashNetworkConfigService : INetworkConfigService {

  companion object {
    val apiResultList by lazy {
      CopyOnWriteArrayList<ApiResult>()
    }
  }

  override fun onCreateOkHttp(builder: OkHttpClient.Builder) {
    builder.addInterceptor {
      // 专门用于收集信息的拦截器
      val request = it.request()
      val apiResult = ApiResult(request.newBuilder().build())
      apiResultList.add(apiResult)
      try {
        it.proceed(request).also { response ->
          // 如果请求成功了就记录新的 request
          apiResult.request = response.request.newBuilder().build()
          apiResult.response = response.newBuilder().build()
        }
      } catch (e: Exception) {
        apiResult.throwable = e
        throw e
      }
    }
  }

  class ApiResult(var request: Request) {
    var response: Response? = null
    var throwable: Throwable? = null
  }
}