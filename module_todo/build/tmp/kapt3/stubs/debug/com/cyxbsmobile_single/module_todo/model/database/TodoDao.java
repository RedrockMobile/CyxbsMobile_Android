package com.cyxbsmobile_single.module_todo.model.database;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:02
 */
@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\'J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\u0006\u0010\t\u001a\u00020\nH\'J\u0016\u0010\u000b\u001a\u00020\u00032\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\rH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\r0\u000fH\'J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\n0\u000f2\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\r0\u000f2\u0006\u0010\u0012\u001a\u00020\u0013H\'J\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\r0\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\'J\u0010\u0010\u0017\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\'J\u0016\u0010\u0018\u001a\u00020\u00032\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\rH\'\u00a8\u0006\u0019"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/database/TodoDao;", "", "deleteAllTodo", "", "deleteTodoById", "todoId", "", "insertTodo", "Lio/reactivex/Single;", "todo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "insertTodoList", "todoList", "", "queryAllTodo", "Lio/reactivex/Flowable;", "queryTodoById", "queryTodoByIdList", "query", "Landroidx/sqlite/db/SupportSQLiteQuery;", "queryTodoByWeatherDone", "isChecked", "", "updateTodo", "updateTodoList", "module_todo_debug"})
public abstract interface TodoDao {
    
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    public abstract void insertTodoList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    public abstract io.reactivex.Single<java.lang.Long> insertTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM todo_list ORDER by lastModifyTime desc")
    public abstract io.reactivex.Flowable<java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>> queryAllTodo();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM todo_list WHERE todoId = :todoId")
    public abstract io.reactivex.Flowable<com.cyxbsmobile_single.module_todo.model.bean.Todo> queryTodoById(long todoId);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM todo_list WHERE isChecked = :isChecked ORDER by lastModifyTime desc")
    public abstract io.reactivex.Flowable<java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>> queryTodoByWeatherDone(int isChecked);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.RawQuery(observedEntities = {com.cyxbsmobile_single.module_todo.model.bean.Todo.class})
    public abstract io.reactivex.Flowable<java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>> queryTodoByIdList(@org.jetbrains.annotations.NotNull()
    androidx.sqlite.db.SupportSQLiteQuery query);
    
    @androidx.room.Query(value = "DELETE FROM todo_list")
    public abstract void deleteAllTodo();
    
    @androidx.room.Query(value = "DELETE FROM todo_list WHERE todoId = :todoId")
    public abstract void deleteTodoById(long todoId);
    
    @androidx.room.Update()
    public abstract void updateTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo);
    
    @androidx.room.Update()
    public abstract void updateTodoList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList);
}