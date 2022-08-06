package com.mredrock.cyxbs.sdks

import com.google.auto.service.AutoService
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.spi.SdkManager
import com.mredrock.cyxbs.spi.SdkService
import tech.linjiang.pandora.Pandora
import tech.linjiang.pandora.util.SensorDetector

/**
 * 一个很强的手机开发辅助工具 https://www.wanandroid.com/blog/show/2526
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/20 23:52
 */
@AutoService(SdkService::class)
class PandoraInitialService: SdkService, SensorDetector.Callback {
  override fun onMainProcess(manager: SdkManager) {
    super.onMainProcess(manager)
    Pandora.get().disableShakeSwitch() // 取消 Pandora 默认的摇一摇打开方法
    SensorDetector(this)
  }
  
  // 摇一摇的回调
  override fun shakeValid() {
    val stuNumSet = setOf(
      "2020214988",
    )
    val stuNum = IAccountService::class.impl.getUserService().getStuNum()
    if (stuNumSet.contains(stuNum)) {
      Pandora.get().open()
    }
  }
}