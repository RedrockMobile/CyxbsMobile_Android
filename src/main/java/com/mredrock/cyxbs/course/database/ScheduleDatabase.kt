package com.mredrock.cyxbs.course.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
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
        //这里不弄单例模式的原因是因为他人课表和自己课表不能用同一个数据库，损失了性能但是可以节流
        fun getDatabase(context: Context,stuNum:String): ScheduleDatabase? {
            INSTANCE = Room.databaseBuilder(context,
                    ScheduleDatabase::class.java, "schedules_database$stuNum").build()
            return INSTANCE
        }
    }
}