package com.redrock.module_notification.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import com.redrock.module_notification.R

/**
 * ...
 * @author: Black-skyline (Hu Shujun)
 * @email: 2031649401@qq.com
 * @date: 2023/8/23
 * @Description:
 *
 */
class RevokeReminderDialog(context: Context) : AlertDialog(context) {
    /**
     * 点击确认按钮的回调
     */
    private var mOnConfirmSelected : ((RevokeReminderDialog) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_dialog_revoke_reminder)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView(){
        findViewById<Button>(R.id.notification_dialog_btn_cancel_revoke).apply {
            setOnClickListener {
                this@RevokeReminderDialog.cancel()
            }
        }
        findViewById<Button>(R.id.notification_dialog_btn_confirm_revoke).apply {
            setOnClickListener {
                mOnConfirmSelected?.invoke(this@RevokeReminderDialog)
            }
        }
    }

    fun setConfirmSelected(onConfirmSelected : (RevokeReminderDialog) -> Unit) : RevokeReminderDialog {
        mOnConfirmSelected = onConfirmSelected
        return this
    }

    override fun cancel() {
        super.cancel()
        mOnConfirmSelected = null
    }
}