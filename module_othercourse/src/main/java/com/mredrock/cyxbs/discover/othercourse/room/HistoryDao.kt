package com.mredrock.cyxbs.discover.othercourse.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: History): Long

    @Query("SELECT * FROM History WHERE Type = :type ORDER BY historyId DESC LIMIT 0,:limit")
    fun getHistory(type: Int, limit: Int = 15): Flowable<MutableList<History>>

    @Query("UPDATE History SET verify = :verifyNew WHERE historyId = :historyId")
    fun updateHistory(historyId: Int, verifyNew: String)

    @Query("DELETE  FROM History WHERE historyId = :historyId")
    fun deleteHistory(historyId: Int)
}