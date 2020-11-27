package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DynamicReleaseResult(@SerializedName("dynamic_id") val id: String = "") : Serializable