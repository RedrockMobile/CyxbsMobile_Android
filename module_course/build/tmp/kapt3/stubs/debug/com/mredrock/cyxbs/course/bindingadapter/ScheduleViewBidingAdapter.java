package com.mredrock.cyxbs.course.bindingadapter;

import java.lang.System;

/**
 * Created by anriku on 2018/9/11.
 * 描述：这个类是用于设置[ScheduleView]的adapter，adapter用于设置[ScheduleView]的内部课程数据
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JB\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u001a\u0010\t\u001a\u0016\u0012\u0004\u0012\u00020\b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b\u0018\u00010\n2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000bH\u0007J0\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u000e\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0007\u00a8\u0006\u0013"}, d2 = {"Lcom/mredrock/cyxbs/course/bindingadapter/ScheduleViewBidingAdapter;", "", "()V", "setNoCourseInvite", "", "scheduleView", "Lcom/mredrock/cyxbs/course/component/ScheduleView;", "nowWeek", "", "studentsCourseMap", "", "", "Lcom/mredrock/cyxbs/course/network/Course;", "nameList", "", "setScheduleData", "schedules", "isBanTouchView", "", "module_course_debug"})
public final class ScheduleViewBidingAdapter {
    public static final com.mredrock.cyxbs.course.bindingadapter.ScheduleViewBidingAdapter INSTANCE = null;
    
    /**
     * @param scheduleView [ScheduleView]
     * @param schedules 要显示的内容。如果是用户课表就包含课表和备忘。如果是他人课表就没有备忘。
     * @param nowWeek 表示是第几周
     * @param isBanTouchView 是否禁用空白处点击添加备忘的功能。如果是用户课表就为false。如果是他人课表就为true。
     */
    @androidx.databinding.BindingAdapter(value = {"schedules", "nowWeek", "isBanTouchView"})
    public static final void setScheduleData(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.component.ScheduleView scheduleView, @org.jetbrains.annotations.Nullable()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> schedules, int nowWeek, boolean isBanTouchView) {
    }
    
    @androidx.databinding.BindingAdapter(value = {"nowWeek", "studentsCourseMap", "nameList"})
    public static final void setNoCourseInvite(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.component.ScheduleView scheduleView, int nowWeek, @org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.Integer, ? extends java.util.List<? extends com.mredrock.cyxbs.course.network.Course>> studentsCourseMap, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> nameList) {
    }
    
    private ScheduleViewBidingAdapter() {
        super();
    }
}