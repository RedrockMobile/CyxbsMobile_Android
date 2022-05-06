package com.mredrock.cyxbs.discover.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Author by OkAndGreat
 * Date on 2022/5/4 19:30.
 *
 */

//消息模块的sp file name
const val NOTIFICATION_SP_FILE_NAME = "notification_module_sp_file"

val Context.NotificationSp: SharedPreferences
    get() = getSharedPreferences(NOTIFICATION_SP_FILE_NAME, Context.MODE_PRIVATE)