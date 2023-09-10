package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import com.mredrock.cyxbs.lib.utils.extensions.screenHeight
import com.mredrock.cyxbs.noclass.R

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description: 批量添加页面输入信息格式有误的提示dialog
 *
 */
class BatchInputErrorDialog(context: Context) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_batch_input_err)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initLocation()
        initView()
    }

    private fun initLocation() {
        window?.apply {
            val attr = this.attributes
            attr.gravity = Gravity.CENTER
            attr.y = -(screenHeight * 0.0765).toInt()
            attributes = attr
        }
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_batch_input_err_confirm).setOnClickListener {
            dismiss()
        }
    }
}