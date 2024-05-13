package com.mredrock.cyxbs.ufield.helper

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.mredrock.cyxbs.lib.base.dailog.BaseChooseDialog
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.ufield.R

/**
 *  description :继承chooseDialog的dialog,加入了editText控件，当用户点击确定或者取消的时候读取文本内容执行相应的逻辑
 *
 *  author : lytMoon
 *  date : 2023/8/24 13:03
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */

open class CheckDialog protected constructor(
    context: Context,
) : BaseChooseDialog<CheckDialog, CheckDialog.DataImpl>(context) {

    open class Builder(context: Context, data: DataImpl) :
        BaseChooseDialog.Builder<CheckDialog, DataImpl>(context, data) {

        override fun buildInternal(): CheckDialog {
            return CheckDialog(context)
        }
    }

    /**
     * @param content dialog 的文本内容
     * @param contentSize content 的字体大小
     * @param contentGravity 文本的 gravity（是 TextView 的 gravity 属性，非 layout_gravity）
     */
    open class DataImpl(
        val content: CharSequence = "弹窗内容为空",
        val contentSize: Float = 13F,
        val contentGravity: Int = Gravity.CENTER,
        override val width: Int = Data.width,
        override val height: Int = Data.height,
    ) : Data by Data

    private lateinit var editText: EditText
    override fun createContentView(parent: ViewGroup): View {

        editText = EditText(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply {
                topMargin = 15.dp2px
                bottomMargin = topMargin
                leftMargin = 25.dp2px
                rightMargin = leftMargin
            }
        }
        return LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(
                TextView(parent.context).apply {
                    text = "驳回理由"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 28.dp2px
                    }
                    setTextColor(com.mredrock.cyxbs.config.R.color.config_level_four_font_color.color)
                    textSize = 18F
                    gravity = Gravity.CENTER
                }
            )
            addView(editText)
        }


    }

    override fun initContentView(view: View) {
        editText.apply {
            hint = data.content
            textSize = data.contentSize
            gravity = data.contentGravity
            setHintTextColor(Color.parseColor("#6615315B"))
            setTextColor(Color.parseColor("#CC15315B"))
            setBackgroundResource(R.drawable.ufield_shape_edit_shape)
            addTextChangedListener(textWatcher)
        }

    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            val inputText = s.toString()
            if (inputText.length > 10) {
                toast("已经超过十个字了哦")
                val limitedText = inputText.substring(0, 10)
                editText.setText(limitedText)
                editText.setSelection(limitedText.length)
            }
        }
    }

    fun getInput(): String {
        return editText.text.toString()
    }

}

