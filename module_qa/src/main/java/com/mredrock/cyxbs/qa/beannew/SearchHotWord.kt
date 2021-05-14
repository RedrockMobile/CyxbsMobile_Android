package com.mredrock.cyxbs.qa.beannew

import com.google.gson.annotations.SerializedName

/**
 *@Date 2020-12-16
 *@Time 11:27
 *@author SpreadWater
 *@description
 */
data class SearchHotWord (
        @SerializedName("hot_words")
        private val hot_words:List<String>?=null
){
    val hotWords: List<String> get() = hot_words ?: listOf()
}