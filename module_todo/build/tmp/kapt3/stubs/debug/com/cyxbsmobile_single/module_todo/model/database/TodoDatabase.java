package com.cyxbsmobile_single.module_todo.model.database;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 9:16
 */
@androidx.room.TypeConverters(value = {com.cyxbsmobile_single.module_todo.model.database.Convert.class})
@androidx.room.Database(entities = {com.cyxbsmobile_single.module_todo.model.bean.Todo.class}, version = 8, exportSchema = false)
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00052\u00020\u0001:\u0001\u0005B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&\u00a8\u0006\u0006"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/database/TodoDatabase;", "Landroidx/room/RoomDatabase;", "()V", "todoDao", "Lcom/cyxbsmobile_single/module_todo/model/database/TodoDao;", "Companion", "module_todo_debug"})
public abstract class TodoDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy INSTANCE$delegate = null;
    public static final com.cyxbsmobile_single.module_todo.model.database.TodoDatabase.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cyxbsmobile_single.module_todo.model.database.TodoDao todoDao();
    
    public TodoDatabase() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\t"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/database/TodoDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/cyxbsmobile_single/module_todo/model/database/TodoDatabase;", "getINSTANCE", "()Lcom/cyxbsmobile_single/module_todo/model/database/TodoDatabase;", "INSTANCE$delegate", "Lkotlin/Lazy;", "module_todo_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.cyxbsmobile_single.module_todo.model.database.TodoDatabase getINSTANCE() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}