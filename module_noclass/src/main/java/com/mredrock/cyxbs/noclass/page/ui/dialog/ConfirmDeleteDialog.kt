package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import com.mredrock.cyxbs.noclass.R


/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      ConfirmDeleteDialog
 * @Author:         Yan
 * @CreateDate:     2022年08月24日 20:38:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    确认删除dialog
 */

class ConfirmDeleteDialog(context: Context) : AlertDialog(context) {

    /**
     * 点击确认按钮的回调
     */
    private var mOnConfirmSelected : ((ConfirmDeleteDialog) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_confirm_delete)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView(){
        findViewById<Button>(R.id.btn_noclass_dialog_delete_cancel).apply {
            setOnClickListener {
                this@ConfirmDeleteDialog.cancel()
            }
        }
        findViewById<Button>(R.id.btn_noclass_dialog_delete_confirm).apply {
            setOnClickListener {
                mOnConfirmSelected?.invoke(this@ConfirmDeleteDialog)
            }
        }
    }

    fun setConfirmSelected(onConfirmSelected : (ConfirmDeleteDialog) -> Unit) : ConfirmDeleteDialog {
        mOnConfirmSelected = onConfirmSelected
        return this
    }

    override fun cancel() {
        super.cancel()
        mOnConfirmSelected = null
    }
}