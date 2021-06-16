package com.mredrock.cyxbs.main.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DebugDataModel
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.main.R
import kotlinx.android.synthetic.main.main_activity_debug.*

class DebugActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_debug)
        initActivity()
    }

    private fun initActivity() {

        DebugDataModel.umPushDeviceId.observe(this, Observer {
            device_id.text = it ?: ""
        })

        DebugDataModel.umAnalyzeDeviceData.observe(this, Observer {
            umeng_analyzes_device_data.text = it ?: ""
        })

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
                ServiceManager.getService(IProtocolService::class.java).jump(text)
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