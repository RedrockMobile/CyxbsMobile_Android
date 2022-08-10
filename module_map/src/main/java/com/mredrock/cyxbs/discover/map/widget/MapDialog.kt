package com.mredrock.cyxbs.discover.map.widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.discover.map.R

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

        val map_tv_tip_title = view.findViewById<TextView>(R.id.map_tv_tip_title)
        val map_tv_tip_text = view.findViewById<TextView>(R.id.map_tv_tip_text)
        val map_tv_tip_deny = view.findViewById<TextView>(R.id.map_tv_tip_deny)
        val map_tv_tip_positive = view.findViewById<TextView>(R.id.map_tv_tip_positive)
        map_tv_tip_title.text = title
        map_tv_tip_text.text = content

        val dialog = builder.create()
        dialog.show()

        map_tv_tip_deny.setOnSingleClickListener {
            listener.onDeny()
            dialog.dismiss()
        }
        map_tv_tip_positive.setOnClickListener {
            listener.onPositive()
            dialog.dismiss()
        }
    }
}