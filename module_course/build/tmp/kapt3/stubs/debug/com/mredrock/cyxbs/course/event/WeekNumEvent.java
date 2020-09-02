package com.mredrock.cyxbs.course.event;

import java.lang.System;

/**
 * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
 * 的修改。
 *
 * @param weekString 当前是第几周。其中0表示整学期
 * @param isOthers 是否是别人课表，防止事件更新到主页面
 *
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000b\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\r\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0012"}, d2 = {"Lcom/mredrock/cyxbs/course/event/WeekNumEvent;", "", "weekString", "", "isOthers", "", "(Ljava/lang/String;Z)V", "()Z", "getWeekString", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "module_course_debug"})
public final class WeekNumEvent {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String weekString = null;
    private final boolean isOthers = false;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getWeekString() {
        return null;
    }
    
    public final boolean isOthers() {
        return false;
    }
    
    public WeekNumEvent(@org.jetbrains.annotations.NotNull()
    java.lang.String weekString, boolean isOthers) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    /**
     * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
     * 的修改。
     *
     * @param weekString 当前是第几周。其中0表示整学期
     * @param isOthers 是否是别人课表，防止事件更新到主页面
     *
     * Created by anriku on 2018/8/18.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.mredrock.cyxbs.course.event.WeekNumEvent copy(@org.jetbrains.annotations.NotNull()
    java.lang.String weekString, boolean isOthers) {
        return null;
    }
    
    /**
     * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
     * 的修改。
     *
     * @param weekString 当前是第几周。其中0表示整学期
     * @param isOthers 是否是别人课表，防止事件更新到主页面
     *
     * Created by anriku on 2018/8/18.
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
     * 的修改。
     *
     * @param weekString 当前是第几周。其中0表示整学期
     * @param isOthers 是否是别人课表，防止事件更新到主页面
     *
     * Created by anriku on 2018/8/18.
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
     * 的修改。
     *
     * @param weekString 当前是第几周。其中0表示整学期
     * @param isOthers 是否是别人课表，防止事件更新到主页面
     *
     * Created by anriku on 2018/8/18.
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}