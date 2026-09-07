package com.cyxbs.pages.map.viewmodel

import com.eygraber.uri.Uri
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class MapComposeViewModel : CommonMapComposeViewModel() {

  override fun jumpToNavigation(endPlace: String) {
    val navigationUri = Uri.Builder()
      .scheme("baidumap")
      .authority("map")
      .appendPath("direction")
      .appendQueryParameter("origin", "我的位置")
      .appendQueryParameter("destination", endPlace)
      .appendQueryParameter("mode", "walking")
      .appendQueryParameter("src", "ios.redrock.cyxbs")
      .build()
    UIApplication.sharedApplication.openURL(
      url = NSURL(string = navigationUri.toString()),
      options = emptyMap<Any?, Any>(),
      completionHandler = { success ->
        if (!success) {
          // 未安装百度地图时，与 Android 一样打开目的地的网页版地图。
          val webUri = Uri.Builder()
            .scheme("https")
            .authority("api.map.baidu.com")
            .appendPath("geocoder")
            .appendQueryParameter("address", endPlace)
            .appendQueryParameter("output", "html")
            .appendQueryParameter("src", "webapp.baidu.openAPIdemo")
            .build()
          UIApplication.sharedApplication.openURL(
            url = NSURL(string = webUri.toString()),
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
          )
        }
      },
    )
  }
}
