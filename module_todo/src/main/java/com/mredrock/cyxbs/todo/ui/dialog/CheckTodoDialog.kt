package com.mredrock.cyxbs.todo.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mredrock.cyxbs.todo.R
import com.mredrock.cyxbs.todo.util.getColor
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * @Project: CyxbsMobile_Android
 * @File: CheckTodoDialog
 * @Author: 86199
 * @Date: 2024/8/25
 * @Description: 第一次提交完成的提示
 */
class CheckTodoDialog private constructor(
    context: Context,
) : ChooseDialog(context) {
    class Builder(context: Context) : ChooseDialog.Builder(
        context,
        DataImpl(
            buttonWidth = 92,
            buttonHeight = 36,
        )
    ) {
        override fun buildInternal(): CheckTodoDialog {
            return CheckTodoDialog(context)
        }
    }

    // 内容
    private val mTvContent = TextView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,//宽度
            LinearLayout.LayoutParams.WRAP_CONTENT //高度
        ).apply {
            topMargin = 5.dp2px
            bottomMargin = 31.dp2px
        }
        setTextColor(getColor(R.color.todo_check_line_color))
        textSize = 15F
        gravity = Gravity.CENTER //居中显示
    }

    override fun createContentView(parent: ViewGroup): View {
        return LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(
                // 标题
                TextView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 31.dp2px
                    }
                    setTextColor(getColor(R.color.todo_check_line_color))
                    textSize = 15F
                    gravity = Gravity.CENTER
                    text = "确定是否完成"
                }
            )
            addView(mTvContent)
        }
    }

    override fun initContentView(view: View) {
        mTvContent.text = "完成后无法恢复"
    }
}