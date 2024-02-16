package com.mredrock.cyxbs.single.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.single.ISingleModuleEntry

/**
 * 用于单模块调试启动的 activity
 *
 * @author 985892345
 * @date 2023/9/7 00:13
 */
class BaseSingleModuleActivity : BaseActivity() {

  private val mSingleModuleEntry by lazyUnlock {
    ServiceManager(ISingleModuleEntry::class)
  }

  override val isCancelStatusBar: Boolean
    get() = mSingleModuleEntry.isCancelStatusBar

  override val isPortraitScreen: Boolean
    get() = mSingleModuleEntry.isPortraitScreen

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (mSingleModuleEntry.isNeedLogin) {
      val isLogin = IAccountService::class.impl
        .getVerifyService()
        .isLogin()
      if (!isLogin) {
        ILoginService::class.impl
          .startLoginActivity(this::class.java)
        safeFinish()
      } else {
        startCreatePage(savedInstanceState)
      }
    } else {
      startCreatePage(savedInstanceState)
    }
  }

  private fun startCreatePage(savedInstanceState: Bundle?) {
    when (val page = mSingleModuleEntry.getPage(this)) {
      is ISingleModuleEntry.FragmentPage -> {
        replaceFragment(android.R.id.content) {
          page.fragment.invoke(this@BaseSingleModuleActivity)
        }
      }
      is ISingleModuleEntry.ActionPage -> {
        if (page.action.invoke(this) != null) {
          safeFinish()
        }
      }
    }
  }

  private var hasFinish = false

  private fun safeFinish() {
    if (hasFinish) return
    hasFinish = true
    mainLooper.queue.addIdleHandler {
      // 在队列空闲时 finish 当前 Activity
      // 防止另一个 activity 还没有完全打开
      finish()
      false
    }
  }
}