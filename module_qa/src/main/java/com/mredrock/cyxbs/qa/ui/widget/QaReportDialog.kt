package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_report.view.*

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


object QaReportDialog {
    fun show(context: Context, onPositive: (String) -> Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.qa_transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.qa_dialog_report, null, false)
        builder.setView(view)
        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()

        view.qa_tv_tip_deny.setOnSingleClickListener {
            dialog.dismiss()
        }
        view.qa_tv_tip_positive.setOnClickListener {
            onPositive.invoke(view.qa_tv_tip_text.toString())
            dialog.dismiss()
        }
    }
}