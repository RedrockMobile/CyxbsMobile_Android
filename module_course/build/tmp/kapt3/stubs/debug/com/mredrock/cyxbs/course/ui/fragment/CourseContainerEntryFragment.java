package com.mredrock.cyxbs.course.ui.fragment;

import java.lang.System;

/**
 * Created by anriku on 2018/8/16.
 */
@com.alibaba.android.arouter.facade.annotation.Route(path = "/course/entry")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u00d8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u00032\u00020\u0004:\u0001dB\u0005\u00a2\u0006\u0002\u0010\u0005J\u0010\u0010:\u001a\u00020\u000f2\u0006\u0010;\u001a\u00020<H\u0007J\u0010\u0010=\u001a\u00020\u000f2\u0006\u0010>\u001a\u00020?H\u0007J\u0010\u0010@\u001a\u00020\u000f2\u0006\u0010A\u001a\u00020BH\u0007J\u0010\u0010C\u001a\u00020\u000f2\u0006\u0010D\u001a\u00020EH\u0002J\b\u0010F\u001a\u00020\u000fH\u0002J\b\u0010G\u001a\u00020\u000fH\u0002J\u0010\u0010H\u001a\u00020\u000f2\u0006\u0010I\u001a\u00020JH\u0007J\b\u0010K\u001a\u00020\u000fH\u0002J\u0010\u0010L\u001a\u00020\u000f2\u0006\u0010M\u001a\u00020NH\u0007J&\u0010O\u001a\u0004\u0018\u00010\u000e2\u0006\u0010P\u001a\u00020Q2\b\u0010R\u001a\u0004\u0018\u00010S2\b\u0010T\u001a\u0004\u0018\u00010UH\u0016J\b\u0010V\u001a\u00020\u000fH\u0016J\b\u0010W\u001a\u00020\u000fH\u0016J\u0010\u0010X\u001a\u00020\u000f2\u0006\u0010D\u001a\u00020YH\u0016J\u001a\u0010Z\u001a\u00020\u000f2\u0006\u0010[\u001a\u00020\u000e2\b\u0010T\u001a\u0004\u0018\u00010UH\u0016J\u0010\u0010\\\u001a\u00020\u000f2\u0006\u0010D\u001a\u00020EH\u0002J\u0016\u0010]\u001a\u00020\u000f2\f\u0010^\u001a\b\u0012\u0004\u0012\u00020`0_H\u0007J\u0010\u0010a\u001a\u00020\u000f2\u0006\u0010b\u001a\u00020cH\u0007R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0010\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0017\u001a\u00020\u00188BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u0014\u001a\u0004\b\u0019\u0010\u001aR.\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001d2\u000e\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001d@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b\u001f\u0010 R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u0010#\u001a\u0004\u0018\u00010\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u000b@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b$\u0010%R\"\u0010&\u001a\u0004\u0018\u00010\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u000b@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b\'\u0010%R\u0016\u0010(\u001a\b\u0012\u0004\u0012\u00020\u000b0)X\u0082.\u00a2\u0006\u0004\n\u0002\u0010*R\u000e\u0010+\u001a\u00020,X\u0082.\u00a2\u0006\u0002\n\u0000R\"\u0010-\u001a\u0004\u0018\u00010\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u000b@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b.\u0010%R.\u0010/\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001d2\u000e\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001d@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b0\u0010 R\u0016\u00101\u001a\b\u0012\u0004\u0012\u00020\u000b0)X\u0082.\u00a2\u0006\u0004\n\u0002\u0010*R\u0014\u00102\u001a\u0002038TX\u0094\u0004\u00a2\u0006\u0006\u001a\u0004\b4\u00105R\u001a\u00106\u001a\b\u0012\u0004\u0012\u00020\u000207X\u0094\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00109\u00a8\u0006e"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment;", "Lcom/mredrock/cyxbs/common/ui/BaseViewModelFragment;", "Lcom/mredrock/cyxbs/course/viewmodels/CoursesViewModel;", "Lcom/mredrock/cyxbs/common/service/account/IUserStateService$StateListener;", "Lcom/mredrock/cyxbs/common/mark/EventBusLifecycleSubscriber;", "()V", "accountService", "Lcom/mredrock/cyxbs/common/service/account/IAccountService;", "courseState", "Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment$CourseState;", "directLoadCourse", "", "inflateListener", "Lkotlin/Function1;", "Landroid/view/View;", "", "inflateView", "getInflateView", "()Landroid/view/View;", "inflateView$delegate", "Lkotlin/Lazy;", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseFragmentCourseContainerBinding;", "mDialogHelper", "Lcom/mredrock/cyxbs/course/ui/ScheduleDetailBottomSheetDialogHelper;", "getMDialogHelper", "()Lcom/mredrock/cyxbs/course/ui/ScheduleDetailBottomSheetDialogHelper;", "mDialogHelper$delegate", "value", "Ljava/util/ArrayList;", "mNameList", "setMNameList", "(Ljava/util/ArrayList;)V", "mNoCourseInviteViewModel", "Lcom/mredrock/cyxbs/course/viewmodels/NoCourseInviteViewModel;", "mOthersTeaName", "setMOthersTeaName", "(Ljava/lang/String;)V", "mOthersTeaNum", "setMOthersTeaNum", "mRawWeeks", "", "[Ljava/lang/String;", "mScheduleAdapter", "Lcom/mredrock/cyxbs/course/adapters/ScheduleVPAdapter;", "mStuNum", "setMStuNum", "mStuNumList", "setMStuNumList", "mWeeks", "openStatistics", "", "getOpenStatistics", "()Z", "viewModelClass", "Ljava/lang/Class;", "getViewModelClass", "()Ljava/lang/Class;", "addAffairs", "addAffairEvent", "Lcom/mredrock/cyxbs/course/event/AddAffairEvent;", "addTheAffairsToTheCalendar", "affairFromInternetEvent", "Lcom/mredrock/cyxbs/course/event/AffairFromInternetEvent;", "deleteAffair", "deleteAffairEvent", "Lcom/mredrock/cyxbs/course/event/DeleteAffairEvent;", "headViewAlphaChange", "state", "", "initFragment", "initHead", "loadCoursePage", "loadCourse", "Lcom/mredrock/cyxbs/common/event/LoadCourse;", "loadViewPager", "modifyAffairs", "modifyAffairEvent", "Lcom/mredrock/cyxbs/course/event/ModifyAffairEvent;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onStart", "onStateChanged", "Lcom/mredrock/cyxbs/common/service/account/IUserStateService$UserState;", "onViewCreated", "view", "settingFollowBottomSheet", "showDialogFromWidget", "event", "Lcom/mredrock/cyxbs/common/event/WidgetCourseEvent;", "Lcom/mredrock/cyxbs/common/bean/WidgetCourse$DataBean;", "showModeChange", "e", "Lcom/mredrock/cyxbs/common/event/ShowModeChangeEvent;", "CourseState", "module_course_debug"})
public final class CourseContainerEntryFragment extends com.mredrock.cyxbs.common.ui.BaseViewModelFragment<com.mredrock.cyxbs.course.viewmodels.CoursesViewModel> implements com.mredrock.cyxbs.common.service.account.IUserStateService.StateListener, com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber {
    private com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState courseState = com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState.OrdinaryCourse;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Class<com.mredrock.cyxbs.course.viewmodels.CoursesViewModel> viewModelClass = null;
    private java.lang.String mOthersTeaNum;
    private java.lang.String mOthersTeaName;
    private java.lang.String mStuNum;
    private java.util.ArrayList<java.lang.String> mStuNumList;
    private java.util.ArrayList<java.lang.String> mNameList;
    private java.lang.String directLoadCourse;
    private com.mredrock.cyxbs.course.adapters.ScheduleVPAdapter mScheduleAdapter;
    private com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel mNoCourseInviteViewModel;
    private com.mredrock.cyxbs.course.databinding.CourseFragmentCourseContainerBinding mBinding;
    private java.lang.String[] mRawWeeks;
    private java.lang.String[] mWeeks;
    private final kotlin.Lazy mDialogHelper$delegate = null;
    private final com.mredrock.cyxbs.common.service.account.IAccountService accountService = null;
    private final kotlin.Lazy inflateView$delegate = null;
    private kotlin.jvm.functions.Function1<? super android.view.View, kotlin.Unit> inflateListener;
    private java.util.HashMap _$_findViewCache;
    
    @java.lang.Override()
    protected boolean getOpenStatistics() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected java.lang.Class<com.mredrock.cyxbs.course.viewmodels.CoursesViewModel> getViewModelClass() {
        return null;
    }
    
    private final void setMOthersTeaNum(java.lang.String value) {
    }
    
    private final void setMOthersTeaName(java.lang.String value) {
    }
    
    private final void setMStuNum(java.lang.String value) {
    }
    
    private final void setMStuNumList(java.util.ArrayList<java.lang.String> value) {
    }
    
    private final void setMNameList(java.util.ArrayList<java.lang.String> value) {
    }
    
    private final com.mredrock.cyxbs.course.ui.ScheduleDetailBottomSheetDialogHelper getMDialogHelper() {
        return null;
    }
    
    private final android.view.View getInflateView() {
        return null;
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
    
    @java.lang.Override()
    public void onDestroyView() {
    }
    
    /**
     * 对当前Fragment进行一系列初始化
     */
    private final void initFragment() {
    }
    
    private final void loadViewPager() {
    }
    
    /**
     * 初始化课表头部信息
     */
    private final void initHead() {
    }
    
    /**
     * 根据主页的fragment的滑动状态来设置当前头部显示效果
     * @param state 从0到1，1表示BottomSheet完全展开
     */
    private final void settingFollowBottomSheet(float state) {
    }
    
    private final void headViewAlphaChange(float state) {
    }
    
    @java.lang.Override()
    public void onStart() {
    }
    
    /**
     * 如果没有直接加载课表ViewPager子页，这个可以用来通知加载
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void loadCoursePage(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.common.event.LoadCourse loadCourse) {
    }
    
    /**
     * 这个方法用于小部件点击打开课表详情页面
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN, sticky = true)
    public final void showDialogFromWidget(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.common.event.WidgetCourseEvent<com.mredrock.cyxbs.common.bean.WidgetCourse.DataBean> event) {
    }
    
    /**
     * 这个方法用于删除事务之后重新获取进行网络请求获取数据
     *
     * @param deleteAffairEvent
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void deleteAffair(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.event.DeleteAffairEvent deleteAffairEvent) {
    }
    
    /**
     * 这个方法用于添加事务后重新进行网络请求获取数据
     *
     * @param addAffairEvent
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void addAffairs(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.event.AddAffairEvent addAffairEvent) {
    }
    
    /**
     * 这个方法用于接收设置课表上备忘项目显示模式的更新
     */
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void showModeChange(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.common.event.ShowModeChangeEvent e) {
    }
    
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void modifyAffairs(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.event.ModifyAffairEvent modifyAffairEvent) {
    }
    
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public final void addTheAffairsToTheCalendar(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.event.AffairFromInternetEvent affairFromInternetEvent) {
    }
    
    @java.lang.Override()
    public void onStateChanged(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.common.service.account.IUserStateService.UserState state) {
    }
    
    public CourseContainerEntryFragment() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment$CourseState;", "", "(Ljava/lang/String;I)V", "OrdinaryCourse", "TeacherCourse", "OtherCourse", "NoClassInvitationCourse", "module_course_debug"})
    public static enum CourseState {
        /*public static final*/ OrdinaryCourse /* = new OrdinaryCourse() */,
        /*public static final*/ TeacherCourse /* = new TeacherCourse() */,
        /*public static final*/ OtherCourse /* = new OtherCourse() */,
        /*public static final*/ NoClassInvitationCourse /* = new NoClassInvitationCourse() */;
        
        CourseState() {
        }
    }
}