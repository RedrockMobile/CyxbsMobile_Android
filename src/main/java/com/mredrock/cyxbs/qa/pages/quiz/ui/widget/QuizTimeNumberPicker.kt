package com.mredrock.cyxbs.qa.pages.quiz.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker

/**
 * Created By jay68 on 2018/11/27.
 */
class QuizTimeNumberPicker(context: Context?, attrs: AttributeSet?) : NumberPicker(context, attrs) {
    constructor(context: Context?) : this(context, null)

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child is EditText) {
            child.setTextColor(Color.parseColor("#d97195fa"))
        }
    }
}