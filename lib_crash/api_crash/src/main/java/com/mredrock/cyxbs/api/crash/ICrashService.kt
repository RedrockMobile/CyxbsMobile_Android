package com.mredrock.cyxbs.api.crash

import android.app.Dialog
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * .
 *
 * @author 985892345
 * 2023/3/1 10:24
 */
interface ICrashService : IProvider{
  
  /**
   * 创建用于显示异常的 Dialog，可用于捕获异常后让用户复制异常信息向我们反馈
   *
   * 内部使用栈顶 Activity，可在任意地方调用
   *
   * 记得主动调用 show()（注意：非主线程会无法显示）
   */
  fun createCrashDialog(throwable: Throwable): Dialog
}