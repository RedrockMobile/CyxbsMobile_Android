package com.mredrock.cyxbs.discover.map.widget

import android.app.Activity
import android.content.Context
import android.view.KeyEvent

object GlideProgressDialog {
    private var mDialog: CustomProgressDialog? = null

    fun show(context: Context, title: String, message: String, cancelable: Boolean) {
        mDialog = CustomProgressDialog.createDialog(context)
        val dialog = mDialog!!
        dialog.setMessage(message)
        dialog.setTitle(title)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (dialog.isShowing) {
                    dialog.hide()
                    val activity = context as Activity
                    activity.finish()
                }
                true
            } else {
                false;//默认返回 false
            }
        }
        dialog.show()
    }

    fun hide() {
        val dialog = mDialog
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
        mDialog = null
    }

    fun setProcess(process: Int) {
        mDialog?.setProgress(process)
    }
}