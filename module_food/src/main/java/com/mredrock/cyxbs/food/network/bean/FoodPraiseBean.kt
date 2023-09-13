package com.mredrock.cyxbs.food.network.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FoodPraiseBean(
    val introduce: String,
    val name: String,
    val picture: String,
    @SerializedName("praise_is")
    val praiseIs: Boolean,
    @SerializedName("praise_num")
    val praiseNum: Int
):Serializable