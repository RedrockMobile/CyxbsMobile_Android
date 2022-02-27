package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-01 17:04
 */
@androidx.room.Entity(tableName = "todo_list")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001f\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0087\b\u0018\u0000 /2\u00020\u00012\b\u0012\u0004\u0012\u00020\u00000\u0002:\u0001/B5\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\rJ\u0011\u0010!\u001a\u00020\t2\u0006\u0010\"\u001a\u00020\u0000H\u0096\u0002J\t\u0010#\u001a\u00020\u0004H\u00c6\u0003J\t\u0010$\u001a\u00020\u0006H\u00c6\u0003J\t\u0010%\u001a\u00020\u0006H\u00c6\u0003J\t\u0010&\u001a\u00020\tH\u00c6\u0003J\t\u0010\'\u001a\u00020\u000bH\u00c6\u0003J\t\u0010(\u001a\u00020\u0004H\u00c6\u0003JE\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u0004H\u00c6\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010\"\u001a\u0004\u0018\u00010,H\u00d6\u0003J\t\u0010-\u001a\u00020\tH\u00d6\u0001J\t\u0010.\u001a\u00020\u0006H\u00d6\u0001R\u001e\u0010\u0007\u001a\u00020\u00068\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001e\u0010\f\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001e\u0010\n\u001a\u00020\u000b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001e\u0010\u0005\u001a\u00020\u00068\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u000f\"\u0004\b\u001e\u0010\u0011R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0016\"\u0004\b \u0010\u0018\u00a8\u00060"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "Ljava/io/Serializable;", "", "todoId", "", "title", "", "detail", "isChecked", "", "remindMode", "Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;", "lastModifyTime", "(JLjava/lang/String;Ljava/lang/String;ILcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;J)V", "getDetail", "()Ljava/lang/String;", "setDetail", "(Ljava/lang/String;)V", "()I", "setChecked", "(I)V", "getLastModifyTime", "()J", "setLastModifyTime", "(J)V", "getRemindMode", "()Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;", "setRemindMode", "(Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;)V", "getTitle", "setTitle", "getTodoId", "setTodoId", "compareTo", "other", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "", "hashCode", "toString", "Companion", "module_todo_debug"})
public final class Todo implements java.io.Serializable, java.lang.Comparable<com.cyxbsmobile_single.module_todo.model.bean.Todo> {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @com.google.gson.annotations.SerializedName(value = "todo_id")
    private long todoId;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "title")
    private java.lang.String title;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "detail")
    private java.lang.String detail;
    @com.google.gson.annotations.SerializedName(value = "is_done")
    private int isChecked;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "remind_mode")
    private com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode;
    @com.google.gson.annotations.SerializedName(value = "last_modify_time")
    private long lastModifyTime;
    public static final com.cyxbsmobile_single.module_todo.model.bean.Todo.Companion Companion = null;
    
    @java.lang.Override()
    public int compareTo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo other) {
        return 0;
    }
    
    public final long getTodoId() {
        return 0L;
    }
    
    public final void setTodoId(long p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    public final void setTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDetail() {
        return null;
    }
    
    public final void setDetail(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public final int isChecked() {
        return 0;
    }
    
    public final void setChecked(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.RemindMode getRemindMode() {
        return null;
    }
    
    public final void setRemindMode(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode p0) {
    }
    
    public final long getLastModifyTime() {
        return 0L;
    }
    
    public final void setLastModifyTime(long p0) {
    }
    
    public Todo(long todoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String detail, int isChecked, @org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode, long lastModifyTime) {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.RemindMode component5() {
        return null;
    }
    
    public final long component6() {
        return 0L;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-01 17:04
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.Todo copy(long todoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String detail, int isChecked, @org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode, long lastModifyTime) {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-01 17:04
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-01 17:04
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-01 17:04
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/Todo$Companion;", "", "()V", "generateEmptyTodo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "module_todo_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.cyxbsmobile_single.module_todo.model.bean.Todo generateEmptyTodo() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}