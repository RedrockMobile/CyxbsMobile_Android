package com.mredrock.cyxbs.qa.pages.answer.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_question_report.*
import kotlinx.android.synthetic.main.qa_dialog_question_report_dialog_type_layout.*

class ReportDialog(context: Context) : BottomSheetDialog(context) {
    private var mBehavior: BottomSheetBehavior<View>

    var pressReport: ((String) -> Unit)? = null
    var pressQuestionIgnore: (() -> Unit)? = null
    @SuppressLint("InflateParams")
    private val container: View = layoutInflater.inflate(R.layout.qa_dialog_question_report, null)

    init {
        setContentView(container)
        mBehavior = BottomSheetBehavior.from(container.parent as View).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_HIDDEN || p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                        cancel()
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.findViewById<View>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
        mBehavior.peekHeight = container.measuredHeight
        initReport()
    }

    fun setType(type: String) {
        val types = context.resources.getStringArray(R.array.qa_title_type)
        when (type) {
            types[0] -> {
                tv_report.text = context.getText(R.string.qa_question_report_text)
                tv_question_ignore.visible()
                tv_report_type_useless.text = context.getText(R.string.qa_report_useless_question_text)
            }
            types[1] -> {
                tv_report.text = context.getText(R.string.qa_answer_report_text)
                tv_report_type_useless.text = context.getText(R.string.qa_report_useless_answer_text)
            }
            types[2] -> {
                tv_report.text = context.getText(R.string.qa_comment_report_text)
                tv_report_type_useless.text = context.getText(R.string.qa_report_useless_comment_text)

            }
        }
    }

    private fun initReport() {
        //遍历子view，TextView，并且不是最后提示文字
        (layout_report_view_group as ViewGroup).forEach {
            if (it is TextView && it != tv_explain && it != tv_question_ignore) {
                it.setOnClickListener { view ->
                    pressReport?.invoke((view as TextView).text.toString())
                }
            }
        }
        tv_question_ignore.setOnClickListener { pressQuestionIgnore?.invoke() }
        btn_cancel_report.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN || behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}