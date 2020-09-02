package com.mredrock.cyxbs.course.utils;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 2, d1 = {"\u0000>\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a\u0016\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005\u001a8\u0010\u0006\u001a\u0016\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007j\n\u0012\u0004\u0012\u00020\b\u0018\u0001`\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e\u001a\u000e\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0003\u001a8\u0010\u0011\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\u00130\u00122\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\u0003\u001a\u000e\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0003\u001a$\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\u0003\u001a\u001e\u0010\u0017\u001a\u0004\u0018\u00010\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\u0003\u00a8\u0006\u0018"}, d2 = {"createCornerBackground", "Landroid/graphics/drawable/Drawable;", "rgb", "", "corner", "", "getCourseByCalendar", "Ljava/util/ArrayList;", "Lcom/mredrock/cyxbs/course/network/Course;", "Lkotlin/collections/ArrayList;", "courses", "", "nowWeek", "calendar", "Ljava/util/Calendar;", "getEndCalendarByNum", "hash_lesson", "getNowCourse", "Lkotlin/Pair;", "", "wholeCourses", "getStartCalendarByNum", "getTodayCourse", "getTomorrowCourse", "module_course_debug"})
public final class CourseUtilKt {
    
    /**
     * 获取当前的课程用于显示在课表的头部
     * @param courses 当天的课程数据
     * @param nowWeek 现在是第几周
     * @return 返回两个值，第一个是课表，第二个是是否第二天的课表
     */
    @org.jetbrains.annotations.NotNull()
    public static final kotlin.Pair<com.mredrock.cyxbs.course.network.Course, java.lang.String> getNowCourse(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> courses, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> wholeCourses, int nowWeek) {
        return null;
    }
    
    /**
     * 获取明天的课程用于显示在课表的头部
     * @param courses 整个课表数据
     * @param nowWeek 现在是第几周
     */
    @org.jetbrains.annotations.Nullable()
    public static final com.mredrock.cyxbs.course.network.Course getTomorrowCourse(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> courses, int nowWeek) {
        return null;
    }
    
    /**
     * 获得今天得课程list信息
     */
    @org.jetbrains.annotations.Nullable()
    public static final java.util.List<com.mredrock.cyxbs.course.network.Course> getTodayCourse(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> courses, int nowWeek) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public static final java.util.ArrayList<com.mredrock.cyxbs.course.network.Course> getCourseByCalendar(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> courses, int nowWeek, @org.jetbrains.annotations.NotNull()
    java.util.Calendar calendar) {
        return null;
    }
    
    /**
     * hash_lesson == 0 第1节 返回8:00
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.util.Calendar getStartCalendarByNum(int hash_lesson) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.Calendar getEndCalendarByNum(int hash_lesson) {
        return null;
    }
    
    /**
     * 这个方法来制造课表item的圆角背景
     * @param rgb 背景颜色
     * 里面的圆角的参数是写在资源文件里的
     */
    @org.jetbrains.annotations.NotNull()
    public static final android.graphics.drawable.Drawable createCornerBackground(int rgb, float corner) {
        return null;
    }
}