package com.mredrock.cyxbs.widget.repo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.widget.repo.bean.Affair

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/8/6 20:09
 */
@Database(entities = [Affair::class], version = 1, exportSchema = false)
abstract class AffairDatabase : RoomDatabase() {

    abstract fun getAffairDao(): AffairDao

    companion object {
        @Volatile
        private var database: AffairDatabase? = null

        @JvmStatic
        private fun createInstance(context: Context) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AffairDatabase::class.java,
                "affair_database"
            ).build()
        }

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): AffairDatabase {
            if (database == null) {
                createInstance(context)
            }
            return database!!
        }
    }
}