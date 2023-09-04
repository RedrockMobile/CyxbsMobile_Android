package com.mredrock.cyxbs.food.network.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FoodRefreshBean(
    @SerializedName("eat_property")
    val eatProperty: List<String>
):Serializable