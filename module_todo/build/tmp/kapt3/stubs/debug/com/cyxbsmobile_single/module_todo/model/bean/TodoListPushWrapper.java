package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:19
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 !2\u00020\u0001:\u0001!B/\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u00a2\u0006\u0002\u0010\nJ\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0018\u001a\u00020\bH\u00c6\u0003J7\u0010\u0019\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u00d6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001R\u001e\u0010\t\u001a\u00020\b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u0007\u001a\u00020\b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\f\"\u0004\b\u0010\u0010\u000eR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\""}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListPushWrapper;", "Ljava/io/Serializable;", "todoList", "", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "syncTime", "", "force", "", "firsPush", "(Ljava/util/List;JII)V", "getFirsPush", "()I", "setFirsPush", "(I)V", "getForce", "setForce", "getSyncTime", "()J", "getTodoList", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "hashCode", "toString", "", "Companion", "module_todo_debug"})
public final class TodoListPushWrapper implements java.io.Serializable {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "data")
    private final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList = null;
    @com.google.gson.annotations.SerializedName(value = "sync_time")
    private final long syncTime = 0L;
    @com.google.gson.annotations.SerializedName(value = "force")
    private int force;
    @com.google.gson.annotations.SerializedName(value = "first_push")
    private int firsPush;
    public static final int IS_FORCE = 1;
    public static final int NONE_FORCE = 0;
    public static final com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoList() {
        return null;
    }
    
    public final long getSyncTime() {
        return 0L;
    }
    
    public final int getForce() {
        return 0;
    }
    
    public final void setForce(int p0) {
    }
    
    public final int getFirsPush() {
        return 0;
    }
    
    public final void setFirsPush(int p0) {
    }
    
    public TodoListPushWrapper(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList, long syncTime, int force, int firsPush) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:19
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList, long syncTime, int force, int firsPush) {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:19
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:19
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Author: RayleighZ
     * Time: 2021-08-29 0:19
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListPushWrapper$Companion;", "", "()V", "IS_FORCE", "", "NONE_FORCE", "module_todo_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}