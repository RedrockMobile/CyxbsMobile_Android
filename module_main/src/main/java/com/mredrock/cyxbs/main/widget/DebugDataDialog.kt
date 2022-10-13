package com.mredrock.cyxbs.main.widget

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.main.R


/**
 *
 * Author Jovines
 * Date 2020/8/29 20:27
 * Description: 用于展示debug测试数据的一个dialog，入口目前是在debug包下长按我的tab
 *
 **/
class DebugDataDialog(context: Context) : Dialog(context) {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_dialog_test_data)
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    initDialog()
  }
  
  private fun initDialog() {
    val tvDeviceId = findViewById<TextView>(R.id.device_id)
    val tvDeviceData = findViewById<TextView>(R.id.device_data)
    tvDeviceId.setOnLongClickListener {
      textViewToClipboard(tvDeviceId.text)
      true
    }
    tvDeviceData.setOnLongClickListener {
      textViewToClipboard(tvDeviceData.text)
      true
    }
  }
  
  private fun textViewToClipboard(text: CharSequence) {
    //获取剪贴板管理器：
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    // 创建普通字符型ClipData
    val mClipData = ClipData.newPlainText("Label", text)
    // 将ClipData内容放到系统剪贴板里。
    cm?.setPrimaryClip(mClipData)
    toast("复制成功")
  }
}