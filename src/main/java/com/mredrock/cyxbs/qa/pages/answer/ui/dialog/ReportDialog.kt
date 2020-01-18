package com.mredrock.cyxbs.qa.pages.answer.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.qa.R

class ReportDialog(context: Context) : BottomSheetDialog(context) {
    private var mBehavior: BottomSheetBehavior<View>
    @SuppressLint("InflateParams")
    private val container: View = layoutInflater.inflate(R.layout.qa_dialog_question_report, null)

    init {
        setContentView(container)
        mBehavior = BottomSheetBehavior.from(container.parent as View).apply {
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


}