package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
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
class BatchQueryErrorDialog(context: Context, private val errList: List<String>) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_batch_query_err)
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_batch_query_err_confirm).setOnClickListener {
            dismiss()
        }
        var errMessageHead: String
        if (errList[0].length > 5){
            errMessageHead = "${errList[0].substring(0..4)}.."
        }
        errMessageHead = errList[0]

        val errMessage = "${errMessageHead}等${errList.size}人信息有误\\n请重新输入"
        findViewById<TextView>(R.id.noclass_batch_tv_query_err_hint).text = errMessage
    }
}