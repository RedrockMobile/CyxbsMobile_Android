package com.mredrock.cyxbs.lib.base

import android.app.Application
import androidx.annotation.CallSuper
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.init.InitialManager
import com.mredrock.cyxbs.init.InitialService
import com.mredrock.cyxbs.config.sp.SP_PRIVACY_AGREED
import com.mredrock.cyxbs.config.sp.defaultSp
import java.util.*
import kotlin.properties.Delegates

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/26 14:01
 */
open class BaseApp : Application() {
  companion object {
    lateinit var baseApp: BaseApp
      private set
  }
  
  private var mInitialManager by Delegates.notNull<InitialManagerImpl>()
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    baseApp = this
    initARouter()
    initInitialService()
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
  }
  
  //隐私策略同意了
  fun privacyAgree(){
    mInitialManager.privacyAgree()
  }
  
  //没同意
  fun privacyDenied(){
    mInitialManager.privacyDenied()
  }
  
  /**
   * BaseApp 不能直接实现于 InitialManager，不然会导致其他模块缺失 app_init 依赖
   */
  private class InitialManagerImpl(override val application: Application) : InitialManager {
    
    private val loader by lazy {
      ServiceLoader.load(InitialService::class.java)
    }
  
    init {
      //由于android每开辟进程都会访问application的生命周期方法,所以为了保证sdk初始化无措，最好对其进行过滤。
      //因为有些sdk的初始化不是幂等的，即多次初始化会导致进程的crash。这样就会导致一些未知的问题。
      //所以解决方案就是对当前进程进程判断，只在main进程初始化sdk，其余进程默认不进行sdk的初始化。
      // (不排除某些sdk需要，比如友盟推送就需要在新开辟的:channel进行进行初始化)
      loader.forEach { it.onAllProcess(this) }
      if (isMainProcess()){
        onMainProcess()
      }else {
        onOtherProcess()
      }
    }
  
    //非主进程
    private fun onOtherProcess() {
      loader.forEach {
        it.onOtherProcess(this)
      }
    }
  
    //主进程
    private fun onMainProcess() {
      //不管是否同意隐私策略都调用
      loader.forEach {
        it.onMainProcess(this)
      }
      //同意了隐私策略
      if (defaultSp.getBoolean(SP_PRIVACY_AGREED, false) && isMainProcess()) {
        loader.forEach {
          it.onPrivacyAgreed(this)
        }
      }
    }
  
    //隐私策略同意了
    fun privacyAgree(){
      loader.forEach {
        it.onPrivacyAgreed(this)
      }
    }
  
    //没同意
    fun privacyDenied(){
      loader.forEach {
        it.onPrivacyDenied(this)
      }
    }
  }
}