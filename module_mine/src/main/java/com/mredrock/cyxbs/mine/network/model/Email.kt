package com.mredrock.cyxbs.mine.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2020-11-30 21:29
 */
data class Email(
        @SerializedName("email")
        val email: String
) : Serializable