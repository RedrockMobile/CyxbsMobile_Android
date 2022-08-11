package com.mredrock.lib.crashmonitor.view

import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.lib.crashmonitor.R

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/11
 * @Description:
 */
class ErrorPopupWindow() {
    fun show(title: String, content: String) {
        val view = LayoutInflater.from(appContext).inflate(R.layout.crash_layout_dialog, null)
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle(title)
        builder.setPositiveButton("收到", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })
        builder.setView(view)
        builder.show()
    }
}