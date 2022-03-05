package com.cyxbsmobile_single.module_todo.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 2, d1 = {"\u0000b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\t\n\u0000\u001a\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002\u001a\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n\u001a\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u001a\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u0010H\u0002\u001a\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\nH\u0002\u001a\u000e\u0010\u0015\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\n\u001a,\u0010\u0016\u001a(\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00170\u0017j\u0018\u0012\u0014\u0012\u0012\u0012\u0004\u0012\u00020\u000e0\u0017j\b\u0012\u0004\u0012\u00020\u000e`\u0018`\u0018\u001a\u0016\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e\u001a\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"\u001a\u000e\u0010#\u001a\u00020 2\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010$\u001a\u00020\u00022\u0006\u0010%\u001a\u00020\n\u001a\u000e\u0010$\u001a\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010\'\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010(\u001a\u00020\n2\u0006\u0010)\u001a\u00020\u0002\u001a\u0006\u0010*\u001a\u00020\u001a\u001a6\u0010+\u001a\u00020\u001a2\u0006\u0010,\u001a\u00020\u001e2\b\b\u0002\u0010-\u001a\u00020\n2\b\b\u0002\u0010.\u001a\u00020\n2\b\b\u0002\u0010/\u001a\u00020\n2\b\b\u0002\u00100\u001a\u00020\n\u001a\u0014\u00101\u001a\u00020\u001a2\f\u00102\u001a\b\u0012\u0004\u0012\u0002030\u0001\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\u00a8\u00064"}, d2 = {"weekStringList", "", "", "getWeekStringList", "()Ljava/util/List;", "formatDateWithTryCatch", "Ljava/util/Date;", "format", "raw", "getColor", "", "id", "getNextFourYears", "getNextNotifyDay", "Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;", "remindMode", "Lcom/cyxbsmobile_single/module_todo/model/bean/RemindMode;", "getNotifyDayCandler", "Ljava/util/Calendar;", "getRealWeek", "rawWeekNum", "getString", "getYearDateSting", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "hideKeyboard", "", "context", "Landroid/content/Context;", "v", "Landroid/view/View;", "isOutOfTime", "", "todo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "needTodayDone", "numToString", "num", "remindMode2RemindList", "repeatMode2RemindTime", "repeatString2Num", "repeatString", "resetRepeatStatus", "setMargin", "view", "top", "left", "right", "bottom", "setRepeatTodoList", "idList", "", "module_todo_release"})
public final class UtilKt {
    
    /**
     * @date 2021-08-14
     * @author Sca RayleighZ
     * TODO模块一些简单的通用工具类
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> weekStringList = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<java.lang.String> getWeekStringList() {
        return null;
    }
    
    public static final int repeatString2Num(@org.jetbrains.annotations.NotNull()
    java.lang.String repeatString) {
        return 0;
    }
    
    /**
     * 根据remindMode生成提醒日期
     * 仅重复不提醒       -> 返回下一次提醒当天的凌晨00:00
     * 重复+提醒 | 仅提醒  -> 返回下一次提醒的时间
     * 不重复不提醒       -> 返回""
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String repeatMode2RemindTime(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final com.cyxbsmobile_single.module_todo.model.bean.DateBeen getNextNotifyDay(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.ArrayList<java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.DateBeen>> getYearDateSting() {
        return null;
    }
    
    public static final boolean needTodayDone(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode) {
        return false;
    }
    
    public static final void setRepeatTodoList(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> idList) {
    }
    
    public static final void resetRepeatStatus() {
    }
    
    public static final void setMargin(@org.jetbrains.annotations.NotNull()
    android.view.View view, int top, int left, int right, int bottom) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<java.lang.String> remindMode2RemindList(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode) {
        return null;
    }
    
    public static final void hideKeyboard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View v) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String numToString(int num) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String numToString(@org.jetbrains.annotations.NotNull()
    java.lang.String num) {
        return null;
    }
    
    private static final java.util.Calendar getNotifyDayCandler(com.cyxbsmobile_single.module_todo.model.bean.RemindMode remindMode) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<java.lang.String> getNextFourYears() {
        return null;
    }
    
    private static final int getRealWeek(int rawWeekNum) {
        return 0;
    }
    
    public static final boolean isOutOfTime(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String getString(int id) {
        return null;
    }
    
    public static final int getColor(int id) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.Date formatDateWithTryCatch(@org.jetbrains.annotations.NotNull()
    java.lang.String format, @org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
        return null;
    }
}