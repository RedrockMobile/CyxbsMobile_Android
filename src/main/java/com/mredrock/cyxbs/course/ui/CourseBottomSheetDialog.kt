package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.course.R

@Route(path = "/test/test")
class CourseBottomSheetDialog : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(dialog.context).inflate(R.layout.course_fragment_course_entry, container)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.setContentView()
//        return dialog
//    }
}
