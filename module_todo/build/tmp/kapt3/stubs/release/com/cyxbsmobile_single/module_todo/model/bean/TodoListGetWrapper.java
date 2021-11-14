package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-29 1:12
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\u0002\u0010\bJ\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J3\u0010\u0013\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\n\u00a8\u0006\u001c"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListGetWrapper;", "Ljava/io/Serializable;", "todoList", "", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "syncTime", "", "delTodoArray", "(Ljava/util/List;JLjava/util/List;)V", "getDelTodoArray", "()Ljava/util/List;", "setDelTodoArray", "(Ljava/util/List;)V", "getSyncTime", "()J", "getTodoList", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "module_todo_release"})
public final class TodoListGetWrapper implements java.io.Serializable {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "changed_todo_array")
    private final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList = null;
    @com.google.gson.annotations.SerializedName(value = "sync_time")
    private final long syncTime = 0L;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "del_todo_array")
    private java.util.List<java.lang.Long> delTodoArray;
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoList() {
        return null;
    }
    
    public final long getSyncTime() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Long> getDelTodoArray() {
        return null;
    }
    
    public final void setDelTodoArray(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> p0) {
    }
    
    public TodoListGetWrapper(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList, long syncTime, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> delTodoArray) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Long> component3() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 1:12
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList, long syncTime, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> delTodoArray) {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 1:12
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 1:12
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 1:12
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}