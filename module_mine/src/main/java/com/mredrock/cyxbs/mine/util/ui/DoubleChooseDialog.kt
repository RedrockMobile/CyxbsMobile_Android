package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.Button
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.BindEmailActivity
import com.mredrock.cyxbs.mine.page.security.activity.SetPasswordProtectActivity

class DoubleChooseDialog(context: Context?, theme: Int) : Dialog(context, theme) {
    companion object {
        fun show(context: Context?) {
            if (context == null) return
            val doubleChooseDialog = DoubleChooseDialog(context, R.style.transparent_dialog)
            doubleChooseDialog.setContentView(R.layout.mine_dialog_double_choose)
            val buttonToBindingEmail = doubleChooseDialog.findViewById<Button>(R.id.mine_bt_security_dialog_postive)
            val buttonToSetPasswordProtect = doubleChooseDialog.findViewById<Button>(R.id.mine_bt_security_dialog_negative)
            buttonToSetPasswordProtect.setOnClickListener {
                SetPasswordProtectActivity.actionStart(context)
                doubleChooseDialog.hide()
            }
            buttonToBindingEmail.setOnClickListener {
                context.startActivity(Intent(context, BindEmailActivity::class.java))
                doubleChooseDialog.hide()
            }
            doubleChooseDialog.show()
        }
    }
}

