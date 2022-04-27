package com.redrock.module_notification.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.redrock.module_notification.R

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 16:10.
 *
 */
@SuppressLint("InflateParams")
class LoadMoreWindow(
    context: Context,
    layoutRes: Int,
    private val window: Window
) : PopupWindow() {


    init {
        window.changeWindowAlpha(0.7f)
        contentView = LayoutInflater.from(context).inflate(layoutRes, null)
        isFocusable = true
        animationStyle = R.style.mypopwindow_anim_style
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        width = context.dp2px(120.toFloat())
        height = context.dp2px(120.toFloat())

    }

    fun setOnItemClickListener(itemViewRes: Int, clickEvent: (() -> Unit)) {
        val itemView = contentView.findViewById<View>(itemViewRes)
        itemView.setOnClickListener {
            clickEvent.invoke()
        }
    }

    override fun dismiss() {
        super.dismiss()
        window.changeWindowAlpha(1.0F)
    }

}