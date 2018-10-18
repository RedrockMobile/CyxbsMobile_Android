package com.mredrock.cyxbs.course.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mredrock.cyxbs.course.network.Affair
import com.mredrock.cyxbs.course.network.Course

/**
 * Created by anriku on 2018/8/14.
 */

@Database(entities = [Course::class, Affair::class], version = 1, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract fun affairDao(): AffairDao

    abstract fun courseDao(): CourseDao

    /**
     * use singleton to avoid concurrent problem
     */
    companion object {
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase? {
            if (INSTANCE == null) {
                synchronized(ScheduleDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context,
                                ScheduleDatabase::class.java, "schedules_database").build()
                    }
                }
            }
            return INSTANCE
        }
    }
}