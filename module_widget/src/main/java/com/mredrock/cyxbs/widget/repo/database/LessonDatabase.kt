package com.mredrock.cyxbs.widget.repo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mredrock.cyxbs.widget.repo.bean.Lesson

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 */
@Database(entities = [Lesson::class], version = 1, exportSchema = false)
abstract class LessonDatabase : RoomDatabase() {

    abstract fun getLessonDao(): LessonDao

    companion object {
        const val MY_STU_NUM = "my_stu_num"
        const val OTHERS_STU_NUM = "others_stu_num"
        @Volatile
        private var database: LessonDatabase? = null

        @JvmStatic
        private fun createInstance(context: Context) {
            database = Room.databaseBuilder(
                context.applicationContext,
                LessonDatabase::class.java,
                "lesson_database"
            ).build()
        }

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): LessonDatabase {
            if (database == null) {
                createInstance(context)
            }
            return database!!
        }
    }
}