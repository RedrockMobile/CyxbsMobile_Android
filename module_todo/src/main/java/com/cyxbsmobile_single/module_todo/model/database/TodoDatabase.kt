package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.*
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp.Companion.context

/**
 * Author: RayleighZ
 * Time: 2021-08-02 9:16
 */
@Database(entities = [Todo::class], version = 7, exportSchema = false)
@TypeConverters(Convert::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        val INSTANCE: TodoDatabase by lazy {
            Room.databaseBuilder(
                context,
                TodoDatabase::class.java, "todo_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
}

class Convert{
    @TypeConverter
    fun remindMode2String(value: RemindMode): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun string2RemindMode(value: String): RemindMode {
        return Gson().fromJson(value, RemindMode::class.java)
    }
}