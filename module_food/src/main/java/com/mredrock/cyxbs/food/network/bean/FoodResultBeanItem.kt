package com.mredrock.cyxbs.food.network.bean

import com.google.gson.annotations.SerializedName

data class FoodResultBeanItem(
    @SerializedName("Introduce")
    val introduce: String,
    @SerializedName("FoodName")
    val name: String,
    @SerializedName("Picture")
    val picture: String,
    @SerializedName("PraiseIs")
    val praise_is: Boolean,
    @SerializedName("PraiseNum")
    val praise_num: Int
)