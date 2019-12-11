package com.mredrock.cyxbs.qa.pages.answer.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.qa.R

class ReportDialog(context: Context) : BottomSheetDialog(context) {
    private var mBehavior: BottomSheetBehavior<View>
    @SuppressLint("InflateParams")
    private val container: View = layoutInflater.inflate(R.layout.qa_dialog_question_report,null)

    init {
        setContentView(container)
        mBehavior = BottomSheetBehavior.from(container.parent as View)
    }

    override fun onStart() {
        super.onStart()

        window?.findViewById<View>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun show() {
        super.show()
        mBehavior.peekHeight = container.measuredHeight
    }

}