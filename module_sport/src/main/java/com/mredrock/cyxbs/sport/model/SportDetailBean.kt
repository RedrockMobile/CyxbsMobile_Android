package com.mredrock.cyxbs.sport.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SportDetailBean(
    @SerializedName("award")
    val award: Int,
    @SerializedName("item")
    val item: List<Item>,
    @SerializedName("other_done")
    val otherDone: Int,
    @SerializedName("other_total")
    val otherTotal: Int,
    @SerializedName("run_done")
    val runDone: Int,
    @SerializedName("run_total")
    val runTotal: Int
) : Serializable {
    data class Item(
        @SerializedName("date")
        override val date: String,
        @SerializedName("is_award")
        override val isAward: Boolean,
        @SerializedName("spot")
        override var spot: String,
        @SerializedName("time")
        override val time: String,
        @SerializedName("type")
        override val type: String,
        @SerializedName("valid")
        override val valid: Boolean
    ) : Serializable, SportDetailItemData
}