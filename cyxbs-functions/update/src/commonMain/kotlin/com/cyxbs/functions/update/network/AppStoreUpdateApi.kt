package com.cyxbs.functions.update.network

import com.cyxbs.components.config.serializable.defaultJson
import com.cyxbs.components.utils.network.HttpClientNoToken
import com.cyxbs.functions.update.api.UpdateInfo
import com.cyxbs.functions.update.bean.APP_STORE_ID
import com.cyxbs.functions.update.bean.AppStoreLookupResult
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

internal suspend fun getAppStoreUpdateInfo(): UpdateInfo {
  val response = HttpClientNoToken.get("https://itunes.apple.com/lookup") {
    parameter("id", APP_STORE_ID)
    parameter("country", "cn")
    parameter("entity", "software")
  }
  // Lookup 返回 text/javascript，不能依赖 application/json 的 ContentNegotiation。
  return defaultJson.decodeFromString<AppStoreLookupResult>(response.bodyAsText()).toUpdateInfo()
}
