package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_quiz_tags_edit.*
import kotlinx.android.synthetic.main.qa_dialog_quiz_tags_edit.view.*

/**
 * Created By jay68 on 2018/11/12.
 */
class TagsEditDialog(context: Context) : BottomSheetDialog(context) {
    var onSureButtonClickedLister: (tag: String) -> Unit = {}

    init {
        val contentView = layoutInflater.inflate(R.layout.qa_dialog_quiz_tags_edit, null, false)
        contentView.tv_quiz_tags_edit_dialog_sure.setOnClickListener {
            onSureButtonClickedLister(edt_quiz_tags.text.toString())
            dismiss()
        }
        setContentView(contentView)
    }
}