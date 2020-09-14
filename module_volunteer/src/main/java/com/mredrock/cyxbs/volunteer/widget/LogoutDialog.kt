package com.mredrock.cyxbs.volunteer.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.volunteer.R
import kotlinx.android.synthetic.main.volunteer_dialog_choose.view.*

/**
 * @date 2020-09-14
 * @author Sca RayleighZ
 */
object LogoutDialog {
    fun show(context: Context, onDeny : () -> Unit, onConfirm:() ->Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.volunteer_transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.volunteer_dialog_choose, null, false)
        builder.setView(view)
        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()

        //表示用户点击取消退出（保持登录）
        view.tv_volunteer_dialog_cancel_logout.setOnClickListener {
            onDeny()
            dialog.dismiss()
        }

        //表示用户确认需要退出登录
        view.tv_volunteer_dialog_confirm_logout.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
    }
}