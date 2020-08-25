package com.mredrock.cyxbs.discover.map.widget

import android.app.Activity
import android.content.Context
import android.view.KeyEvent

//转圈圈的弹窗

object GlideProgressDialog {
    private var dialog: CustomProgressDialog? = null

    fun show(context: Context, title: String, message: String, cancelable: Boolean) {
        dialog = CustomProgressDialog.createDialog(context)
        dialog?.setMessage(message)
        dialog?.setTitle(title)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(cancelable)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (dialog?.isShowing == true) {
                    dialog?.hide()
                    val activity = context as Activity
                    activity.finish()
                }
                true
            } else {
                false;//默认返回 false
            }
        }
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


}