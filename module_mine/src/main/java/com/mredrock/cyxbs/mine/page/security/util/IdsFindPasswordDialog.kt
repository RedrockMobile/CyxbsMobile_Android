package com.mredrock.cyxbs.mine.page.security.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R

/**
 * @author : why
 * @time   : 2022/8/22 13:59
 * @bless  : God bless my code
 */
class IdsFindPasswordDialog(context: Context) : Dialog(context) {
    private var mTitleText: String = ""
    private var mContentText: String = ""
    private var mBlock: IdsFindPasswordDialog.() -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //取消dialog默认背景
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.mine_dialog_find_password_ids)
        //设置title
        val title = findViewById<TextView>(R.id.mine_tv_find_password_ids_dialog_title)
        title.text = mTitleText
        //设置content
        val content = findViewById<TextView>(R.id.mine_tv_find_password_ids_dialog_content)
        content.text = mContentText
        //设置文字的点击事件
        val confirm = findViewById<TextView>(R.id.mine_tv_find_password_ids_dialog_confirm)
        confirm.setOnSingleClickListener {
            mBlock()
        }
        setCancelable(false) // 返回键不允许关闭次此 dialog
        setCanceledOnTouchOutside(false) // 点击其他区域也不允许关闭此 dialog
    }

    /**
     * 设置标题文本
     */
    fun setTitle(title: String): IdsFindPasswordDialog {
        mTitleText = title
        return this
    }

    /**
     * 设置内容文本
     */
    fun setContent(content: String): IdsFindPasswordDialog {
        mContentText = content
        return this
    }

    /**
     * 设置确认文字的点击事件
     */
    fun setConfirm(click: IdsFindPasswordDialog.() -> Unit): IdsFindPasswordDialog {
        mBlock = click
        return this
    }
}