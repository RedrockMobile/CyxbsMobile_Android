package com.mredrock.cyxbs.lib.utils.network

import com.google.gson.annotations.SerializedName
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.Serializable

/**
 * 不需要 token 时全局通用的接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 14:14
 */
interface CommonApiService : IApi {
  /**
   * 下发接口，目前适用于title--content，公共可用
   * exp:
   * ApiGenerator
   *     .getCommonApiService(CommonApiService::class.java)
   *     .getDownMessage(DownMessageParams("name"))
   */
  @Headers("Content-Type: application/json")
  @POST("/magipoke-text/text/get")
  fun getDownMessage(@Body downMessageParams: DownMessageParams): Single<ApiWrapper<DownMessage>>
  
  companion object {
    
    /**
     * 直接使用该实例即可，请不要每次都生成新的
     *
     * 或者使用 CommonApiService::class.commonApi 也是可以的
     */
    val INSTANCE by lazy {
      CommonApiService::class.commonApi
    }
  }
}

data class DownMessageParams(val name: String) : Serializable

data class DownMessage(
  @SerializedName("name")
  var name: String = "",
  @SerializedName("text")
  var textList: List<DownMessageText>
) : Serializable {
  data class DownMessageText(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("content")
    var content: String = ""
  ) : Serializable
}
