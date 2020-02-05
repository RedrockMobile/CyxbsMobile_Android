package com.mredrock.cyxbs.discover.othercourse.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: History)

    @Query("SELECT * FROM History WHERE Type = :type ORDER BY historyId DESC LIMIT 0,:limit")
    fun getHistory(type: Int, limit: Int = 15): Flowable<List<History>>
}