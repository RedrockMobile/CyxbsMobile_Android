package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import com.mredrock.cyxbs.lib.utils.extensions.screenHeight
import com.mredrock.cyxbs.noclass.R

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description: 批量添加页面输入信息进行检查后显示信息有误的提示dialog
 *
 */
class BatchQueryErrorDialog(context: Context, private val errList: List<String>) :
    AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_batch_query_err)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initLocation()
        initView()
    }

    private fun initLocation() {
        window?.apply {
            val attr = this.attributes
            attr.gravity = Gravity.CENTER
            // y方向的偏移
            attr.y = -(screenHeight * 0.0765).toInt()
            attributes = attr
        }
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_batch_query_err_confirm).setOnClickListener {
            dismiss()
        }
        var errMessageHead: String?
        val errMessage: String
        // 确保取到errList中不为空字符的信息，否则errMessageHead为空
        errMessageHead = errList.firstOrNull { it.trim().isNotBlank() }

        if (errMessageHead.isNullOrBlank()) { // 如果errList里没有符合要求的非空字符信息
            errMessage = "输入信息有误\n请重新输入"
        } else {
            if (errMessageHead.length > 5)
                errMessageHead = "${errMessageHead.substring(0..4)}.."
            errMessage = "“${errMessageHead}”等${errList.size}人信息有误\n请重新输入"
        }
        findViewById<TextView>(R.id.noclass_batch_tv_query_err_hint).text = errMessage
    }
}