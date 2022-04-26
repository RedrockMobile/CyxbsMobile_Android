package com.redrock.module_notification.util

import android.content.Context
import com.redrock.module_notification.util.Constant.NOTIFICATION_SP_FILE_NAME

/**
 * Author by OkAndGreat
 * Date on 2022/4/26 16:13.
 * 扩展函数类
 */

val Context.NotificationSp
get() = getSharedPreferences(NOTIFICATION_SP_FILE_NAME, Context.MODE_PRIVATE)

