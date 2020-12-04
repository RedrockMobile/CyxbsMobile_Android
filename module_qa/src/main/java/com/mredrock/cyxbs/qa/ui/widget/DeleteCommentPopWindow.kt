package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.mredrock.cyxbs.qa.R


/**
 *@Date 2020-11-24
 *@Time 21:39
 *@author SpreadWater
 *@description  删除评论的popwindow
 */
class DeleteCommentPopWindow(context: Context?) : PopupWindow(context) {
    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val contentView: View = LayoutInflater.from(context).inflate(R.layout.qa_popwindow_delete,
                null, false)
        setContentView(contentView)
    }

    fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode: Int
        mode = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else {
            View.MeasureSpec.EXACTLY
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }
}