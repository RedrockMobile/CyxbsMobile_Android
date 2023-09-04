package com.mredrock.cyxbs.food.network.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable



data class FoodResultBeanItem(
    @SerializedName("Introduce")
    val introduce: String,
    @SerializedName("FoodName")
    val name: String,
    @SerializedName("Picture")
    val picture: String,
    @SerializedName("PraiseIs")
    val praiseIs: Boolean,
    @SerializedName("PraiseNum")
    val praiseNum: Int
):Serializable