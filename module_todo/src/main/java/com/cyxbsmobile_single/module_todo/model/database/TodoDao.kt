package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:02
 */
@Dao
interface TodoDao {
    @Insert
    fun insertTodoList(todoList: List<Todo>)//: Flowable<List<Long>>

    @Insert
    fun insertTodo(todo: Todo): Single<Long>

    @Query("SELECT * FROM todo_list ORDER by lastModifyTime desc")
    fun queryAllTodo(): Flowable<List<Todo>>

    @Query("SELECT * FROM todo_list WHERE todoId = :todoId")
    fun queryTodoById(todoId: Long): Flowable<Todo>

    @Query("SELECT * FROM todo_list WHERE isChecked = :isChecked")
    fun queryTodoByWeatherDone(isChecked: Boolean): Flowable<List<Todo>>

//    //分页查找未完成事项
//    @Query("SELECT * FROM todo_list WHERE isChecked = :isChecked ORDER by lastModifyTime limit :size offset :page-1 * :size")
//    fun queryTodoByWeatherDone(isChecked: Boolean, page: Int, size: Int): Flowable<List<Todo>>

    @Query("DELETE FROM todo_list")
    fun deleteAllTodo()

    @Query("DELETE FROM todo_list WHERE todoId = :todoId")
    fun deleteTodoById(todoId: Long)

    @Update
    fun updateTodo(todo: Todo)
}