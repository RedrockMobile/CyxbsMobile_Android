package com.mredrock.cyxbs.lib.base.dailog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/22 13:44
 */
class TestDialog(context: Context) : Dialog(context) {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // 取消 dialog 默认背景
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    setContentView(com.mredrock.cyxbs.common.R.layout.common_dialog)
  }
}