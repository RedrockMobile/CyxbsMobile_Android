package com.mredrock.cyxbs.course.event;

import java.lang.System;

/**
 * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
 *
 * @param isFold true: 折叠
 * false:不折叠
 *
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0006\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\u0007\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\b\u001a\u00020\u00032\b\u0010\t\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\n\u001a\u00020\u000bH\u00d6\u0001J\t\u0010\f\u001a\u00020\rH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0005\u00a8\u0006\u000e"}, d2 = {"Lcom/mredrock/cyxbs/course/event/TabIsFoldEvent;", "", "isFold", "", "(Z)V", "()Z", "component1", "copy", "equals", "other", "hashCode", "", "toString", "", "module_course_debug"})
public final class TabIsFoldEvent {
    private final boolean isFold = false;
    
    public final boolean isFold() {
        return false;
    }
    
    public TabIsFoldEvent(boolean isFold) {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    /**
     * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
     *
     * @param isFold true: 折叠
     * false:不折叠
     *
     * Created by anriku on 2018/8/18.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.mredrock.cyxbs.course.event.TabIsFoldEvent copy(boolean isFold) {
        return null;
    }
    
    /**
     * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
     *
     * @param isFold true: 折叠
     * false:不折叠
     *
     * Created by anriku on 2018/8/18.
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
     *
     * @param isFold true: 折叠
     * false:不折叠
     *
     * Created by anriku on 2018/8/18.
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
     *
     * @param isFold true: 折叠
     * false:不折叠
     *
     * Created by anriku on 2018/8/18.
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}