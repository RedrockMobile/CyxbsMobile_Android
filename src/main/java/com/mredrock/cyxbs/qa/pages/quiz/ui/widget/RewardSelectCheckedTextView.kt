package com.mredrock.cyxbs.qa.pages.quiz.ui.widget

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatCheckedTextView
import android.util.AttributeSet
import android.view.Gravity
import com.mredrock.cyxbs.qa.R

/**
 * Created By jay68 on 2018/11/28.
 */
class RewardSelectCheckedTextView(context: Context?, attrs: AttributeSet?) : AppCompatCheckedTextView(context, attrs) {
    private val checkedTextColor = Color.parseColor("#fefefe")
    private val unCheckedTextColor = Color.parseColor("#7195fa")

    init {
        isChecked = isChecked   //初始化颜色
        textSize = 16f
        background = resources.getDrawable(R.drawable.qa_selector_quiz_reward_checked_tv)
        gravity = Gravity.CENTER
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        setTextColor(checkedTextColor.takeIf { checked } ?: unCheckedTextColor)
    }
}