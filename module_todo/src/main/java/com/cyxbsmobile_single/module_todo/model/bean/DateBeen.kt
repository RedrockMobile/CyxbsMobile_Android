package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 这里使用序列化的原因是为了用JSON存储
 * 使得只用进行一次操作
 */
data class DateBeen(
    @SerializedName("month")
    val month: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("week")
    val week: Int
): Serializable