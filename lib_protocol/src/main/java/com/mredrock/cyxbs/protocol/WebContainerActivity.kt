package com.mredrock.cyxbs.protocol

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.protocol.PROTOCOL_WEB_CONTAINER
import kotlinx.android.synthetic.main.protocol_activity_web_container.*


@Route(path = PROTOCOL_WEB_CONTAINER)
class WebContainerActivity : AppCompatActivity() {

    companion object{
        const val URI = "uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.getStringExtra(URI)
        setContentView(R.layout.protocol_activity_web_container)
        web_view.loadUrl(uri)
    }
}