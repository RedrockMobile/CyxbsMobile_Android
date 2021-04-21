package com.mredrock.cyxbs.common.network.cache

import androidx.room.Room
import com.mredrock.cyxbs.common.BaseApp

object NetworkCache {
    private val db: NetworkDatabase by lazy {
        Room.databaseBuilder(
            BaseApp.context,
            NetworkDatabase::class.java, "url_cache"
        )
            .build()
    }

    fun addCache(url: String, content: String) {
        db.networkDao()?.addUrlCache(UrlContent(url, System.currentTimeMillis(), content))
        db.networkDao()?.deleteOverdue(System.currentTimeMillis())
    }

    fun getCache(url: String): String? {
        return db.networkDao()?.getContent(url)?.content
    }
}