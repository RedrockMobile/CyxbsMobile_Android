package com.mredrock.cyxbs.common.utils.extensions

import android.text.SpannableStringBuilder
import android.text.Spanned

/**
 * Created by yyfbe, Date on 2020/8/20.
 * anko bridge
 */

fun SpannableStringBuilder.appendln(text: CharSequence, vararg spans: Any) {
    append(text, *spans)
    appendln()
}

fun SpannableStringBuilder.appendln(text: CharSequence, span: Any) {
    append(text, span)
    appendln()
}

fun SpannableStringBuilder.append(text: CharSequence, vararg spans: Any) {
    val textLength = text.length
    append(text)
    spans.forEach { span ->
        setSpan(span, this.length - textLength, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}