package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import io.reactivex.Flowable

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:02
 */
@Dao
interface TodoDao {
    @Insert
    fun insertTodoList(todoList: List<Todo>)

    @Insert
    fun insertTodo(todo: Todo)

    @Query("SELECT * FROM todo_list")
    fun queryAllTodo(): Flowable<List<Todo>>

    @Query("SELECT * FROM todo_list WHERE todoId = :todoId")
    fun queryTodoById(todoId: Long): Flowable<Todo>

    @Query("DELETE FROM todo_list")
    fun deleteAllTodo()

    @Update
    fun updateTodo(todo: Todo)
}