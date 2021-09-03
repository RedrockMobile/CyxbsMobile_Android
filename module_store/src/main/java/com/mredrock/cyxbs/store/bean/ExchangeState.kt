package com.mredrock.cyxbs.store.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExchangeState(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("msg")
    val msg: String
) : Serializable