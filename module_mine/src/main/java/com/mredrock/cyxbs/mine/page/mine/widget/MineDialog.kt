package com.mredrock.cyxbs.mine.page.mine.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_dialog_choose.view.*


/**
 * @ClassName MineDialog
 * @Description TODO
 * @Author 29942
 * @QQ 2994250239
 * @Date 2021/10/24 12:36
 * @Version 1.0
 */
object MineDialog {
    fun show(context: Context, title:String, onDeny: () -> Unit, onPositive: () -> Unit) {
        val dialog = Dialog(context, R.style.transparent_dialog)
        dialog.setContentView(R.layout.mine_dialog_choose)
        val view = dialog.window!!.decorView
        view.qa_tv_tip_text.text = title
        dialog.window?.attributes?.gravity = Gravity.CENTER
        dialog.show()

        view.qa_tv_tip_deny.setOnSingleClickListener {
            onDeny.invoke()
            dialog.dismiss()
        }
        view.qa_tv_tip_positive.setOnSingleClickListener {
            onPositive.invoke()
            dialog.dismiss()
        }
    }
}