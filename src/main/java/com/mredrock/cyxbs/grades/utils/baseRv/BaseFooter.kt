package com.mredrock.cyxbs.grades.utils.baseRv

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class BaseFooter(context: Context?) : LinearLayout(context) {
    private val textView: TextView

    init {
        this.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView = TextView(context)
        textView.apply {
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 40)
        }
        addView(textView)
        showLoading()
    }

    private fun showLoading() {
        textView.text = "加载中..."
    }

    fun getTextView(): TextView = textView

}