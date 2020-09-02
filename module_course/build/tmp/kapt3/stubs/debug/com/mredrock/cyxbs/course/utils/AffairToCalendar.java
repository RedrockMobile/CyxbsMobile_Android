package com.mredrock.cyxbs.course.utils;

import java.lang.System;

/**
 * This class is used to add the [com.mredrock.cyxbs.course.network.Affair] to the phone's calendar.
 *
 * Created by anriku on 2018/10/11.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u0000 #2\u00020\u0001:\u0002\"#B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\b\u0010\u0014\u001a\u00020\u0015H\u0003J \u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u001a\u001a\u00020\u0013H\u0002J\u0006\u0010\u001b\u001a\u00020\u0015J\u0018\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u001e\u001a\u00020\u0017H\u0003J\u0016\u0010\u001f\u001a\u00020\u00152\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0002J\b\u0010!\u001a\u00020\u0015H\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\rX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\rX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000eR\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\rX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000eR\u000e\u0010\u0012\u001a\u00020\u0013X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/mredrock/cyxbs/course/utils/AffairToCalendar;", "", "mActivity", "Landroidx/appcompat/app/AppCompatActivity;", "mAffairs", "", "Lcom/mredrock/cyxbs/course/network/Course;", "(Landroidx/appcompat/app/AppCompatActivity;Ljava/util/List;)V", "mCalendarId", "", "mDeleteSelection", "", "mDeleteSelectionArgs", "", "[Ljava/lang/String;", "mGetIdSelection", "mGetIdSelectionArgs", "mProjection", "mProjectionId", "", "deleteAffairFromCalendar", "", "getAffairTimeMillis", "Lcom/mredrock/cyxbs/course/utils/AffairToCalendar$AffairTimeInMillis;", "week", "hashDay", "classX", "getPermissionToInsert", "insert", "affair", "affairTimeInMillis", "insertAffairToCalendar", "affairs", "queryCalendarId", "AffairTimeInMillis", "Companion", "module_course_debug"})
public final class AffairToCalendar {
    private long mCalendarId = 0L;
    private final java.lang.String[] mProjection = {"_id"};
    private final int mProjectionId = 0;
    private final java.lang.String mGetIdSelection = "((visible = ?) AND (calendar_access_level >= ?))";
    private final java.lang.String[] mGetIdSelectionArgs = {"1", "500"};
    private final java.lang.String mDeleteSelection = "((description = ?))";
    private final java.lang.String[] mDeleteSelectionArgs = {"\u6765\u81ea\u638c\u4e0a\u91cd\u90ae\u7684\u5907\u5fd8"};
    private final androidx.appcompat.app.AppCompatActivity mActivity = null;
    private final java.util.List<com.mredrock.cyxbs.course.network.Course> mAffairs = null;
    private static final java.lang.String TAG = "AffairToCalendar";
    private static final java.lang.String AFFAIR_DESCRIPTION = "\u6765\u81ea\u638c\u4e0a\u91cd\u90ae\u7684\u5907\u5fd8";
    public static final com.mredrock.cyxbs.course.utils.AffairToCalendar.Companion Companion = null;
    
    /**
     * Check the permission to insert.
     */
    public final void getPermissionToInsert() {
    }
    
    /**
     * This method is used to query the Calendar which can modify it events.
     */
    @android.annotation.SuppressLint(value = {"MissingPermission", "Recycle"})
    private final void queryCalendarId() {
    }
    
    /**
     * Traverse all the affairs execute the concrete add action.
     *
     * @param affairs All affairs going to be added.
     */
    private final void insertAffairToCalendar(java.util.List<? extends com.mredrock.cyxbs.course.network.Course> affairs) {
    }
    
    /**
     * Delete the events in the local calendar.
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void deleteAffairFromCalendar() {
    }
    
    /**
     * This method is used to insert the affairs to the local calendar.
     *
     * @param affair Single affair going to be add as a event to the calendar.
     * @param affairTimeInMillis A class record the event's start time and end time.
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void insert(com.mredrock.cyxbs.course.network.Course affair, com.mredrock.cyxbs.course.utils.AffairToCalendar.AffairTimeInMillis affairTimeInMillis) {
    }
    
    /**
     * Convert the affair's start time and end time to the [AffairTimeInMillis]
     *
     * @param week The week of the affair.
     * @param hashDay The range of hashDay is 0~6. 0 represent Monday, 6 represent Sunday.
     * @param classX The range of classX is 0~5. 0 represent 1,2 class, 5 represent 11,12 class.
     *
     * @return ...
     */
    private final com.mredrock.cyxbs.course.utils.AffairToCalendar.AffairTimeInMillis getAffairTimeMillis(int week, int hashDay, int classX) {
        return null;
    }
    
    public AffairToCalendar(@org.jetbrains.annotations.NotNull()
    androidx.appcompat.app.AppCompatActivity mActivity, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> mAffairs) {
        super();
    }
    
    /**
     * A class contain a affair's startTime and endTime as millis.
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2 = {"Lcom/mredrock/cyxbs/course/utils/AffairToCalendar$AffairTimeInMillis;", "", "startTimeInMillis", "", "endTimeInMillis", "(JJ)V", "getEndTimeInMillis", "()J", "getStartTimeInMillis", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "module_course_debug"})
    public static final class AffairTimeInMillis {
        private final long startTimeInMillis = 0L;
        private final long endTimeInMillis = 0L;
        
        public final long getStartTimeInMillis() {
            return 0L;
        }
        
        public final long getEndTimeInMillis() {
            return 0L;
        }
        
        public AffairTimeInMillis(long startTimeInMillis, long endTimeInMillis) {
            super();
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final long component2() {
            return 0L;
        }
        
        /**
         * A class contain a affair's startTime and endTime as millis.
         */
        @org.jetbrains.annotations.NotNull()
        public final com.mredrock.cyxbs.course.utils.AffairToCalendar.AffairTimeInMillis copy(long startTimeInMillis, long endTimeInMillis) {
            return null;
        }
        
        /**
         * A class contain a affair's startTime and endTime as millis.
         */
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        /**
         * A class contain a affair's startTime and endTime as millis.
         */
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        /**
         * A class contain a affair's startTime and endTime as millis.
         */
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object p0) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/course/utils/AffairToCalendar$Companion;", "", "()V", "AFFAIR_DESCRIPTION", "", "TAG", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}