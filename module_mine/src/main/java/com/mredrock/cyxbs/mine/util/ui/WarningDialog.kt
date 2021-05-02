package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_dialog_warning.*
import kotlinx.android.synthetic.main.mine_layout_dialog_with_title_and_content.*

/**
 * Author: RayleighZ
 * Time: 2021-05-02 19:02
 */
class WarningDialog(context: Context, theme: Int) : Dialog(context, theme) {
    companion object {
        fun showDialog(context: Context?,title: String = "提醒", content: String, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
            if (context == null) return
            val warningDialog = Dialog(context, R.style.transparent_dialog)
            warningDialog.apply {
                setContentView(R.layout.mine_dialog_warning)
                mine_tv_dialog_warning_title.text = title
                mine_tv_dialog_warning_content.text = content
                mine_bt_security_dialog_negative.setOnClickListener {
                    onNegativeClick.invoke()
                    warningDialog.dismiss()
                }
                mine_bt_security_dialog_postive.setOnClickListener {
                    onPositiveClick.invoke()
                }
            }
            warningDialog.show()
        }
    }
}