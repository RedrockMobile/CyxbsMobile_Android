package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:23
 */
data class SyncTime(
    @SerializedName("sync_time")
    val syncTime: Long
): Serializable
