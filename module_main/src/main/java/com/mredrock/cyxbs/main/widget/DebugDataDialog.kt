package com.mredrock.cyxbs.main.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.mredrock.cyxbs.lib.base.utils.Umeng
import com.mredrock.cyxbs.lib.utils.extensions.gone
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
  
  @SuppressLint("SetTextI18n")
  private fun initDialog() {
    val tvDeviceId = findViewById<TextView>(R.id.umeng_device_id)
    tvDeviceId.text = Umeng.deviceId
    tvDeviceId.setOnLongClickListener {
      textViewToClipboard(tvDeviceId.text)
      true
    }
    
    val tvTitle = findViewById<TextView>(R.id.umeng_device_title)
    if (context.packageName != "com.mredrock.cyxbs") {
      tvDeviceId.gone()
      tvTitle.text = "包名不为 com.mredrock.cyxbs，Umeng 注册失败，无法获取 deviceId !"
      /*
      * 如果你当前使用了 debug 下 apk 共存功能，会导致 debug 下包名不为 com.mredrock.cyxbs，所以注册会失败
      *
      * 请修改 build-logic/core/version 模块中的 Config#getApplicationId() 方法即可解决
      *
      * */
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