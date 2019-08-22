package com.mredrock.cyxbs.course.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import com.mredrock.cyxbs.course.component.RedRockTextView

/**
 * Created by anriku on 2018/9/11.
 */

object RedRockTextViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("nowWeek")
    fun isDaysOfMonthShouldShow(redRockTextView: RedRockTextView, nowWeek: Int) {
        if (nowWeek == 0) {
            redRockTextView.visibility = View.GONE
        }
    }
}