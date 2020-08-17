package com.mredrock.cyxbs.qa.pages.search.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by yyfbe, Date on 2020/8/16.
 */

@Entity
data class QAHistory(
        @ColumnInfo(name = "qaHistory_info")
        val info: String,
        @ColumnInfo(name = "qaHistory_time")
        var time: Long,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "qaHistory_id")
        var historyId: Int = 0)
