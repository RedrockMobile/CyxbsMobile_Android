package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode
import com.cyxbsmobile_single.module_todo.model.bean.RemindMode.Companion.generateDefaultRemindMode
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import com.google.gson.Gson
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.processLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * description:
 * author: sanhuzhen
 * date: 2024/8/20 14:21
 */
@Database(entities = [Todo::class], version = 2)
@TypeConverters(Convert::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        val instance: TodoDatabase
            get() = INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    appContext,
                    TodoDatabase::class.java,
                    "todo_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                val database = INSTANCE ?: return@execute
                                if (appContext.getSp("todo")
                                        .getLong("TODO_LAST_MODIFY_TIME", 0L) == 0L
                                ) {
                                    // 在数据库创建时插入默认数据
                                    database.runInTransaction {
                                        val dao = database.todoDao()
                                        val defaultTodos = listOf(
                                            Todo(
                                                1,
                                                "长按可以拖动我哟",
                                                "",
                                                0,
                                                generateDefaultRemindMode(),
                                                System.currentTimeMillis(),
                                                "",
                                                "",
                                                0,
                                                0
                                            ),
                                            Todo(
                                                2,
                                                "左滑可置顶或者删除",
                                                "",
                                                0,
                                                generateDefaultRemindMode(),
                                                System.currentTimeMillis(),
                                                "",
                                                "",
                                                0,
                                                0
                                            ),
                                            Todo(
                                                3,
                                                "点击查看代办详情",
                                                "",
                                                0,
                                                generateDefaultRemindMode(),
                                                System.currentTimeMillis(),
                                                "",
                                                "",
                                                0,
                                                0
                                            )
                                        )
                                        processLifecycleScope.launch(Dispatchers.IO) {
                                            dao.insertAll(defaultTodos)
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
    }
}

class Convert {
    @TypeConverter
    fun remindMode2String(value: RemindMode): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun string2RemindMode(value: String): RemindMode {
        return Gson().fromJson(value, RemindMode::class.java)
    }
}