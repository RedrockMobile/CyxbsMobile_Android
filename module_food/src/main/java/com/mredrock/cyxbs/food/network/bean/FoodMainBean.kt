package com.mredrock.cyxbs.food.network.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FoodMainBean(
    @SerializedName("eat_area")
    val eatArea: List<String>,
    @SerializedName("eat_num")
    val eatNum: List<String>,
    @SerializedName("eat_property")
    val eatProperty: List<String>,
    val picture: String
):Serializable