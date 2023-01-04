package com.mredrock.cyxbs.lib.debug.pandora

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.auto.service.AutoService
import com.mredrock.cyxbs.api.init.InitialManager
import com.mredrock.cyxbs.api.init.InitialService
import com.mredrock.cyxbs.lib.debug.SecretActivity
import com.mredrock.cyxbs.lib.utils.utils.defaultImpl
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
  
  override fun onMainProcess(manager: InitialManager) {
    super.onMainProcess(manager)
    Pandora.get().disableShakeSwitch() // 取消 Pandora 默认的摇一摇打开方法
    SensorDetector(this)
    
    /*
    * 除了摇一摇以外，点击屏幕顶部状态栏下正中间区域 10 下也能打开
    * */
    manager.application.registerActivityLifecycleCallbacks(
      object : Application.ActivityLifecycleCallbacks by defaultImpl() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
          val decorView = activity.window.decorView as FrameLayout
          decorView.addView(
            View(activity).apply {
              layoutParams = FrameLayout.LayoutParams(160, 160, Gravity.CENTER_HORIZONTAL)
              setOnClickListener {
                val key = 76456823
                val times = it.getTag(key) as? Int
                if (times == null) {
                  it.setTag(key, 1)
                } else {
                  if (times == 3) {
                    // 点击的第 3 下打开
                    shakeValid()
                    it.setTag(key, 1)
                  } else {
                    it.setTag(key, times + 1)
                  }
                }
              }
            }
          )
        }
      }
    )
  }
  
  override fun shakeValid() {
    SecretActivity.tryStart {
      Pandora.get().open()
    }
  }
}