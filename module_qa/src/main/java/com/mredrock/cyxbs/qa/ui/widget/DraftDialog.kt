package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_draft.*



/**
 * Create by xgl at 2020-12-14
 * 草稿dialog
 */
class DraftDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_dialog_draft)
        window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            val lp: WindowManager.LayoutParams = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.windowAnimations = R.style.BottomInAndOutStyle
            lp.gravity = Gravity.BOTTOM
            attributes = lp
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun initView(
            title: String,
            saveText: String,
            noSaveText: String?,
            cancelText: String,
            saveListener: View.OnClickListener,
            noSaveListener: View.OnClickListener,
            cancelListener: View.OnClickListener?) {
        qa_tv_title.text = title
        qa_tv_save.text = saveText
        qa_tv_no_save.text = noSaveText
        qa_tv_cancel.text = cancelText
        qa_tv_save.setOnClickListener(saveListener)
        qa_tv_no_save.setOnClickListener(noSaveListener)
        qa_tv_cancel.setOnClickListener(cancelListener)
    }
}