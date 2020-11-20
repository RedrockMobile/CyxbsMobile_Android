package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_common_dialog.*


/**
 * Create by yyfbe at 2020-01-25
 * 只适合qa的几个相似dialog
 */
class CommonDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_common_dialog)
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
        tv_title.text = title
        tv_save.text = saveText
        tv_no_save.text = noSaveText
        tv_cancel.text = cancelText
        tv_save.setOnClickListener(saveListener)
        tv_no_save.setOnClickListener(noSaveListener)
        tv_cancel.setOnClickListener(cancelListener)
    }
}