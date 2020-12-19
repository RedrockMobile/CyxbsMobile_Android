package com.mredrock.cyxbs.qa.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_share.*

/**
 * @Author: xgl
 * @ClassName: ShareDialog
 * @Description:
 * @Date: 2020/12/19 13:10
 */
class ShareDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_dialog_share)
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
            onClickListener: View.OnClickListener,
            onCancelListener: View.OnClickListener
    ) {
        qa_tv_cancel.setOnClickListener(onCancelListener)
    }
}