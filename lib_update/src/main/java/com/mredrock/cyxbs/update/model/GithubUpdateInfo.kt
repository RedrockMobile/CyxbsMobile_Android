package com.mredrock.cyxbs.update.model

import com.google.gson.annotations.SerializedName

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/10/28
 * @Description:
 */
data class GithubUpdateInfo(
    @SerializedName("assets")
    val assets: List<Asset>,
    @SerializedName("body")
    val body: String,
    @SerializedName("tag_name")
    val tag: String
) {
    data class Asset(
        @SerializedName("browser_download_url")
        val downloadUrl: String,
    ) {
    }
}