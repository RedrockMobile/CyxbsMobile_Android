package com.mredrock.cyxbs.lib.utils.logger.network

import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @author : why
 * @time   : 2023/12/4 17:23
 * @bless  : God bless my code
 */
internal interface TrackingApiService {
  companion object {
    /**
     * LoggerApiService的实例
     */
    val INSTANCE by lazy {
      ApiGenerator.getApiService(TrackingApiService::class)
    }
  }

  @POST("/data-middle-office/stats")
  suspend fun trackEvent(@QueryMap params: Map<String, String>): ApiWrapper<TrackingResponseBody>
}