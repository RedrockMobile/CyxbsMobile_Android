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
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.dialog
 * @ClassName:      ConfirmReturnDialog
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 05:53:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    确定退出dialog
 */

class ConfirmReturnDialog (context: Context) : AlertDialog(context) {

    /**
     * 退出按钮回调
     */
    private var mOnReturnClick : ((ConfirmReturnDialog) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_confirm_return)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView(){

        findViewById<Button>(R.id.btn_noclass_dialog_return_continue).apply {
            setOnClickListener {
                cancel()
            }
        }

        findViewById<Button>(R.id.btn_noclass_dialog_return_back).apply {
            setOnClickListener {
                mOnReturnClick?.invoke(this@ConfirmReturnDialog)
            }
        }
    }

    fun setOnReturnClick(onReturnClick : (ConfirmReturnDialog) -> Unit) : ConfirmReturnDialog{
        return this.apply {
            mOnReturnClick = onReturnClick
        }
    }

}