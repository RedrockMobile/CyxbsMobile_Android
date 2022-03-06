package com.mredrock.cyxbs.course.bindingadapter

import androidx.databinding.BindingAdapter
import com.mredrock.cyxbs.course.component.FlexibleNumberPicker

/**
 * Created by anriku on 2018/9/11.
 */

object FlexibleNumberPickerBindingAdapter {

    @JvmStatic
    @BindingAdapter("values")
    fun setValues(flexibleNumberPicker: FlexibleNumberPicker, values: Array<String>) {
        flexibleNumberPicker.displayedValues = values
        flexibleNumberPicker.minValue = 0
        flexibleNumberPicker.maxValue = values.size - 1
    }
}