package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created By jay68 on 2018/06/03.
 */
data class CheckInStatus(
    @SerializedName("checked")
    var checkedInt: Int,
    @SerializedName("serialDays")
    val serialDays: Int) : Serializable {
    val isChecked get() = checkedInt == 1
}