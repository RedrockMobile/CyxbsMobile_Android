package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Cchanges on 2019/3/22
 * 解析从草稿箱传回的数据
 */
data class Content(val title: String,
                   @SerializedName("photo_thumbnail_src")
                   val pictures: String?) : Serializable