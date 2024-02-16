package com.mredrock.cyxbs.lib.utils.network

import okhttp3.OkHttpClient

/**
 * 一些额外的网络配置放这里面，请使用 @AutoService 进行注册
 *
 * @author 985892345
 * @date 2024/2/16 19:15
 */
interface INetworkConfigService {
  fun onCreateOkHttp(builder: OkHttpClient.Builder)
}