package com.cyxbsmobile_single.module_todo.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cyxbsmobile_single.module_todo.R
import com.cyxbsmobile_single.module_todo.util.getColor
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * author : zeq
 * email : 1301731619@qq.com
 * date : 2024/8/16 17:34
 */
class DetailAlarmDialog private constructor(
    context: Context,
) : ChooseDialog(context) {
    class Builder(context: Context) : ChooseDialog.Builder(
        context,
        DataImpl(
            buttonWidth = 92,
            buttonHeight = 36,
        )
    ) {
        override fun buildInternal(): DetailAlarmDialog {
            return DetailAlarmDialog(context)
        }
    }

    // 内容
    private val mTvContent = TextView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,//宽度
            LinearLayout.LayoutParams.WRAP_CONTENT //高度
        ).apply {
            topMargin = 10.dp2px
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
                    text = "确定是否返回"
                }
            )
            addView(mTvContent)
        }
    }

    override fun initContentView(view: View) {
        mTvContent.text = "返回后修改内容不保存"
    }
}