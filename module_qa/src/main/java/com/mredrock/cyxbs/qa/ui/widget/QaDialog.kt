package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_choose.view.*

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


object QaDialog {
    fun show(context: Context, title: String, onDeny: () -> Unit, onPositive: () -> Unit) {
        val dialog = Dialog(context, R.style.qa_transparent_dialog)
        dialog.setContentView(R.layout.qa_dialog_choose)
        val view = dialog.window.decorView//LayoutInflater.from(context).inflate(R.layout.qa_dialog_choose, null, false)
        view.qa_tv_tip_text.text = title
        dialog.window?.attributes?.gravity = Gravity.CENTER

//        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.qa_transparent_dialog)
//        builder.setView(view)
//        builder.setCancelable(true)
//        view.qa_tv_tip_text.text = title
//
//        val dialog = builder.create()
        dialog.show()

        view.qa_tv_tip_deny.setOnSingleClickListener {
            onDeny.invoke()
            dialog.dismiss()
        }
        view.qa_tv_tip_positive.setOnSingleClickListener {
            onPositive.invoke()
            dialog.dismiss()
        }
    }
}