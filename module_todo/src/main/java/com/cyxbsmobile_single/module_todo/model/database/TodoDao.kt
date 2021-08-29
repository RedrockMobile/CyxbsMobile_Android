package com.cyxbsmobile_single.module_todo.model.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.cyxbsmobile_single.module_todo.model.bean.Todo
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:02
 */
@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodoList(todoList: List<Todo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo): Single<Long>

    @Query("SELECT * FROM todo_list ORDER by lastModifyTime desc")
    fun queryAllTodo(): Flowable<List<Todo>>

    @Query("SELECT * FROM todo_list WHERE todoId = :todoId")
    fun queryTodoById(todoId: Long): Flowable<Todo>

    @Query("SELECT * FROM todo_list WHERE isChecked = :isChecked ORDER by lastModifyTime desc")
    fun queryTodoByWeatherDone(isChecked: Boolean): Flowable<List<Todo>>

//    //分页查找未完成事项
//    @Query("SELECT * FROM todo_list WHERE isChecked = :isChecked ORDER by lastModifyTime limit :size offset :page-1 * :size")
//    fun queryTodoByWeatherDone(isChecked: Boolean, page: Int, size: Int): Flowable<List<Todo>>


    @RawQuery(observedEntities = [Todo::class])
    fun queryTodoByIdList(query: SupportSQLiteQuery): Flowable<List<Todo>>

    @Query("DELETE FROM todo_list")
    fun deleteAllTodo()

    @Query("DELETE FROM todo_list WHERE todoId = :todoId")
    fun deleteTodoById(todoId: Long)

    @Update
    fun updateTodo(todo: Todo)

    @Update
    fun updateTodoList(todoList: List<Todo>)
}