package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * @param mActivity [Context]
 * @param mNowWeek 表示当前的周数
 * @param mSchedules 表示显示的数据
 * @param mIsBanTouchView 是否禁用在空白处的点击
 *
 * Created by anriku on 2018/8/14.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 C2\u00020\u0001:\u0002CDB+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\b\u0010)\u001a\u00020*H\u0002J\u000f\u0010+\u001a\u0004\u0018\u00010\u0005H\u0016\u00a2\u0006\u0002\u0010,J \u0010-\u001a\u00020\u000f2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u00052\u0006\u00100\u001a\u000201H\u0016J\u001a\u00102\u001a\u0004\u0018\u0001032\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u0005H\u0016J \u00104\u001a\u00020*2\u0006\u00105\u001a\u00020\u000f2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u0005H\u0016J\b\u00106\u001a\u00020*H\u0016J \u00107\u001a\n\u0012\u0004\u0012\u00020*\u0018\u0001082\u0006\u00105\u001a\u00020\u000f2\u0006\u00109\u001a\u00020:H\u0016J \u0010;\u001a\u00020*2\u0006\u0010<\u001a\u00020\b2\u0006\u0010=\u001a\u00020\u00052\u0006\u0010>\u001a\u00020\u0005H\u0002J\u001e\u0010?\u001a\u00020*2\u0006\u0010@\u001a\u00020\u000f2\f\u0010A\u001a\b\u0012\u0004\u0012\u00020\b0$H\u0002J\b\u0010B\u001a\u00020*H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0012\u001a\u00020\u00138BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0014\u0010\u0015R\u001b\u0010\u0018\u001a\u00020\u00138BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u0017\u001a\u0004\b\u0019\u0010\u0015R\u001b\u0010\u001b\u001a\u00020\u001c8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u0017\u001a\u0004\b\u001d\u0010\u001eR\u000e\u0010 \u001a\u00020!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\"\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\b\u0018\u00010$0#0#X\u0082.\u00a2\u0006\u0004\n\u0002\u0010%R\u000e\u0010&\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006E"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/ScheduleViewAdapter;", "Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;", "mActivity", "Landroid/app/Activity;", "mNowWeek", "", "mSchedules", "", "Lcom/mredrock/cyxbs/course/network/Course;", "mIsBanTouchView", "", "(Landroid/app/Activity;ILjava/util/List;Z)V", "mAffairBackground", "Lcom/mredrock/cyxbs/course/component/AffairBackgroundView;", "mBackground", "Landroid/view/View;", "mBottom", "Landroid/widget/TextView;", "mCoursesColors", "", "getMCoursesColors", "()[I", "mCoursesColors$delegate", "Lkotlin/Lazy;", "mCoursesTextColors", "getMCoursesTextColors", "mCoursesTextColors$delegate", "mDialogHelper", "Lcom/mredrock/cyxbs/course/ui/ScheduleDetailBottomSheetDialogHelper;", "getMDialogHelper", "()Lcom/mredrock/cyxbs/course/ui/ScheduleDetailBottomSheetDialogHelper;", "mDialogHelper$delegate", "mInflater", "Landroid/view/LayoutInflater;", "mSchedulesArray", "", "", "[[Ljava/util/List;", "mShowModel", "mTag", "mTop", "addCourse", "", "getHighLightPosition", "()Ljava/lang/Integer;", "getItemView", "row", "column", "container", "Landroid/view/ViewGroup;", "getItemViewInfo", "Lcom/mredrock/cyxbs/course/component/ScheduleView$ScheduleItem;", "initItemView", "view", "notifyDataChange", "onTouchViewClick", "Lkotlin/Function0;", "position", "Lcom/mredrock/cyxbs/course/component/ScheduleView$TouchPositionData;", "setItemView", "course", "index", "itemCount", "setItemViewOnclickListener", "itemView", "schedules", "sortCourse", "Companion", "CourseComparator", "module_course_debug"})
public final class ScheduleViewAdapter extends com.mredrock.cyxbs.course.component.ScheduleView.Adapter {
    private android.view.LayoutInflater mInflater;
    private final boolean mShowModel = false;
    private java.util.List<com.mredrock.cyxbs.course.network.Course>[][] mSchedulesArray;
    private final kotlin.Lazy mCoursesColors$delegate = null;
    private final kotlin.Lazy mCoursesTextColors$delegate = null;
    private final kotlin.Lazy mDialogHelper$delegate = null;
    private android.widget.TextView mTop;
    private android.widget.TextView mBottom;
    private android.view.View mBackground;
    private android.view.View mTag;
    private com.mredrock.cyxbs.course.component.AffairBackgroundView mAffairBackground;
    private final android.app.Activity mActivity = null;
    private final int mNowWeek = 0;
    private final java.util.List<com.mredrock.cyxbs.course.network.Course> mSchedules = null;
    private final boolean mIsBanTouchView = false;
    private static final java.lang.String TAG = "ScheduleViewAdapter";
    private static final int NOT_LONG_COURSE = -1;
    public static final com.mredrock.cyxbs.course.adapters.ScheduleViewAdapter.Companion Companion = null;
    
    private final int[] getMCoursesColors() {
        return null;
    }
    
    private final int[] getMCoursesTextColors() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.ui.ScheduleDetailBottomSheetDialogHelper getMDialogHelper() {
        return null;
    }
    
    @java.lang.Override()
    public void notifyDataChange() {
    }
    
    /**
     * 这个方法用于进行课程的添加
     */
    @kotlin.Suppress(names = {"RemoveExplicitTypeArguments"})
    private final void addCourse() {
    }
    
    /**
     * 这个方法用于对课表进行排序
     */
    private final void sortCourse() {
    }
    
    /**
     * 如果[mIsBanTouchView]为true禁止mTouchView；反之就返回添加事务的事件。
     */
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public kotlin.jvm.functions.Function0<kotlin.Unit> onTouchViewClick(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.component.ScheduleView.TouchPositionData position) {
        return null;
    }
    
    /**
     * 在ScheduleView中通过getItemViewInfo方法获取当前行列有schedule信息后，才会调用此方法
     * @param row 行
     * @param column 列
     * @param container [ScheduleView]
     * @return 添加的View
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.View getItemView(int row, int column, @org.jetbrains.annotations.NotNull()
    android.view.ViewGroup container) {
        return null;
    }
    
    @java.lang.Override()
    public void initItemView(@org.jetbrains.annotations.NotNull()
    android.view.View view, int row, int column) {
    }
    
    /**
     * 此方法用于对即将添加的ItemView进行数据设置
     *
     * @param course Course相关信息
     * @param index 表示取那个颜色
     * @param itemCount 表示该位置Course的数量
     */
    private final void setItemView(com.mredrock.cyxbs.course.network.Course course, int index, int itemCount) {
    }
    
    /**
     * 获取当前课程位置上的高度信息
     * @param
     */
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public com.mredrock.cyxbs.course.component.ScheduleView.ScheduleItem getItemViewInfo(int row, int column) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Integer getHighLightPosition() {
        return null;
    }
    
    /**
     * 此方法用于自定义ItemView点击事件
     * @param itemView 显示课程的Item
     * @param schedules 课程信息
     */
    private final void setItemViewOnclickListener(android.view.View itemView, java.util.List<com.mredrock.cyxbs.course.network.Course> schedules) {
    }
    
    public ScheduleViewAdapter(@org.jetbrains.annotations.NotNull()
    android.app.Activity mActivity, int mNowWeek, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends com.mredrock.cyxbs.course.network.Course> mSchedules, boolean mIsBanTouchView) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0007\u001a\u00020\u0002H\u0016\u00a8\u0006\b"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/ScheduleViewAdapter$CourseComparator;", "Ljava/util/Comparator;", "Lcom/mredrock/cyxbs/course/network/Course;", "()V", "compare", "", "behind", "front", "module_course_debug"})
    public static final class CourseComparator implements java.util.Comparator<com.mredrock.cyxbs.course.network.Course> {
        public static final com.mredrock.cyxbs.course.adapters.ScheduleViewAdapter.CourseComparator INSTANCE = null;
        
        /**
         * 排序的方式是课程排在事务的前面，课程中的排序是时间长的排在时间短的前面。
         * @param behind 现在排在后面的Course
         * @param front 现在排在前面的Course
         */
        @java.lang.Override()
        public int compare(@org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.network.Course behind, @org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.network.Course front) {
            return 0;
        }
        
        private CourseComparator() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/ScheduleViewAdapter$Companion;", "", "()V", "NOT_LONG_COURSE", "", "TAG", "", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}