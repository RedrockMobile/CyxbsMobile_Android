package com.mredrock.cyxbs.freshman.bean

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import java.io.Serializable

/**
 * Create by roger
 * on 2019/8/5
 */
data class Photo (
        val name: String,
        var photo: String
): Serializable
data class Scenery (
        val title: String,
        val photo: String,
        @SerializedName("message")
        val photos: List<Photo>

):Serializable

//接口返回的数据bean类
data class SceneryPhoto(
        var code: Int,
        var update_date: String,
        var text: Scenery
) : RedrockApiStatus(),Serializable