package com.cyxbsmobile_single.module_todo.model.bean;

import java.lang.System;

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * 写成ArrayList是为了方便添加数据
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0086\b\u0018\u0000 (2\u00020\u0001:\u0001(B_\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0016\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u0007\u0012\u0016\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u0007\u0012\u0016\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u0007\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\u0019\u0010\u001d\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u0007H\u00c6\u0003J\u0019\u0010\u001e\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u0007H\u00c6\u0003J\u0019\u0010\u001f\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u0007H\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\u0006H\u00c6\u0003Jm\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0018\b\u0002\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u00072\u0018\b\u0002\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u00072\u0018\b\u0002\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u00072\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%H\u00d6\u0003J\t\u0010&\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0006H\u00d6\u0001R.\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR.\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u000fR \u0010\n\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001e\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R.\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0005j\b\u0012\u0004\u0012\u00020\u0003`\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\r\"\u0004\b\u001b\u0010\u000f\u00a8\u0006)"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;", "Ljava/io/Serializable;", "repeatMode", "", "date", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "week", "day", "notifyDateTime", "(ILjava/util/ArrayList;Ljava/util/ArrayList;Ljava/util/ArrayList;Ljava/lang/String;)V", "getDate", "()Ljava/util/ArrayList;", "setDate", "(Ljava/util/ArrayList;)V", "getDay", "setDay", "getNotifyDateTime", "()Ljava/lang/String;", "setNotifyDateTime", "(Ljava/lang/String;)V", "getRepeatMode", "()I", "setRepeatMode", "(I)V", "getWeek", "setWeek", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "toString", "Companion", "module_todo_debug"})
public final class RemindMode implements java.io.Serializable {
    @com.google.gson.annotations.SerializedName(value = "repeat_mode")
    private int repeatMode;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "date")
    private java.util.ArrayList<java.lang.String> date;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "week")
    private java.util.ArrayList<java.lang.Integer> week;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "day")
    private java.util.ArrayList<java.lang.Integer> day;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "notify_datetime")
    private java.lang.String notifyDateTime;
    public static final int NONE = 0;
    public static final int DAY = 1;
    public static final int WEEK = 2;
    public static final int MONTH = 3;
    public static final int YEAR = 4;
    public static final com.cyxbsmobile_single.module_todo.model.bean.RemindMode.Companion Companion = null;
    
    public final int getRepeatMode() {
        return 0;
    }
    
    public final void setRepeatMode(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDate() {
        return null;
    }
    
    public final void setDate(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.Integer> getWeek() {
        return null;
    }
    
    public final void setWeek(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.Integer> getDay() {
        return null;
    }
    
    public final void setDay(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotifyDateTime() {
        return null;
    }
    
    public final void setNotifyDateTime(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public RemindMode(int repeatMode, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> date, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> week, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> day, @org.jetbrains.annotations.Nullable()
    java.lang.String notifyDateTime) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.Integer> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.Integer> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 写成ArrayList是为了方便添加数据
     */
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.RemindMode copy(int repeatMode, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> date, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> week, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.Integer> day, @org.jetbrains.annotations.Nullable()
    java.lang.String notifyDateTime) {
        return null;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 写成ArrayList是为了方便添加数据
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 写成ArrayList是为了方便添加数据
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * @date 2021-08-18
     * @author Sca RayleighZ
     * 写成ArrayList是为了方便添加数据
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode$Companion;", "", "()V", "DAY", "", "MONTH", "NONE", "WEEK", "YEAR", "generateDefaultRemindMode", "Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;", "module_todo_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.cyxbsmobile_single.module_todo.model.bean.RemindMode generateDefaultRemindMode() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}