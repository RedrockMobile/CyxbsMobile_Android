package com.mredrock.cyxbs.food.network.bean

data class FoodPraiseBean(
    val introduce: String,
    val name: String,
    val picture: String,
    val praise_is: Boolean,
    val praise_num: Int
)