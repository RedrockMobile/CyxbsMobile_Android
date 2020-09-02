package com.mredrock.cyxbs.course.ui.fragment;

import java.lang.System;

/**
 * Created by anriku on 2018/8/14.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0007J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\u0010\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J&\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\b\u0010\"\u001a\u00020\u0013H\u0016J\u001a\u0010#\u001a\u00020\u00132\u0006\u0010$\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\u00020\r8TX\u0094\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/fragment/CourseFragment;", "Lcom/mredrock/cyxbs/common/ui/BaseFragment;", "Lcom/mredrock/cyxbs/common/mark/EventBusLifecycleSubscriber;", "()V", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseFragmentCourseBinding;", "mCoursePageViewModel", "Lcom/mredrock/cyxbs/course/viewmodels/CoursePageViewModel;", "mCoursesViewModel", "Lcom/mredrock/cyxbs/course/viewmodels/CoursesViewModel;", "mWeek", "", "openStatistics", "", "getOpenStatistics", "()Z", "scheduleView", "Lcom/mredrock/cyxbs/course/component/ScheduleView;", "dismissAddAffairEventView", "", "e", "Lcom/mredrock/cyxbs/course/event/DismissAddAffairViewEvent;", "initFragment", "onAttach", "context", "Landroid/content/Context;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onViewCreated", "view", "module_course_debug"})
public final class CourseFragment extends com.mredrock.cyxbs.common.ui.BaseFragment implements com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber {
    private int mWeek = 0;
    private com.mredrock.cyxbs.course.viewmodels.CoursesViewModel mCoursesViewModel;
    private com.mredrock.cyxbs.course.viewmodels.CoursePageViewModel mCoursePageViewModel;
    private com.mredrock.cyxbs.course.databinding.CourseFragmentCourseBinding mBinding;
    private com.mredrock.cyxbs.course.component.ScheduleView scheduleView;
    private java.util.HashMap _$_findViewCache;
    
    @java.lang.Override()
    protected boolean getOpenStatistics() {
        return false;
    }
    
    @java.lang.Override()
    public void onAttach(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initFragment() {
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    /**
     * 这个方法用于清理课表上加备忘的View
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void dismissAddAffairEventView(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent e) {
    }
    
    public CourseFragment() {
        super();
    }
}