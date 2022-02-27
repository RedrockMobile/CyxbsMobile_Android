package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:13
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\u0011\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005H\u00c6\u0003J%\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR&\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r\u00a8\u0006\u0019"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListSyncTimeWrapper;", "Ljava/io/Serializable;", "syncTime", "", "todoArray", "", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "(JLjava/util/List;)V", "getSyncTime", "()J", "getTodoArray", "()Ljava/util/List;", "setTodoArray", "(Ljava/util/List;)V", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "module_todo_release"})
public final class TodoListSyncTimeWrapper implements java.io.Serializable {
    @com.google.gson.annotations.SerializedName(value = "sync_time")
    private final long syncTime = 0L;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "changed_todo_array")
    private java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoArray;
    
    public final long getSyncTime() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoArray() {
        return null;
    }
    
    public final void setTodoArray(@org.jetbrains.annotations.Nullable()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> p0) {
    }
    
    public TodoListSyncTimeWrapper(long syncTime, @org.jetbrains.annotations.Nullable()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoArray) {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> component2() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:13
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper copy(long syncTime, @org.jetbrains.annotations.Nullable()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoArray) {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:13
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:13
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:13
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}