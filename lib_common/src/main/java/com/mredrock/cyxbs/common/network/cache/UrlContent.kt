package com.mredrock.cyxbs.common.network.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "url_cache")
data class UrlContent(
    @ColumnInfo(name = "url")
    @PrimaryKey
    var url: String = "", // 存{url?key=value......}这种格式，不管是否是get，post，一定要拼接参数

    @ColumnInfo(name = "time")
    var time: Long = 0L, // 访问时间，以用来过期

    @ColumnInfo(name = "content")
    var content: String = "" // 访问链接直接返回的内容，一般为json格式
) : Serializable

