package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.*
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.google.gson.Gson
import com.mredrock.cyxbs.lib.utils.extensions.appContext

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/20 14:21
 */
@Database(entities = [Todo::class], version = 1)
@TypeConverters(Convert::class)
abstract class TodoDataBase: RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object{
        val INSTANCE by lazy {
            Room.databaseBuilder(
                appContext,
                TodoDataBase::class.java,
                "todo_db"
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