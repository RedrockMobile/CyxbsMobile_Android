package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.mredrock.cyxbs.common.BaseApp.Companion.context

/**
 * Author: RayleighZ
 * Time: 2021-08-02 9:16
 */
@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        val CHECKED_INSTANCE: TodoDatabase by lazy {
            Room.databaseBuilder(
                context,
                TodoDatabase::class.java, "todo_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
}