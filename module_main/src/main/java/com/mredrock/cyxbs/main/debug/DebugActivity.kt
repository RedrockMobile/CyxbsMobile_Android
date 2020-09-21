package com.mredrock.cyxbs.main.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DebugDataModel
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.jump.JumpProtocol
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.databinding.MainActivityDebugBinding
import kotlinx.android.synthetic.main.main_activity_debug.*

class DebugActivity : AppCompatActivity() {

//    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<MainActivityDebugBinding>(this, R.layout.main_activity_debug).apply {
            debugModel = DebugDataModel
        }
        initActivity()
    }

    private fun initActivity() {
        device_id.setOnLongClickListener {
            textViewToClipboard(device_id)
            true
        }

        umeng_analyzes_device_data.setOnLongClickListener {
            textViewToClipboard(umeng_analyzes_device_data)
            true
        }

        button_start_jumping.setOnClickListener {
            val text = et_unified_protocol_jump.text.toString()
            if (text.matches(Regex(".+://.+(/.+)?"))) {
                JumpProtocol.start(text)
            } else {
                Toast.makeText(this, "格式不正确", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun textViewToClipboard(deviceId: TextView) {
        //获取剪贴板管理器：
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", deviceId.text)
        // 将ClipData内容放到系统剪贴板里。
        cm?.primaryClip = mClipData
        CyxbsToast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show()
    }

}