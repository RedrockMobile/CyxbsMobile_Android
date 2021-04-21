package com.mredrock.cyxbs.common.network.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UrlContent::class], version = 1)
abstract class NetworkDatabase : RoomDatabase() {
    abstract fun networkDao(): NetworkContentDao?
}