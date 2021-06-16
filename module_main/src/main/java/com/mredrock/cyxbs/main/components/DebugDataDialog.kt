package com.mredrock.cyxbs.main.components

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DebugDataModel
import com.mredrock.cyxbs.main.BR.debugModel
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.databinding.MainDialogTestDataBindingImpl
import kotlinx.android.synthetic.main.main_dialog_test_data.*


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
        val view = DataBindingUtil.inflate<MainDialogTestDataBindingImpl>(
                LayoutInflater.from(context),
                R.layout.main_dialog_test_data,
                window?.decorView as ViewGroup,
                false).apply {
            debugModel = DebugDataModel
        }
        setContentView(view.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initDialog(view)
    }

    private fun initDialog(view: MainDialogTestDataBindingImpl) {
        view.apply {
            device_id.setOnLongClickListener {
                textViewToClipboard(device_id)
                true
            }

            umeng_analyzes_device_data.setOnLongClickListener {
                textViewToClipboard(umeng_analyzes_device_data)
                true
            }
        }
    }


    private fun textViewToClipboard(deviceId: TextView) {
        //获取剪贴板管理器：
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", deviceId.text)
        // 将ClipData内容放到系统剪贴板里。
        cm?.primaryClip = mClipData
        CyxbsToast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
    }

}