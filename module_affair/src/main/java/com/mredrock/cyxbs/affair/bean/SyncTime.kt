package com.mredrock.cyxbs.affair.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * author : QT
 * date : 2024/8/23 21:20
 */
data class SyncTime(
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("is_sync_time_exist")
    val isSyncTimeExit: Boolean
) : Serializable

