package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService
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
    // 获取热修包，这个会读取设备信息，需要放在同意用户协议后
    SophixManager.getInstance().queryAndLoadNewPatch()
  }
}