package com.mredrock.cyxbs.food.ui.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mredrock.cyxbs.food.R
import com.mredrock.cyxbs.lib.base.dailog.ChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * Create by bangbangp on 2023/2/14 17:55
 * Email:1678921845@qq.com
 * Description:
 */
class FoodMainDialog private constructor(
    context: Context,
    positiveClick: (ChooseDialog.() -> Unit)? = null,
    negativeClick: (ChooseDialog.() -> Unit)? = null,
    dismissCallback: (ChooseDialog.() -> Unit)? = null,
    cancelCallback: (ChooseDialog.() -> Unit)? = null,
    data: Data,
) : ChooseDialog(
    context,
    positiveClick,
    negativeClick,
    dismissCallback,
    cancelCallback,
    data
) {

    class Builder(context: Context, data: Data) : ChooseDialog.Builder(
        context,
        data
    ) {
        override fun build(): FoodMainDialog {
            return FoodMainDialog(
                context,
                positiveClick,
                negativeClick,
                dismissCallback,
                cancelCallback,
                data,
            )
        }
    }

    override fun createContentView(context: Context): View {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(
                //标题
                TextView(context).apply {
                    text = "温馨提示"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 28.dp2px
                    }
                    setTextColor(R.color.food_text_dialog_title.color)
                    textSize = 18F
                    gravity = Gravity.CENTER
                }
            )
            addView(
                TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        text = data.content
                        gravity = Gravity.CENTER
                        topMargin = 10.dp2px
                        leftMargin = 18.dp2px
                        rightMargin = leftMargin
                        bottomMargin = 18.dp2px
                    }
                    setTextColor(R.color.food_text_dialog.color)
                    textSize = 14F
                }
            )
            addView(
                View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1)
                    setBackgroundColor(R.color.food_text_dialog_line.color)
                }
            )
        }
    }

    override fun initContentView(view: View) {
        view as LinearLayout
    }
}