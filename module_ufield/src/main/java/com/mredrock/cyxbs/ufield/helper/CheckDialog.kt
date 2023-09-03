package com.mredrock.cyxbs.ufield.helper

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.mredrock.cyxbs.lib.base.dailog.BaseDialog
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
) : BaseDialog<CheckDialog, CheckDialog.Data>(context) {

    open class Builder(context: Context, data: Data) :
        BaseDialog.Builder<CheckDialog, Data>(context, data) {

        override fun buildInternal(): CheckDialog {
            return CheckDialog(context)
        }
    }

    /**
     * @param content dialog 的文本内容
     * @param contentSize content 的字体大小
     * @param contentGravity 文本的 gravity（是 TextView 的 gravity 属性，非 layout_gravity）
     * @param positiveButtonText 确定按钮文本
     * @param negativeButtonText 取消按钮文本
     * @param positiveButtonColor 确定按钮颜色
     * @param negativeButtonColor 取消按钮颜色
     * @param width dialog 的宽
     * @param height dialog 的高
     * @param backgroundId dialog 的背景，默认背景应该能满足
     * @param buttonSize button 的大小，单位 dp
     */
    open class Data(
        val content: CharSequence = "弹窗内容为空",
        val contentSize: Float = 13F,
        val contentGravity: Int = Gravity.CENTER,
        override val type: DialogType = BaseDialog.Data.type,
        override val positiveButtonText: String = BaseDialog.Data.positiveButtonText,
        override val negativeButtonText: String = BaseDialog.Data.negativeButtonText,
        @ColorRes
        override val positiveButtonColor: Int = BaseDialog.Data.positiveButtonColor,
        @ColorRes
        override val negativeButtonColor: Int = BaseDialog.Data.negativeButtonColor,
        override val buttonSize: Size = BaseDialog.Data.buttonSize,
        override val width: Int = BaseDialog.Data.width,
        override val height: Int = BaseDialog.Data.height,
        @DrawableRes
        override val backgroundId: Int = BaseDialog.Data.backgroundId,
    ) : BaseDialog.Data

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
        return editText

    }

    override fun initContentView(view: View) {
        view as EditText
        view.apply {
            hint = data.content
            textSize = data.contentSize
            gravity = data.contentGravity
            addTextChangedListener(textWatcher)
        }

    }

    // 定义文本变化监听器
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            // 检查输入的文本长度
            val inputText = s.toString()
            if (inputText.length > 10) {
                // 如果超过10个字，则截取前10个字
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

