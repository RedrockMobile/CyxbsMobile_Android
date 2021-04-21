package com.mredrock.cyxbs.common.network.cache

import androidx.room.*

@Dao
interface NetworkContentDao {
    // 7天过期
    @Query("DELETE FROM url_cache WHERE (time - :time) > 604800000")
    fun deleteOverdue(time: Long)

    @Query("SELECT * FROM url_cache WHERE url = :url LIMIT 1")
    fun getContent(url: String): UrlContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUrlCache(urlContent: UrlContent)
}

