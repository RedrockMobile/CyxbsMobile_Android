package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.DrawableRes
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_common_dialog.*

/**
 * Create by yyfbe at 2020-01-25
 * 只适合qa的几个相似dialog
 */
class CommonDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_common_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun initView(@DrawableRes icon: Int,
                 title: String,
                 firstNotice: String,
                 secondNotice: String?,
                 buttonText: String,
                 confirmListener: View.OnClickListener,
                 cancelListener: View.OnClickListener?) {
        tv_title.text = title
        iv_pic.setImageResource(icon)
        tv_notice_first_line.text = firstNotice
        if (secondNotice.isNullOrEmpty()) {
            tv_notice_second_line.gone()
        } else {
            tv_notice_second_line.text = secondNotice
        }
        btn_confirm.text = buttonText
        btn_confirm.setOnClickListener(confirmListener)
        if (cancelListener == null) {
            iv_cancel.gone()
        } else {
            iv_cancel.setOnClickListener(cancelListener)
        }
    }
}