package com.mredrock.cyxbs.skin.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by LinTong on 2021/9/18
 * Description:
 */

data class SkinInfo(
        @SerializedName("skin_data")
        val skin_data: List<Data>
) : Serializable {
    data class Data(@SerializedName("skin_cover")
                    val skinCover: String,
                    @SerializedName("skin_download")
                    val skinDownload: String,
                    @SerializedName("skin_name")
                    val skinName: String,
                    @SerializedName("skin_size")
                    val skinSize: String,
                    @SerializedName("skin_price")
                    val skinPrice: String,
                    @SerializedName("skin_version")
                    val skinVersion: String) : Serializable
}


