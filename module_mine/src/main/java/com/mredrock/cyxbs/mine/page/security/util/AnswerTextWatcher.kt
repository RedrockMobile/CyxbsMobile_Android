package com.mredrock.cyxbs.mine.page.security.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import com.mredrock.cyxbs.mine.R

/**
 * Author: RayleighZ
 * Time: 2020-11-19 21:34
 * Describe: 本模块输入问题的答案的泛用性TextWatcher
 */
open class AnswerTextWatcher(private val tipOF: ObservableField<String>, val button: Button, val context: Context) : TextWatcher {
    open val min = 2
    open val max = 16
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            when {
                it.length < min -> {
                    tipOF.set("请至少输入2个字符")
                    button.background = ContextCompat.getDrawable(context, R.drawable.mine_shape_round_corner_light_blue)
                }
                it.length >= max -> {
                    tipOF.set("输入已达上限")
                }
                else -> {
                    tipOF.set("")
                    button.background = ContextCompat.getDrawable(context, R.drawable.mine_shape_round_cornor_purple_blue)
                }
            }
        }
    }
}