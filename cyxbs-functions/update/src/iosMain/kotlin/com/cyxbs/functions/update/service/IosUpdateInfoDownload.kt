package com.cyxbs.functions.update.service

import com.cyxbs.components.utils.extensions.toast
import com.cyxbs.functions.update.bean.APP_STORE_ID
import com.cyxbs.functions.update.dialog.IPlatformUpdateInfoDownload
import com.g985892345.provider.api.annotation.ImplProvider
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@ImplProvider
class IosUpdateInfoDownload : IPlatformUpdateInfoDownload {
  override fun clickDownload(downloadUrl: String) {
    // 该入口只负责掌邮自身更新，始终打开固定的产品页。
    UIApplication.sharedApplication.openURL(
      url = NSURL(string = "itms-apps://apps.apple.com/cn/app/id$APP_STORE_ID"),
      options = emptyMap<Any?, Any>(),
      completionHandler = { success ->
        if (!success) "无法打开 App Store，请稍后重试".toast()
      },
    )
  }
}
