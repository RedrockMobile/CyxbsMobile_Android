package com.mredrock.cyxbs.discover.news.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsDetails(@SerializedName("date")
                       val date: String = "",
                       @SerializedName("author")
                       val author: String = "",
                       @SerializedName("files")
                       val files: List<NewsAttachment>?,
                       @SerializedName("id")
                       val id: String = "",
                       @SerializedName("title")
                       val title: String = "",
                       @SerializedName("readCount")
                       val readCount: String = "",
                       @SerializedName("content")
                       val content: String = ""): Serializable