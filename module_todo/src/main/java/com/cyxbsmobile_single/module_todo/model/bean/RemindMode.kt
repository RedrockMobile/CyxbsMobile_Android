package com.cyxbsmobile_single.module_todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 写成ArrayList是为了方便添加数据
 */
data class RemindMode(
    @SerializedName("repeat_mode")
    var repeatMode: Int,
    @SerializedName("date")
    var date: ArrayList<String>,
    @SerializedName("week")
    var week: ArrayList<Int>,
    @SerializedName("day")
    var day: ArrayList<Int>,
    @SerializedName("notify_datetime")
    var notifyDateTime: String?
): Serializable{
    companion object{
        const val NONE = 0
        const val DAY = 1
        const val WEEK = 2
        const val MONTH = 3
        const val YEAR = 4

        fun generateDefaultRemindMode(): RemindMode{
            return RemindMode(
                NONE,
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                ""
            )
        }
    }
}
