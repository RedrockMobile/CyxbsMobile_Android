package com.mredrock.cyxbs.discover.map.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.mredrock.cyxbs.discover.map.R

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


class CustomProgressDialog(context: Context, theme: Int) : Dialog(context, theme) {

    /**
     *
     * [Summary]
     * setTitile 标题
     * @param strTitle
     * @return
     */
    fun setTitle(strTitle: String?) {
        val tvMsg: TextView = findViewById<View>(R.id.map_tv_download_title) as TextView
        tvMsg.text = strTitle
    }

    /**
     *
     * [Summary]
     * setMessage 提示内容
     * @param strMessage
     * @return
     */
    fun setMessage(strMessage: String?) {
        val tvMsg: TextView = findViewById<View>(R.id.map_tv_download_text) as TextView
        tvMsg.text = strMessage
    }

    fun setProgress(progress: Int) {
        val progressBar: ProgressBar = findViewById<View>(R.id.map_progressbar_download) as ProgressBar
        val progressValue: TextView = findViewById<View>(R.id.map_tv_download_progress_value) as TextView
        progressBar.progress = progress
        val value = "$progress%"
        progressValue.text = value
    }

    companion object {
        // TODO 以前学长写的垃圾代码，内存泄漏，目前暂时手动赋值为 null
        fun createDialog(context: Context): CustomProgressDialog {
            val customProgressDialog = CustomProgressDialog(context, R.style.map_transparent_dialog)
            customProgressDialog.setContentView(R.layout.map_dialog_download)
            customProgressDialog.window?.attributes?.gravity = Gravity.CENTER
            return customProgressDialog
        }
    }
}
