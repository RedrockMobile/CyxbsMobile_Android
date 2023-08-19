package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import com.mredrock.cyxbs.noclass.R

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description:
 *
 */
class BatchInputErrorDialog(context: Context) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_batch_input_err)
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_batch_input_err_confirm).setOnClickListener {
            dismiss()
        }
    }
}