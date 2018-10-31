package com.mredrock.cyxbs.news.bean

import com.google.gson.annotations.SerializedName

data class NewsListItem(@SerializedName("teaName")
                        val teaName: String = "",
                        @SerializedName("pubIp")
                        val pubIp: String = "",
                        @SerializedName("pubTime")
                        val pubTime: String = "",
                        @SerializedName("pubId")
                        val pubId: String = "",
                        @SerializedName("days")
                        val days: Int = 0,
                        @SerializedName("dirId")
                        val dirId: String = "",
                        @SerializedName("totalCount")
                        val totalCount: Int = 0,
                        @SerializedName("title")
                        val title: String = "",
                        @SerializedName("readCount")
                        val readCount: Int = 0,
                        @SerializedName("fileId")
                        val fileId: Int = 0,
                        @SerializedName("dirName")
                        val dirName: String = "")