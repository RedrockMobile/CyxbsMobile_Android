package com.mredrock.cyxbs.qa.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.sp
import com.mredrock.cyxbs.qa.R

/**
 * Created By jay68 on 2018/11/27.
 */
class QuizTimeNumberPicker(context: Context?, attrs: AttributeSet?) : NumberPicker(context, attrs) {
    constructor(context: Context?) : this(context, null)

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child is EditText) {
            child.setTextColor(ContextCompat.getColor(context, R.color.qa_quiz_time_select_color))
            child.textSize = context.sp(8).toFloat()
        }
    }
}