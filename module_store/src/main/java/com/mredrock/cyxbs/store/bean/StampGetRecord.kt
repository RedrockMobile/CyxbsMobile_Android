package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StampGetRecord(
    @SerializedName("date")
    val date: Long,
    @SerializedName("task_income")
    val taskIncome: Int,
    @SerializedName("task_name")
    val taskName: String
) : Serializable