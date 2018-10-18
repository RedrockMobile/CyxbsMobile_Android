package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName

/**
 * Created By jay68 on 2018/06/03.
 */
data class CheckInStatus(@SerializedName("checked")
                         var checkedInt: Int,
                         @SerializedName("serialDays")
                         val serialDays: Int) {
    val isChecked get() = checkedInt == 1
}