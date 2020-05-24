package com.mredrock.cyxbs.course.utils

import android.content.Context
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.course.R

/**
 * @author Jovines
 * @create 2020-01-28 1:16 PM
 *
 * 描述:
 *   一个课表便于设置控件状态的工具函数类
 */

/**
 * 周数选择CheckBox字体颜色状态改变，下面两个全局变量是为了防止多次从资源文件中获取颜色
 */
var isCheckBox: Int? = null
var notCheckBox: Int? = null
fun weekSelectCheckBoxState(checkBox: CheckBox, context: Context) {
    if (isCheckBox == null || notCheckBox == null) {
        notCheckBox = ContextCompat.getColor(context, R.color.transactionAddPageWarpItemTextColor)
        isCheckBox = ContextCompat.getColor(context, R.color.selectFontColorForTransactionWeeks)
    }
    if (checkBox.isChecked) {
        isCheckBox?.let {
            checkBox.setTextColor(it)
        }
    } else {
        notCheckBox?.let {
            checkBox.setTextColor(it)
        }
    }
}