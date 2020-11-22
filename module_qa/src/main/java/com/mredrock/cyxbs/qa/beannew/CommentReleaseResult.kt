package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created By jay68 on 2018/12/3.
 */
data class CommentReleaseResult(@SerializedName("dynamic_id") val id: String = "") : Serializable