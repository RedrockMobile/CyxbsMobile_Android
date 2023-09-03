package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.mredrock.cyxbs.noclass.R

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description:
 *
 */
class BatchQueryErrorDialog(context: Context, private val errList: List<String>) :
    AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_batch_query_err)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_batch_query_err_confirm).setOnClickListener {
            dismiss()
        }
        var errMessageHead: String?
        val errMessage: String
        errMessageHead = errList.firstOrNull { it.trim().isNotBlank() }

        if (errMessageHead.isNullOrBlank()) {
            errMessage = "输入信息有误\n请重新输入"
        } else {
            if (errMessageHead.length > 5)
                errMessageHead = "${errMessageHead.substring(0..4)}.."
            errMessage = "${errMessageHead}等${errList.size}人信息有误\n请重新输入"
        }
        findViewById<TextView>(R.id.noclass_batch_tv_query_err_hint).text = errMessage
    }
}