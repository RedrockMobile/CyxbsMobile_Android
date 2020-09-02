package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * Created by anriku on 2018/8/21.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0010\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u0006H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0018H\u0016J\u000e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0016J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0016\u001a\u00020\u0006H\u0002J\u0018\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0016\u001a\u00020\u0006H\u0003J\u0018\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u0018H\u0016J\u0018\u0010#\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0016\u001a\u00020\u0006H\u0016R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/ScheduleDetailViewAdapter;", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;", "mDialog", "Landroid/app/Dialog;", "mSchedules", "", "Lcom/mredrock/cyxbs/course/network/Course;", "(Landroid/app/Dialog;Ljava/util/List;)V", "courseOfDay", "", "dayOfWeek", "mCourseApiService", "Lcom/mredrock/cyxbs/course/network/CourseApiService;", "getMCourseApiService", "()Lcom/mredrock/cyxbs/course/network/CourseApiService;", "mCourseApiService$delegate", "Lkotlin/Lazy;", "addDotsView", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$DotsView;", "container", "Landroid/view/ViewGroup;", "createAffairWeekStr", "itemViewInfo", "getAffairDetailLayout", "", "getCourseDetailLayout", "getSchedules", "setAffairContent", "", "itemView", "Landroid/view/View;", "setCourseContent", "setCurrentFocusDot", "dotsView", "position", "setScheduleContent", "module_course_debug"})
public final class ScheduleDetailViewAdapter implements com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter {
    private final java.util.List<java.lang.String> dayOfWeek = null;
    private final java.util.List<java.lang.String> courseOfDay = null;
    private final kotlin.Lazy mCourseApiService$delegate = null;
    private final android.app.Dialog mDialog = null;
    private final java.util.List<com.mredrock.cyxbs.course.network.Course> mSchedules = null;
    
    private final com.mredrock.cyxbs.course.network.CourseApiService getMCourseApiService() {
        return null;
    }
    
    @java.lang.Override()
    public int getCourseDetailLayout() {
        return 0;
    }
    
    @java.lang.Override()
    public int getAffairDetailLayout() {
        return 0;
    }
    
    @java.lang.Override()
    public void setScheduleContent(@org.jetbrains.annotations.NotNull()
    android.view.View itemView, @org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.network.Course itemViewInfo) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView addDotsView(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup container) {
        return null;
    }
    
    @java.lang.Override()
    public void setCurrentFocusDot(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView dotsView, int position) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.util.List<com.mredrock.cyxbs.course.network.Course> getSchedules() {
        return null;
    }
    
    /**
     * 设置点击课表中事务后弹出的dialog内容
     * @param itemView dialog 的content View
     * @param itemViewInfo 点击的事务信息
     */
    private final void setAffairContent(android.view.View itemView, com.mredrock.cyxbs.course.network.Course itemViewInfo) {
    }
    
    /**
     * 用来构造事务
     */
    private final java.lang.String createAffairWeekStr(com.mredrock.cyxbs.course.network.Course itemViewInfo) {
        return null;
    }
    
    /**
     * 设置点击课表中课程后弹出的dialog内容
     * @param itemView dialog 的content View
     * @param itemViewInfo 点击的课程信息
     */
    @android.annotation.SuppressLint(value = {"SetTextI18n"})
    private final void setCourseContent(android.view.View itemView, com.mredrock.cyxbs.course.network.Course itemViewInfo) {
    }
    
    public ScheduleDetailViewAdapter(@org.jetbrains.annotations.NotNull()
    android.app.Dialog mDialog, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> mSchedules) {
        super();
    }
}