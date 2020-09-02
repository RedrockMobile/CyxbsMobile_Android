package com.mredrock.cyxbs.qa.pages.search.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.qa.bean.QAHistory

/**
 * Created by yyfbe, Date on 2020/8/16.
 */
@Database(entities = [QAHistory::class], version = 1)
abstract class QASearchHistoryDatabase : RoomDatabase() {
    abstract fun getHistoryDao(): QAHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QASearchHistoryDatabase? = null

        fun getDatabase(context: Context): QASearchHistoryDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        QASearchHistoryDatabase::class.java,
                        "qa_history_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}