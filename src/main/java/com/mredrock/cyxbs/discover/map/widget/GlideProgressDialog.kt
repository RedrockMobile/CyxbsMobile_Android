package com.mredrock.cyxbs.discover.map.widget

import android.app.ProgressDialog
import android.content.Context

//转圈圈的弹窗

object GlideProgressDialog {
    private var dialog: CustomProgressDialog? = null

    fun show(context: Context, title: String, message: String, cancelable: Boolean) {
        dialog = CustomProgressDialog.createDialog(context)
        dialog?.setMessage(message)
        dialog?.setTitle(title)
        dialog?.setCancelable(cancelable)
        dialog?.show()
    }

    fun hide() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun setProcess(process: Int) {
        if (dialog != null) {
            dialog?.setProgress(process)
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}