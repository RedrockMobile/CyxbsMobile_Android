package com.mredrock.cyxbs.news.bean

import com.google.gson.annotations.SerializedName

data class NewsAttachment(@SerializedName("urlname")
                          val name: String = "",
                          @SerializedName("url")
                          val url: String = "")