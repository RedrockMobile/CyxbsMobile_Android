package com.mredrock.cyxbs.dialog

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.dialog.webView.DialogJsInterface
import com.mredrock.cyxbs.dialog.webView.DialogWebView
import com.mredrock.api.dialog.DIALOG_SERVICE
import com.mredrock.api.dialog.IDialogService

/**
 * com.mredrock.cyxbs.dialog.DialogService
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/8/2 14:02
 */
@Route(path = DIALOG_SERVICE)
class DialogService : IDialogService {

    override fun openWebDialog(
        lifecycle: Lifecycle,
        context: Context,
        url: String,
        handleWebView: WebView.() -> Unit
    ) {
        val dialog = MaterialDialog(context)
            .customView(R.layout.layout_dialog)
        val webView = dialog.getCustomView() as DialogWebView
        webView.handleWebView()
        webView.loadUrl(url)
        webView.initialize(lifecycle, "DialogService" to DialogJsInterface())
        dialog.show()
    }

    override fun init(context: Context) {

    }
}