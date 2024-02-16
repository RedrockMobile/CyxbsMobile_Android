package com.mredrock.cyxbs.single

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

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
   *
   * 这里面应该启动 java 文件夹下的 activity 或者 Fragment，
   * 如果你有特殊需求，需要在 debug 文件夹下新建 activity，
   * 则参考 lib_crash 的设置，单独提供 AndroidManifest 文件
   */
  fun getPage(activity: SingleModuleActivity): Page

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
  class FragmentPage(val fragment: SingleModuleActivity.() -> Fragment): Page

  /**
   * 用于直接触发一个 action，比如直接内部调用 startActivity
   *
   * 返回 null 则不会 finish [SingleModuleActivity]
   */
  class ActionPage(val action: SingleModuleActivity.() -> Any?): Page
}