package com.cyxbsmobile_single.module_todo.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * @date 2021-08-14
 * @author Sca RayleighZ
 */

fun remindTimeStamp2String(timeStamp: Long): String{
    return SimpleDateFormat("M月d日 hh:mm", Locale.CHINA).format(timeStamp)
}