package com.mredrock.cyxbs.discover.news.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsAttachment(@SerializedName("urlname")
                          val name: String = "",
                          @SerializedName("url")
                          val url: String = "") : Serializable