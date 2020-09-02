package com.mredrock.cyxbs.discover.map.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_dialog_update.view.*

/**
 *@author 林潼
 *@date 2020/8/20
 *@description
 */

interface OnUpdateSelectListener {
    fun onDeny()
    fun onPositive()
}

object UpdateMapDialog {
    fun show(context: Context, title: String, content: String, listener: OnUpdateSelectListener) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.map_transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.map_dialog_update, null, false)
        builder.setView(view)
        builder.setCancelable(true)
        view.map_tv_update_tip_title.text = title
        view.map_tv_update_tip_text.text = content
        view.map_tv_update_tip_deny.text = context.getText(R.string.map_update_dialog_deny)
        view.map_tv_update_tip_positive.text = context.getText(R.string.map_update_dialog_sure)
        val dialog = builder.create()
        dialog.show()

        view.map_tv_update_tip_deny.setOnClickListener {
            listener.onDeny()
            dialog.dismiss()
        }
        view.map_tv_update_tip_positive.setOnClickListener {
            listener.onPositive()
            dialog.dismiss()
        }
    }
}