package com.mredrock.cyxbs.common.utils.down.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by yyfbe, Date on 2020/4/7.
 * 下发返回结果
 */
data class DownMessage(@SerializedName("name")
                       var name: String = "",

                       @SerializedName("text")
                       var textList: List<DownMessageText>) : Serializable

data class DownMessageText(@SerializedName("title")
                           var title: String = "",
                           @SerializedName("content")
                           var content: String = "") : Serializable