package com.mredrock.cyxbs.discover.map

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alibaba.android.arouter.facade.annotation.Route
//import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.config.DISCOVER_MAP
import com.mredrock.cyxbs.common.ui.BaseActivity
import kotlinx.android.synthetic.main.discover_map_activity_map.*

@Route(path = DISCOVER_MAP)
class MapActivity : BaseActivity() {
    override val isFragmentActivity = false

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_map_activity_map)

//        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(false)

        val dialog = ProgressDialog(this)
        dialog.setMessage("加载中...")
        dialog.show()

        discover_map_web.settings.javaScriptEnabled = true
        discover_map_web.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            discover_map_web.settings.mediaPlaybackRequiresUserGesture = false
        }
        discover_map_web.loadUrl("http://720yun.com/t/0e929mp6utn?pano_id=473004")
        discover_map_web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    dialog.dismiss()
                }
            }
        }
        discover_map_web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }
}
