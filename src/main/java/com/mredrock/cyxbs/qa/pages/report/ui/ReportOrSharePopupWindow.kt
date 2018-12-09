package com.mredrock.cyxbs.qa.pages.report.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_popup_window_report_or_share.view.*

/**
 * Created By jay68 on 2018/12/9.
 */
class ReportOrSharePopupWindow(context: Context,
                               private val qid: String,
                               private val anchor: View,
                               private val frame: View) : PopupWindow(context) {
    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        contentView = LayoutInflater.from(context).inflate(R.layout.qa_popup_window_report_or_share, null, false)
        contentView.tv_report.setOnClickListener {
            ReportActivity.activityStart(context, qid)
            dismiss()
        }
        animationStyle = R.style.PopupAnimation
        isTouchable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setOnDismissListener { frame.gone() }
    }

    fun show() {
        frame.visible()
        showAtLocation(anchor, Gravity.END or Gravity.TOP, 0, anchor.height)
    }
}