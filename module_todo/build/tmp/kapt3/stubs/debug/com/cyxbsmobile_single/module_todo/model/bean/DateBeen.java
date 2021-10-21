package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 这里使用序列化的原因是为了用JSON存储
 * 使得只用进行一次操作
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u001e\u0010\u0006\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\t\"\u0004\b\f\u0010\rR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\t\u00a8\u0006\u001c"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;", "Ljava/io/Serializable;", "month", "", "day", "week", "type", "(IIII)V", "getDay", "()I", "getMonth", "getType", "setType", "(I)V", "getWeek", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "hashCode", "toString", "", "Companion", "module_todo_debug"})
public final class DateBeen implements java.io.Serializable {
    @com.google.gson.annotations.SerializedName(value = "month")
    private final int month = 0;
    @com.google.gson.annotations.SerializedName(value = "day")
    private final int day = 0;
    @com.google.gson.annotations.SerializedName(value = "week")
    private final int week = 0;
    @com.google.gson.annotations.SerializedName(value = "type")
    private int type;
    public static final int EMPTY = 0;
    public static final int TODAY = 1;
    public static final int NORMAL = 2;
    public static final com.cyxbsmobile_single.module_todo.model.bean.DateBeen.Companion Companion = null;
    
    public final int getMonth() {
        return 0;
    }
    
    public final int getDay() {
        return 0;
    }
    
    public final int getWeek() {
        return 0;
    }
    
    public final int getType() {
        return 0;
    }
    
    public final void setType(int p0) {
    }
    
    public DateBeen(int month, int day, int week, int type) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 这里使用序列化的原因是为了用JSON存储
     * 使得只用进行一次操作
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.DateBeen copy(int month, int day, int week, int type) {
        return null;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 这里使用序列化的原因是为了用JSON存储
     * 使得只用进行一次操作
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 这里使用序列化的原因是为了用JSON存储
     * 使得只用进行一次操作
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 这里使用序列化的原因是为了用JSON存储
     * 使得只用进行一次操作
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen$Companion;", "", "()V", "EMPTY", "", "NORMAL", "TODAY", "module_todo_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}