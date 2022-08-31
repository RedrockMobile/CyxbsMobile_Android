package com.mredrock.cyxbs.lib.base.operations

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.utils.RxjavaLifecycle
import com.mredrock.cyxbs.lib.base.utils.ToastUtils
import com.mredrock.cyxbs.lib.utils.service.impl

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
}