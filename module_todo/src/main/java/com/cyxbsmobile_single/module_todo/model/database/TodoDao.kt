package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import kotlinx.coroutines.flow.Flow


/**
 * description: 修改数据库
 * author: sanhuzhen
 * date: 2024/8/20 14:11
 */
@Dao
interface TodoDao {

    /**
     * 插入数据，如果存在则替换
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todoList: List<Todo>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo?)

    /**
     * 删除数据
     */
    @Query("DELETE FROM todo_list")
    suspend fun deleteAll()

    @Query("DELETE FROM todo_list WHERE todoId = :todoId")
    suspend fun deleteTodoById(todoId: Long?)

    /**
     * 查询所有数据
     */
    @Query("select * from todo_list")
    suspend fun queryAll(): List<Todo>?
    /**
     * 查询分组的数据
     */
    @Query("select * from todo_list where type=:type")
    suspend fun queryByType(type: String): List<Todo>?

    /**
     * 按ID查询数据
     */
    @Query("select * from todo_list where todoId = :todoId")
    suspend fun queryTodoById(todoId: Long?): Todo?
}