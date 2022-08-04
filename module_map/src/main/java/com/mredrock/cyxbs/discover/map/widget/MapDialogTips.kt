package com.mredrock.cyxbs.discover.map.widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.discover.map.R

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */

interface OnSelectListenerTips {
    fun onPositive()
}

object MapDialogTips {
    fun show(context: Context, title: String, content: String, cancelable: Boolean, listener: OnSelectListenerTips) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.map_transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.map_dialog_tips, null, false)
        builder.setView(view)
        builder.setCancelable(cancelable)
        val map_tv_tip_title:TextView = view.findViewById(R.id.map_tv_tip_title)
        val map_tv_tip_text:TextView = view.findViewById(R.id.map_tv_tip_text)
        val map_tv_tip_positive:TextView = view.findViewById(R.id.map_tv_tip_positive)

        map_tv_tip_title.text = title
        map_tv_tip_text.text = content

        val dialog = builder.create()
        dialog.show()

        map_tv_tip_positive.setOnClickListener {
            listener.onPositive()
            dialog.dismiss()
        }
    }
}