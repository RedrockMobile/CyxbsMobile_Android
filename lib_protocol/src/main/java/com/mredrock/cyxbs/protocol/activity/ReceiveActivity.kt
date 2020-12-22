package com.mredrock.cyxbs.protocol.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.api.protocol.api.IProtocolService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.protocol.R

class ReceiveActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ReceiveActivityTAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        val uri: Uri? = intent.data
        if (uri != null) {
            // 完整的url信息
            val url: String = uri.toString()
            Log.e(TAG, "url: $uri")
            // scheme部分
            val scheme: String = uri.scheme
            Log.e(TAG, "scheme: $scheme")
            // host部分
            val host: String = uri.host
            Log.e(TAG, "host: $host")
            //port部分
            val port: Int = uri.port
            Log.e(TAG, "host: $port")
            // 访问路劲
            val path: String = uri.path
            Log.e(TAG, "path: $path")
            if (path.isBlank()) {
                // tod
                Toast.makeText(this, "找不到该页面", Toast.LENGTH_SHORT).show()
                return
            }
            val pathSegments: List<String> = uri.pathSegments
            // Query部分
            val query: String = uri.query
            Log.e(TAG, "query: $query")
            //获取指定参数值
//            val goodsId: String = uri.getQueryParameter("goodsId")
//            Log.e(TAG, "goodsId: $goodsId")
            ServiceManager.getService(IProtocolService::class.java).jump(uri.toString())
            finish()
        }
    }
}