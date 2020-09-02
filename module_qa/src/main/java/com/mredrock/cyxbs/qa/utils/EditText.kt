package com.mredrock.cyxbs.qa.utils

import android.os.Build
import android.text.InputFilter
import android.widget.EditText

/**
 * Created By jay68 on 2018/10/30.
 */
internal fun EditText.getMaxLength() = filters.filter { it is InputFilter.LengthFilter }
        .map { it as InputFilter.LengthFilter }
        .map {
            if (Build.VERSION.SDK_INT >= 21) {
                it.max
            } else {
                try {
                    it.javaClass.getDeclaredField("mMax").apply { isAccessible = true }.get(it) as Int
                } catch (e: NoSuchFieldException) {
                    0
                }
            }
        }.sum()