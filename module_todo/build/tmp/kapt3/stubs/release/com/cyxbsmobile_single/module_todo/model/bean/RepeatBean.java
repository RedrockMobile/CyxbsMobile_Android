package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * @date 2021-08-19
 * @author Sca RayleighZ
 * 需要重复提醒的todo的索引
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u001a"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/RepeatBean;", "Ljava/io/Serializable;", "nextDate", "Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;", "todoId", "", "(Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;J)V", "getNextDate", "()Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;", "setNextDate", "(Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;)V", "getTodoId", "()J", "setTodoId", "(J)V", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "module_todo_release"})
public final class RepeatBean implements java.io.Serializable {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "next_date")
    private com.cyxbsmobile_single.module_todo.model.bean.DateBeen nextDate;
    @com.google.gson.annotations.SerializedName(value = "todo_id")
    private long todoId;
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.DateBeen getNextDate() {
        return null;
    }
    
    public final void setNextDate(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.DateBeen p0) {
    }
    
    public final long getTodoId() {
        return 0L;
    }
    
    public final void setTodoId(long p0) {
    }
    
    public RepeatBean(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.DateBeen nextDate, long todoId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.DateBeen component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    /**
     * @date 2021-08-19
     * @author Sca RayleighZ
     * 需要重复提醒的todo的索引
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.RepeatBean copy(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.DateBeen nextDate, long todoId) {
        return null;
    }
    
    /**
     * @date 2021-08-19
     * @author Sca RayleighZ
     * 需要重复提醒的todo的索引
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * @date 2021-08-19
     * @author Sca RayleighZ
     * 需要重复提醒的todo的索引
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * @date 2021-08-19
     * @author Sca RayleighZ
     * 需要重复提醒的todo的索引
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}