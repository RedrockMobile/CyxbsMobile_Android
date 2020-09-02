package com.mredrock.cyxbs.mine.util.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText

/**
 * Created by zia on 2018/9/10.
 */
fun EditText.setAutoGravity() {
    post { changeGravity() }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            changeGravity()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

private fun EditText.changeGravity() {
    gravity = if (lineCount <= 1) {
        Gravity.END or Gravity.CENTER_VERTICAL
    } else {
        Gravity.START or Gravity.CENTER_VERTICAL
    }
}