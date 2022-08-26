package com.mredrock.cyxbs.lib.debug.pandora

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.lib.base.spi.InitialManager
import com.mredrock.cyxbs.lib.base.spi.InitialService
import tech.linjiang.pandora.Pandora
import tech.linjiang.pandora.util.SensorDetector

/**
 * 一个很强的手机开发辅助工具 https://www.wanandroid.com/blog/show/2526
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/20 23:52
 */
@AutoService(InitialService::class)
class PandoraInitialService: InitialService, SensorDetector.Callback {
  
  companion object {
    lateinit var application: Application
      private set
  
    var sSpPandoraIsOpen: Boolean
      get() = application.getSharedPreferences("pandora", Context.MODE_PRIVATE).getBoolean("isOpen", false)
      set(value) {
        application.getSharedPreferences("pandora", Context.MODE_PRIVATE).edit {
          putBoolean("isOpen", value)
        }
      }
  }
  
  override fun onMainProcess(manager: InitialManager) {
    super.onMainProcess(manager)
    application = manager.application
    Pandora.get().disableShakeSwitch() // 取消 Pandora 默认的摇一摇打开方法
    SensorDetector(this)
  }
  
  override fun shakeValid() {
    if (sSpPandoraIsOpen) {
      Pandora.get().open()
    } else {
      application.startActivity(
        Intent(application, PandoraActivity::class.java)
          .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      )
    }
  }
}