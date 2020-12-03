package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.security.activity.FindPasswordActivity
import com.mredrock.cyxbs.mine.page.security.activity.FindPasswordActivity.Companion.FIND_PASSWORD_BY_EMAIL
import com.mredrock.cyxbs.mine.page.security.activity.FindPasswordActivity.Companion.FIND_PASSWORD_BY_SECURITY_QUESTION

/**
 * Author: RayleighZ
 * Time: 2020-11-03 2:34
 */
class ChooseFindTypeDialog(context: Context?, theme: Int) : Dialog(context, theme) {
    companion object {
        //此处函数将来可以优化，目前必须要传递一个学号进来
        fun showDialog(context: Context?, hasEmailBinding: Boolean, hasSecurityQuestion: Boolean, activity: BaseActivity, isFromLogin: Boolean, stuNumber: String) {
            if (context == null) return
            val chooseFindTypeDialog = ChooseFindTypeDialog(context, R.style.transparent_dialog)
            chooseFindTypeDialog.setContentView(R.layout.mine_dialog_choose_find_type)
            chooseFindTypeDialog.window?.attributes?.gravity = Gravity.CENTER
            val tvEmail = chooseFindTypeDialog.findViewById<TextView>(R.id.mine_tv_dialog_choose_type_email)
            val tvProtect = chooseFindTypeDialog.findViewById<TextView>(R.id.mine_tv_dialog_choose_type_protect)
            tvEmail.setOnClickListener {
                //当点击通过邮箱找回的按钮时
                if (hasEmailBinding) {
                    //启动邮箱找回模块
                    if (isFromLogin) {//是否已经登陆
                        FindPasswordActivity.actionStartFromLogin(context, FIND_PASSWORD_BY_EMAIL, stuNumber)
                    } else {
                        FindPasswordActivity.actionStartFromMine(context, FIND_PASSWORD_BY_EMAIL)
                    }
                    chooseFindTypeDialog.hide()
                    activity.finish()
                    chooseFindTypeDialog.dismiss()
                } else {
                    //弹出toast提示没有进行密码绑定
                    BaseApp.context.toast("您好像还没有绑定邮箱")
                }
            }
            tvProtect.setOnClickListener {
                //当点击通过密保找回时
                if (hasSecurityQuestion) {
                    //启动密保找回模块
                    if (isFromLogin) {//是否已经登陆
                        FindPasswordActivity.actionStartFromLogin(context, FIND_PASSWORD_BY_SECURITY_QUESTION, stuNumber)
                    } else {
                        FindPasswordActivity.actionStartFromMine(context, FIND_PASSWORD_BY_SECURITY_QUESTION)
                    }
                    chooseFindTypeDialog.hide()
                    activity.finish()
                    chooseFindTypeDialog.dismiss()
                } else {
                    BaseApp.context.toast("您好像还没有设置密保问题")
                }
            }
            chooseFindTypeDialog.show()
        }
    }
}