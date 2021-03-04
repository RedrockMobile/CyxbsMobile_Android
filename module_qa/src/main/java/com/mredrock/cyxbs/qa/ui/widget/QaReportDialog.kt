package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_activity_quiz.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_dialog_report.view.*

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


object QaReportDialog {
    const val MAX_SIZE = 150
    var reportContent = ""
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
        view.qa_et_report.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
            ) {
            }

            override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
            ) = if (charSequence.length <= MAX_SIZE) {
                reportContent = charSequence.toString()
            } else {
                CyxbsToast.makeText(context, R.string.qa_dialog_report_text, Toast.LENGTH_SHORT).show()
                view.qa_et_report.setText(reportContent)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        view.qa_tv_tip_positive.setOnClickListener {
            onPositive.invoke(reportContent)
            dialog.dismiss()
        }
    }
}