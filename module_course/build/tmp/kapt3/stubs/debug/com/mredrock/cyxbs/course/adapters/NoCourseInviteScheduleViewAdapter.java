package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * Created by anriku on 2018/10/6.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0018\u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u00a2\u0006\u0002\u0010\fJ\u0010\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020\u0005H\u0002J\b\u0010(\u001a\u00020)H\u0003J\u000f\u0010*\u001a\u0004\u0018\u00010\u0005H\u0016\u00a2\u0006\u0002\u0010+J \u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u00052\u0006\u00100\u001a\u000201H\u0016J\u001a\u00102\u001a\u0004\u0018\u0001032\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u0005H\u0016J\u0018\u00104\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u0005H\u0002R$\u0010\r\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u000f0\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014R$\u0010\u0017\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u000f0\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0010R \u0010\u0006\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0018\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u0016\u001a\u0004\b\u0019\u0010\u0014R\u001b\u0010\u001b\u001a\u00020\u001c8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u0016\u001a\u0004\b\u001d\u0010\u001eR\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010 \u001a\u00020!8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b$\u0010\u0016\u001a\u0004\b\"\u0010#R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00065"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/NoCourseInviteScheduleViewAdapter;", "Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;", "mContext", "Landroid/content/Context;", "mNowWeek", "", "mCoursesMap", "", "", "Lcom/mredrock/cyxbs/course/network/Course;", "mNameList", "", "(Landroid/content/Context;ILjava/util/Map;Ljava/util/List;)V", "mCommonNoCoursesNames", "", "", "[[Ljava/util/List;", "mCoursesColors", "", "getMCoursesColors", "()[I", "mCoursesColors$delegate", "Lkotlin/Lazy;", "mCoursesIndex", "mCoursesTextColors", "getMCoursesTextColors", "mCoursesTextColors$delegate", "mLayoutInflater", "Landroid/view/LayoutInflater;", "getMLayoutInflater", "()Landroid/view/LayoutInflater;", "mLayoutInflater$delegate", "mNoCourseInviteDetailDialogHelper", "Lcom/mredrock/cyxbs/course/ui/NoCourseInviteDetailBottomSheetDialogHelper;", "getMNoCourseInviteDetailDialogHelper", "()Lcom/mredrock/cyxbs/course/ui/NoCourseInviteDetailBottomSheetDialogHelper;", "mNoCourseInviteDetailDialogHelper$delegate", "createBackground", "Landroid/graphics/drawable/Drawable;", "rgb", "getCommonNoCoursesNames", "", "getHighLightPosition", "()Ljava/lang/Integer;", "getItemView", "Landroid/view/View;", "row", "column", "container", "Landroid/view/ViewGroup;", "getItemViewInfo", "Lcom/mredrock/cyxbs/course/component/ScheduleView$ScheduleItem;", "getNoCourseLength", "module_course_debug"})
public final class NoCourseInviteScheduleViewAdapter extends com.mredrock.cyxbs.course.component.ScheduleView.Adapter {
    private final kotlin.Lazy mCoursesColors$delegate = null;
    private final kotlin.Lazy mCoursesTextColors$delegate = null;
    private final java.util.List<java.lang.Integer>[][] mCoursesIndex = null;
    private final java.util.List<java.lang.String>[][] mCommonNoCoursesNames = null;
    private final kotlin.Lazy mNoCourseInviteDetailDialogHelper$delegate = null;
    private final kotlin.Lazy mLayoutInflater$delegate = null;
    private final android.content.Context mContext = null;
    private final int mNowWeek = 0;
    private final java.util.Map<java.lang.Integer, java.util.List<com.mredrock.cyxbs.course.network.Course>> mCoursesMap = null;
    private final java.util.List<java.lang.String> mNameList = null;
    
    private final int[] getMCoursesColors() {
        return null;
    }
    
    private final int[] getMCoursesTextColors() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.ui.NoCourseInviteDetailBottomSheetDialogHelper getMNoCourseInviteDetailDialogHelper() {
        return null;
    }
    
    private final android.view.LayoutInflater getMLayoutInflater() {
        return null;
    }
    
    /**
     * 此方法用于对[mCoursesMap]进行处理。来获取[mCommonNoCoursesNames]。
     */
    @android.annotation.SuppressLint(value = {"CI_ByteDanceKotlinRules_List_Contains_Not_Allow"})
    private final void getCommonNoCoursesNames() {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.View getItemView(int row, int column, @org.jetbrains.annotations.NotNull()
    android.view.ViewGroup container) {
        return null;
    }
    
    /**
     * 这个方法来制造课表item的圆角背景
     * @param rgb 背景颜色
     * 里面的圆角的参数是写在资源文件里的
     */
    private final android.graphics.drawable.Drawable createBackground(int rgb) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public com.mredrock.cyxbs.course.component.ScheduleView.ScheduleItem getItemViewInfo(int row, int column) {
        return null;
    }
    
    private final int getNoCourseLength(int row, int column) {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Integer getHighLightPosition() {
        return null;
    }
    
    public NoCourseInviteScheduleViewAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context mContext, int mNowWeek, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.Integer, ? extends java.util.List<? extends com.mredrock.cyxbs.course.network.Course>> mCoursesMap, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> mNameList) {
        super();
    }
}