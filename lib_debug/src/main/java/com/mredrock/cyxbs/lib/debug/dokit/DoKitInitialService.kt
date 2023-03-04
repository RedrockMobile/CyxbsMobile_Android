package com.mredrock.cyxbs.lib.debug.dokit

import com.didichuxing.doraemonkit.DoKit
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService

/**
 * 比 Pandora 更牛逼的检测工具 DoKit
 *
 * 官网文档：https://xingyun.xiaojukeji.com/docs/dokit/#/androidGuide
 * github 仓库：https://github.com/didi/DoKit
 *
 * @author 985892345
 * 2023/3/3 21:28
 */
@AutoService(InitialService::class)
class DoKitInitialService : InitialService {
  override fun onAllProcess(manager: InitialManager) {
    super.onAllProcess(manager)
    DoKit.Builder(manager.application)
      .productId("需要使用平台功能的话，需要到 dokit.cn 平台申请id")
      .build()
  }
}