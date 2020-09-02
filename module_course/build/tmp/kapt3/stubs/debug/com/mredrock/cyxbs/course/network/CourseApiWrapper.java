package com.mredrock.cyxbs.course.network;

import java.lang.System;

/**
 * Created by anriku on 2018/8/14.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B#\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0005\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\n\"\u0004\b\u0012\u0010\f\u00a8\u0006\u0013"}, d2 = {"Lcom/mredrock/cyxbs/course/network/CourseApiWrapper;", "T", "Lcom/mredrock/cyxbs/course/network/RedRockApiWrapper;", "cachedTimestamp", "", "outOfDateTimestamp", "nowWeek", "", "(JJI)V", "getCachedTimestamp", "()J", "setCachedTimestamp", "(J)V", "getNowWeek", "()I", "setNowWeek", "(I)V", "getOutOfDateTimestamp", "setOutOfDateTimestamp", "module_course_debug"})
public final class CourseApiWrapper<T extends java.lang.Object> extends com.mredrock.cyxbs.course.network.RedRockApiWrapper<T> {
    private long cachedTimestamp;
    private long outOfDateTimestamp;
    private int nowWeek;
    
    public final long getCachedTimestamp() {
        return 0L;
    }
    
    public final void setCachedTimestamp(long p0) {
    }
    
    public final long getOutOfDateTimestamp() {
        return 0L;
    }
    
    public final void setOutOfDateTimestamp(long p0) {
    }
    
    public final int getNowWeek() {
        return 0;
    }
    
    public final void setNowWeek(int p0) {
    }
    
    public CourseApiWrapper(long cachedTimestamp, long outOfDateTimestamp, int nowWeek) {
        super(0, false, null, null, null, null);
    }
    
    public CourseApiWrapper() {
        super(0, false, null, null, null, null);
    }
}