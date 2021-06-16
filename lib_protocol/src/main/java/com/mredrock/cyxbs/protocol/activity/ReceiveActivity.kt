package com.mredrock.cyxbs.protocol.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.protocol.R
import com.mredrock.cyxbs.protocol.databinding.ProtocolActivityReceiveBinding

class ReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = DataBindingUtil.setContentView<ProtocolActivityReceiveBinding>(this, R.layout.protocol_activity_receive)
        contentView.isShow = View.GONE
        val uri: Uri? = intent.data
        if (uri != null) {
            // 能够唤起这个activity就说明这个uri是符合规范的，所以这里只需要判定一下path是否存在
            val path: String = uri.path ?: ""
            if (path.isBlank()) {
                contentView.isShow = View.VISIBLE
                return
            } else {
                // 跳转
                ServiceManager.getService(IProtocolService::class.java).jump(uri.toString())
                finish()
            }
        }
    }
}