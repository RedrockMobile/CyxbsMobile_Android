package com.mredrock.cyxbs.main.utils

import android.content.Context
import android.content.SharedPreferences
import com.mredrock.cyxbs.main.utils.Const.NOTIFICATION_SP_FILE_NAME

/**
 * Author by OkAndGreat
 * Date on 2022/5/6 20:00.
 *
 */

val Context.NotificationSp: SharedPreferences
    get() = getSharedPreferences(NOTIFICATION_SP_FILE_NAME, Context.MODE_PRIVATE)