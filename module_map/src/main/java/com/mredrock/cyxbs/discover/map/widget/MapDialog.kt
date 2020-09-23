package com.mredrock.cyxbs.discover.map.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.utils.extensions.setSingleOnClickListener
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_dialog_choose.view.*

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */

interface OnSelectListener {
    fun onDeny()
    fun onPositive()
}

object MapDialog {
    fun show(context: Context, title: String, content: String, listener: OnSelectListener) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.map_transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.map_dialog_choose, null, false)
        builder.setView(view)
        builder.setCancelable(true)
        view.map_tv_tip_title.text = title
        view.map_tv_tip_text.text = content

        val dialog = builder.create()
        dialog.show()

        view.map_tv_tip_deny.setSingleOnClickListener {
            listener.onDeny()
            dialog.dismiss()
        }
        view.map_tv_tip_positive.setOnClickListener {
            listener.onPositive()
            dialog.dismiss()
        }
    }
}