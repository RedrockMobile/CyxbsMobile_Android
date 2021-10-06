package com.mredrock.cyxbs.skin.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by LinTong on 2021/9/18
 * Description:
 */

data class SkinInfo(
        @SerializedName("data")
        var data: List<Data>? = null
) : Serializable {
    data class Data(@SerializedName("skin_cover")
                    val skinCover: String? = null,
                    @SerializedName("skin_download")
                    val skinDownload: String? = null,
                    @SerializedName("skin_name")
                    val skinName: String? = null,
                    @SerializedName("skin_size")
                    val skinSize: String? = null,
                    @SerializedName("skin_price")
                    val skinPrice: String? = null,
                    @SerializedName("skin_version")
                    val skinVersion: String? = null,
                    @SerializedName("ID")
                    val ID: Long,
                    @SerializedName("CreatedAt")
                    val createdAt: String? = null,
                    @SerializedName("UpdatedAt")
                    val UpdatedAt: String? = null,
                    @SerializedName("DeletedAt")
                    val DeletedAt: String? = null) : Serializable
}


