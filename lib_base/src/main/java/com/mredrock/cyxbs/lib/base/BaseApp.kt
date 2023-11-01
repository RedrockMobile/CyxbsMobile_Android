package com.mredrock.cyxbs.lib.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.CallSuper
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.lib.base.utils.InitialManagerImpl
import com.mredrock.cyxbs.lib.utils.utils.impl.ActivityLifecycleCallbacksImpl
import java.lang.ref.WeakReference
import java.util.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 14:01
 */
open class BaseApp : Application() {
  companion object {
    @SuppressLint("StaticFieldLeak")
    lateinit var baseApp: BaseApp
      private set
  
    /**
     * 获取栈顶的 Activity
     *
     * 栈顶 Activity 可用于实现全局 dialog
     */
    @SuppressLint("StaticFieldLeak")
    lateinit var topActivity: WeakReference<Activity>
      private set

    /**
     * 使用 androidId 来代替设备 id
     *
     * android id 会在设备重置后还原，并且不具有唯一性
     *
     * 但都重置系统了，可以不用管这么多
     */
    fun getAndroidID(): String {
      return Settings.Secure.getString(baseApp.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * 获取设备型号
     */
    @SuppressLint("PrivateApi")
    fun getDeviceModel(): String {
      return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
  }
  
  private lateinit var mInitialManager: InitialManagerImpl
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    baseApp = this
    initARouter()
    initInitialService()
    initActivityManger()
  }
  
  /**
   * 在单模块调试时也需要该 ARouter，所以直接在这里初始化
   */
  private fun initARouter() {
    if (BuildConfig.DEBUG) {
      ARouter.openDebug()
      ARouter.openLog()
    }
    ARouter.init(this)
  }
  
  private fun initInitialService() {
    mInitialManager = InitialManagerImpl(this)
    mInitialManager.init()
  }
  
  //隐私策略同意了
  fun privacyAgree(){
    mInitialManager.privacyAgree()
  }
  
  //没同意
  fun privacyDenied(){
    mInitialManager.privacyDenied()
  }
  
  private fun initActivityManger() {
    registerActivityLifecycleCallbacks(
      object : ActivityLifecycleCallbacksImpl {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
          topActivity = WeakReference(activity)
        }
  
        override fun onActivityResumed(activity: Activity) {
          if (activity !== topActivity.get()) {
            topActivity = WeakReference(activity)
          }
        }
      }
    )
  }
}