package com.mredrock.cyxbs.discover.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsListItem(@SerializedName("date")
                        val date: String = "",
                        @SerializedName("id")
                        val id: String = "",
                        @SerializedName("title")
                        val title: String = "",
                        @SerializedName("readCount")
                        val readCount: String = "") : Serializable