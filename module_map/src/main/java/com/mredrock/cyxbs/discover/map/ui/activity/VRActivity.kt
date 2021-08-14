package com.mredrock.cyxbs.discover.map.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.startActivity

class VRActivity : BaseActivity() {

    private lateinit var webView: WebView


    companion object {
        const val VR_URL = "vr_url"
        const val VR_URL_SP = "vr_url_sp"
        const val DEFAULT_VR_URL = "http://720yun.com/t/0e929mp6utn?pano_id=473004"
        fun startVRActivity(activity: Activity, vrUrl: String) {
            activity.startActivity<VRActivity>(VR_URL to vrUrl)
        }
    }

    private var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(VR_URL).isNullOrEmpty()) {
            url = defaultSharedPreferences.getString(VR_URL_SP, DEFAULT_VR_URL) ?: DEFAULT_VR_URL
        } else {
            url = intent.getStringExtra(VR_URL)
            defaultSharedPreferences.editor {
                putString(VR_URL_SP, url)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onStart() {
        super.onStart()

        webView = WebView(this)
        setContentView(webView)

        val dialog = ProgressDialog(this)
        dialog.setMessage("加载中...")
        dialog.show()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.settings.mediaPlaybackRequiresUserGesture = false
        }
        webView.loadUrl(url)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    dialog.dismiss()
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    override fun onStop() {
        webView.destroy()
        super.onStop()
    }
}
