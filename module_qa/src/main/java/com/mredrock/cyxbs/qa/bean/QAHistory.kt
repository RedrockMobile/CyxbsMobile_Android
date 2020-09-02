package com.mredrock.cyxbs.qa.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by yyfbe, Date on 2020/8/16.
 */

@Entity(tableName = "qa_history")
data class QAHistory(
        @ColumnInfo(name = "qaHistory_info")
        @PrimaryKey
        val info: String,
        @ColumnInfo(name = "qaHistory_time")
        var time: Long) : Serializable
