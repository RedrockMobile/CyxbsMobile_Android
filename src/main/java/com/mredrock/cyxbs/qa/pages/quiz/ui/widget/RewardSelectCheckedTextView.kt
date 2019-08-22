package com.mredrock.cyxbs.qa.pages.quiz.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView

/**
 * Created By jay68 on 2018/11/28.
 */
class RewardSelectCheckedTextView(context: Context?, attrs: AttributeSet?) : AppCompatCheckedTextView(context, attrs) {
    private val checkedTextColor = Color.parseColor("#fefefe")
    private val unCheckedTextColor = Color.parseColor("#7195fa")

    init {
        setTextColor(checkedTextColor.takeIf { isChecked } ?: unCheckedTextColor)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        setTextColor(checkedTextColor.takeIf { checked } ?: unCheckedTextColor)
    }
}