package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Cchanges on 2019/3/22
 * 解析从草稿箱传回的数据
 */
data class Content(@SerializedName("title")
                   val title: String,
                   @SerializedName("photo_url")
                   val pictures: ArrayList<String>) : Serializable