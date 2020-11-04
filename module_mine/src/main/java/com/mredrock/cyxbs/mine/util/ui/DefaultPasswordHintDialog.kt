package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.ForgetPasswordActivity

/**
 *@Date 2020-11-04
 *@Time 19:10
 *@author SpreadWater
 *@description 一个提示默认密码的dialog
 */
class DefaultPasswordHintDialog(context: Context,them:Int) :Dialog(context,them){
    companion object{
        private var defaultPasswordHintDialog:DefaultPasswordHintDialog?=null
        fun show(context: Context?,activity: BaseActivity){
            if (context == null) return
            if (defaultPasswordHintDialog==null){
                defaultPasswordHintDialog= DefaultPasswordHintDialog(context, R.style.transparent_dialog)
            }
            defaultPasswordHintDialog!!.setContentView(R.layout.mine_dialog_default_password_hint)
            val relogin= defaultPasswordHintDialog!!.findViewById<Button>(R.id.mine_security_bt_relogin)
            relogin.setOnClickListener {
                defaultPasswordHintDialog!!.hide()
                activity.finish()
            }
        }
    }
}