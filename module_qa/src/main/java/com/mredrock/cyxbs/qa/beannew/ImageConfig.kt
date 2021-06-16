package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-05-18 16:23
 */
data class ImageConfig(
    @SerializedName("image_limit")
    val image_limit: Int
): Serializable
