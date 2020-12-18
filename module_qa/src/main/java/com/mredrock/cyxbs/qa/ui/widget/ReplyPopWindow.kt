package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R

/**
 * created by zhangzhe
 * 2020/12/4
 */

object ReplyPopWindow {

    enum class AlignMode {
        LEFT,  // 左对齐
        RIGHT, // 右对齐
        MIDDLE // 居中
    }

    private var replyPopWindow: PopupWindow? = null
    private var context: Context? = null
    private var mainView: View? = null


    fun with(context: Context) {
        if (replyPopWindow?.isShowing == true) {
            replyPopWindow?.dismiss()
        }
        replyPopWindow = PopupWindow(context)
        this.context = context
        replyPopWindow?.apply {
            isOutsideTouchable = false
            isFocusable = false
        }
        mainView = LayoutInflater.from(context).inflate(R.layout.qa_popwindow_reply, null, false)
        replyPopWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setReplyName(name: String) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val s = "回复 <font color=\"#0000FF\">@${name}</font>"
            mainView?.findViewById<TextView>(R.id.qa_tv_reply_popwindow_name)?.text = Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT)
        } else {
            mainView?.findViewById<TextView>(R.id.qa_tv_reply_popwindow_name)?.text = "回复 @${name}"
        }
    }

    fun show(view: View, alignMode: AlignMode, offsetYReverse: Int) {
        if (replyPopWindow?.isShowing == true) {
            return
        }
        if (mainView == null || context == null) {
            throw IllegalStateException("IllegalState!")
        }
        replyPopWindow?.contentView = mainView

        // 先测量mainView，以免取到measuredWidth为0
        mainView!!.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        // 这样就可以根据布局的大小去计算是居中还是左右对齐了
        val offsetX = when (alignMode) {
            AlignMode.MIDDLE -> -(View.MeasureSpec.getSize(mainView!!.measuredWidth) - view.width) / 2
            AlignMode.LEFT -> 0
            AlignMode.RIGHT -> -(View.MeasureSpec.getSize(mainView!!.measuredWidth) - view.width)
        }
        // 显示弹窗
        replyPopWindow?.showAsDropDown(view, offsetX, -(offsetYReverse + View.MeasureSpec.getSize(mainView!!.measuredHeight) + view.height), Gravity.START)
    }

    fun setOnClickEvent(callback: () -> Unit) {
        mainView?.findViewById<ImageView>(R.id.qa_iv_reply_popwindow_dismiss)?.setOnSingleClickListener {
            callback.invoke()
        }
    }

    fun dismiss() {
        if (replyPopWindow?.isShowing == false) {
            return
        }
        replyPopWindow?.dismiss()
    }

    fun isShowing() = replyPopWindow?.isShowing?: false


}