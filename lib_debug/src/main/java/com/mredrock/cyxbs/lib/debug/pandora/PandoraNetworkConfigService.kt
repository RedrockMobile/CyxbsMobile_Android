package com.mredrock.cyxbs.lib.debug.pandora

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.utils.network.INetworkConfigService
import okhttp3.OkHttpClient
import tech.linjiang.pandora.Pandora

/**
 * .
 *
 * @author 985892345
 * @date 2024/2/16 19:31
 */
@AutoService(INetworkConfigService::class)
class PandoraNetworkConfigService : INetworkConfigService {
  override fun onCreateOkHttp(builder: OkHttpClient.Builder) {
    builder.addInterceptor(Pandora.get().interceptor)
  }
}