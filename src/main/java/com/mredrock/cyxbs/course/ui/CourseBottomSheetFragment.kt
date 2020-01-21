package com.mredrock.cyxbs.course.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.course.R


/**
 * @author jon
 * @create 2020-01-19 12:29 PM
 *
 * 描述:
 *
 */
@Route(path = "/course/test")
class CourseBottomSheetFragment(): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.course_fragment_course_bottom_sheet_dialog, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            return TestBottomSheetDialog(it)
        }
        return super.onCreateDialog(savedInstanceState)
    }

    class TestBottomSheetDialog : BottomSheetDialog {

        lateinit var mContentView: View

        constructor(context: Context) : super(context) {}
        constructor(context: Context, theme: Int) : super(context, theme) {}


        override fun setContentView(view: View) {
            super.setContentView(view)
            mContentView = view
        }

        override fun onStart() {
            super.onStart()
            val bottomSheetBehavior = BottomSheetBehavior.from(mContentView.parent as View)
            bottomSheetBehavior.peekHeight = 300
            bottomSheetBehavior.isHideable = false
        }

    }
}
