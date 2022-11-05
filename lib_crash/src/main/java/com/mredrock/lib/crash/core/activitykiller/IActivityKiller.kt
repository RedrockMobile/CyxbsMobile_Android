package com.mredrock.lib.crash.core.activitykiller

import android.os.Message

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/6
 * @Description:
 */
interface IActivityKiller {
    fun finishActivity(message: Message)
}