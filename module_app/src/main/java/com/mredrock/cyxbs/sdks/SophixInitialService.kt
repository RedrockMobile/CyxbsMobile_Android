package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.mredrock.cyxbs.lib.base.spi.InitialService
import com.taobao.sophix.SophixManager

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/21 15:34
 */
@AutoService(InitialService::class)
class SophixInitialService : InitialService {
  override fun onPrivacyAgreed(manager: InitialManager) {
    super.onPrivacyAgreed(manager)
    SophixManager.getInstance().queryAndLoadNewPatch() // 这个会获取
  }
}