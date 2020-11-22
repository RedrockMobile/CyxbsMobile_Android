package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName

/**
 *@author zhangzhe
 *@date 2020/11/22
 *@description
 */

data class SearchResult(
        @SerializedName("knowledge")
        val knowledge: Knowledge? = null,
        @SerializedName("dynamic_list")
        private val _dynamicList: List<Dynamic>? = null,
        @SerializedName("topic_list")
        private val _topicList: List<Topic>? = null
) {
    val dynamicList: List<Dynamic> get() = _dynamicList ?: listOf()
    val topicList: List<Topic> get() = _topicList ?: listOf()
}