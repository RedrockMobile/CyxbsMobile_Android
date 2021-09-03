package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExchangeRecord(
    @SerializedName("date")
    val date: Long,
    @SerializedName("goods_name")
    val goodsName: String,
    @SerializedName("goods_price")
    val goodsPrice: Int,
    @SerializedName("is_received")
    val isReceived: Boolean,
    @SerializedName("order_id")
    val orderId: Int
) : Serializable