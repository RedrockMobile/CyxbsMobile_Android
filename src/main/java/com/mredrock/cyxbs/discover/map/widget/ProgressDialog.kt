package com.mredrock.cyxbs.discover.map.widget

import android.app.ProgressDialog
import android.content.Context

//转圈圈的弹窗

object ProgressDialog {
    private var progressDialog: ProgressDialog? = null

    fun show(context: Context, title: String, message: String, cancelable: Boolean) {
        progressDialog = ProgressDialog.show(context, title, message, true, cancelable);
        progressDialog?.setTitle(title);
        progressDialog?.setMessage(message);
        progressDialog?.show()
    }

    fun hide() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }
    fun dismiss(){
        progressDialog?.dismiss()
    }
}