package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment.DynamicFragment
import kotlinx.android.synthetic.main.qa_dialog_report.*

/**
 *@author zhangzhe
 *@date 2020/8/18
 *@description
 */


class QaReportDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_dialog_report)
        window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            val lp: WindowManager.LayoutParams = attributes
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER_VERTICAL
            attributes = lp
            setCancelable(true)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    fun show( onPositive: (String) -> Unit) {
        qa_tv_tip_deny.setOnSingleClickListener {
            dismiss()
        }

        qa_et_report.filters = arrayOf(
                object : InputFilter.LengthFilter(
                        Companion.MAX_SIZE) {}
        )
        qa_et_report.addTextChangedListener(object : TextWatcher {
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
            ) {
                if (charSequence.length >= 150) {
                    CyxbsToast.makeText(context, R.string.qa_dialog_report_text, Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        qa_tv_tip_positive.setOnClickListener {
            onPositive.invoke(qa_et_report.text.toString())
            dismiss()
        }
    }

    companion object {
        const val MAX_SIZE = 150
    }
}