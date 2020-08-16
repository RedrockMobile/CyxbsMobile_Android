package com.mredrock.cyxbs.qa.pages.search.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by yyfbe, Date on 2020/8/16.
 */

@Entity
data class QAHistory(val info: String, var time: Long) {
    @PrimaryKey(autoGenerate = true)
    var historyId: Int = 0
}