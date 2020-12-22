package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2020-11-30 18:17
 */
data class ConfirmQuestion(
        @SerializedName("code")
        val code: Int
) : Serializable