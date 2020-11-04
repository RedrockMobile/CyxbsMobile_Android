package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.SetPasswordProtectActivity
import com.mredrock.cyxbs.mine.page.security.viewmodel.FindPasswordViewModel

class DoubleChooseDialog(context: Context?, theme: Int) : Dialog(context!!, theme) {
    companion object {
        private var doubleChooseDialog: DoubleChooseDialog? = null
        fun show(context: Context?) {
            if (context == null) return
            if (doubleChooseDialog == null) {
                doubleChooseDialog = DoubleChooseDialog(context, R.style.transparent_dialog)
            }
            doubleChooseDialog!!.setContentView(R.layout.mine_dialog_double_choose)
            val buttonToBindingEmail = doubleChooseDialog!!.findViewById<Button>(R.id.mine_bt_security_dialog_binding_email)
            val buttonToSetPasswordProtect = doubleChooseDialog!!.findViewById<Button>(R.id.mine_bt_security_dialog_set_protect)
            buttonToSetPasswordProtect.setOnClickListener {
                SetPasswordProtectActivity.start(context)
                doubleChooseDialog!!.hide()
            }
            buttonToBindingEmail.setOnClickListener {
                //TODO:尚未Merge齐哥的部分，暂时不添加跳转
                doubleChooseDialog!!.hide()
            }
            doubleChooseDialog!!.show()
        }
    }
}

