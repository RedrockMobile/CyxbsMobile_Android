package com.mredrock.cyxbs.course.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Created by anriku on 2018/9/11.
 */

object TextViewBidingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["month", "nowWeek"])
    fun setMonth(textView: TextView, month: String, nowWeek: Int) {
        if (nowWeek != 0) {
            textView.text = month
        }
    }
}