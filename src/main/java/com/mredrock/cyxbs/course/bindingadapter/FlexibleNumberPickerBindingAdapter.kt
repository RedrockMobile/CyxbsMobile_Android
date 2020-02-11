package com.mredrock.cyxbs.course.bindingadapter

import androidx.annotation.ArrayRes
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


    @JvmStatic
    @BindingAdapter("values")
    fun setValues(flexibleNumberPicker: FlexibleNumberPicker, @ArrayRes values: Int) {
        val strings = flexibleNumberPicker.context.resources.getStringArray(values)
        flexibleNumberPicker.displayedValues = strings
        flexibleNumberPicker.minValue = 0
        flexibleNumberPicker.maxValue = strings.size - 1
    }
}