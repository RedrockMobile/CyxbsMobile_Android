package com.mredrock.lib.crash.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mredrock.lib.crash.R

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/9/2
 * @Description: 展示错误信息的弹窗
 */
internal object ErrorDialog {
    private var mDialog: AlertDialog? = null
    fun showCrashDialog(currentActivity: Activity, title: String, content: String) {
        mDialog?.let {
            if (it.isShowing) return //一次只允许弹一个错误（一般也只会有一个错误）
        }
        var view: View? =
            LayoutInflater.from(currentActivity).inflate(R.layout.crash_layout_dialog, null)
        var mContentTV: TextView? = view?.findViewById(R.id.crash_textview_content)
        mContentTV?.text = content
        mDialog = AlertDialog.Builder(currentActivity)
            .setTitle(title)
            .setPositiveButton("收到") { dialog, _ ->
                dialog.cancel()
                view = null
                mContentTV = null
                mDialog = null
            }
            .setView(view)
            .create()
        mDialog!!.show()
    }
}