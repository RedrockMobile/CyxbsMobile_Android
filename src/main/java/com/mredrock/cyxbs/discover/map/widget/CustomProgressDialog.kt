package com.mredrock.cyxbs.discover.map.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.mredrock.cyxbs.discover.map.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


class CustomProgressDialog : Dialog {

    constructor(context: Context?) : super(context) {
    }

    constructor(context: Context?, theme: Int) : super(context, theme) {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (customProgressDialog == null) {
            return
        }
    }

    /**
     *
     * [Summary]
     * setTitile 标题
     * @param strTitle
     * @return
     */
    fun setTitle(strTitle: String?): CustomProgressDialog? {
        val tvMsg: TextView = customProgressDialog!!.findViewById<View>(R.id.map_tv_download_title) as TextView
        tvMsg.text = strTitle
        return customProgressDialog
    }

    /**
     *
     * [Summary]
     * setMessage 提示内容
     * @param strMessage
     * @return
     */
    fun setMessage(strMessage: String?): CustomProgressDialog? {
        val tvMsg: TextView = customProgressDialog!!.findViewById<View>(R.id.map_tv_download_text) as TextView
        tvMsg.text = strMessage
        return customProgressDialog
    }

    fun setProgress(progress: Int) {
        val progressBar: ProgressBar = customProgressDialog!!.findViewById<View>(R.id.map_progressbar_download) as ProgressBar
        val progressValue: TextView = customProgressDialog!!.findViewById<View>(R.id.map_tv_download_progress_value) as TextView
        progressBar.progress = progress
        val value = "$progress%"
        GlobalScope.launch(Dispatchers.Main) {
            progressValue.text = value
        }
    }

    companion object {
        private var customProgressDialog: CustomProgressDialog? = null
        fun createDialog(context: Context?): CustomProgressDialog? {
            customProgressDialog = CustomProgressDialog(context, R.style.map_transparent_dialog)
            customProgressDialog?.setContentView(R.layout.map_dialog_download)
            customProgressDialog?.window?.attributes?.gravity = Gravity.CENTER
            return customProgressDialog
        }
    }
}
