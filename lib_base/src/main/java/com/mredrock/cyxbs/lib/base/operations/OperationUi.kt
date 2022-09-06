package com.mredrock.cyxbs.lib.base.operations

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.utils.RxjavaLifecycle
import com.mredrock.cyxbs.lib.base.utils.ToastUtils
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.disposables.Disposable

/**
 *
 * 业务层的 Activity 和 Fragment 的共用函数
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 20:58
 */
interface OperationUi : ToastUtils, RxjavaLifecycle {
  
  /**
   * 根布局
   */
  val rootView: View
  
  /**
   * View 的 LifecycleOwner
   */
  fun getViewLifecycleOwner(): LifecycleOwner
  
  /**
   * 如果没有登录则会引导去登录界面
   */
  fun doIfLogin(msg: String? = "此功能", next: () -> Unit) {
    val verifyService = IAccountService::class.impl.getVerifyService()
    if (verifyService.isLogin()) {
      next()
    } else {
      verifyService.askLogin(rootView.context, "请先登录才能使用${msg}哦~")
    }
  }
  
  // Rxjava 自动关流
  @Deprecated("内部方法，禁止调用", level = DeprecationLevel.HIDDEN)
  override fun onAddRxjava(disposable: Disposable) {
    getViewLifecycleOwner().lifecycle.addObserver(
      object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          if (event.targetState == Lifecycle.State.DESTROYED) {
            source.lifecycle.removeObserver(this)
            disposable.dispose() // 在 DESTROYED 时关掉流
          } else {
            if (disposable.isDisposed) {
              // 如果在其他生命周期时流已经被关了，就取消该观察者
              source.lifecycle.removeObserver(this)
            }
          }
        }
      }
    )
  }
}