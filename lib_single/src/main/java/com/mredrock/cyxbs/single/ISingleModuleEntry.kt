package com.mredrock.cyxbs.single

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity

/**
 * 负责单模块调试的入口界面启动
 *
 * @author 985892345
 * @date 2023/9/7 00:08
 */
interface ISingleModuleEntry : IProvider {

  override fun init(context: Context) {}

  /**
   * 返回页面
   */
  fun getPage(activity: BaseSingleModuleActivity): Page

  /**
   * 是否需要登陆
   */
  val isNeedLogin: Boolean
    get() = true

  /**
   * 是否锁定竖屏
   */
  val isPortraitScreen: Boolean
    get() = true

  /**
   * 是否沉浸式状态栏
   */
  val isCancelStatusBar: Boolean
    get() = true

  sealed interface Page
  class FragmentPage(val fragment: BaseSingleModuleActivity.() -> Fragment): Page

  /**
   * 用于直接触发一个 action，比如直接内部调用 startActivity
   *
   * 返回 null 则不会 finish [BaseSingleModuleActivity]
   */
  class ActionPage(val action: BaseSingleModuleActivity.() -> Any?): Page
}