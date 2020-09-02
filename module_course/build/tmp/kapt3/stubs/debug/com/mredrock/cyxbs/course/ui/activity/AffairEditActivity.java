package com.mredrock.cyxbs.course.ui.activity;

import java.lang.System;

/**
 * @author Jovines
 * @date 2019/12/21 20:35
 * description：具有三个状态，添加标题，添加内容，最后修改，
 *             分别对应了Helper里面的几个切换方法
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 (2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001(B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u001f\u001a\u00020 H\u0002J\b\u0010!\u001a\u00020 H\u0002J\b\u0010\"\u001a\u00020 H\u0016J\u0012\u0010#\u001a\u00020 2\b\u0010$\u001a\u0004\u0018\u00010%H\u0014J\b\u0010&\u001a\u00020 H\u0014J\b\u0010\'\u001a\u00020 H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00078VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0010\u001a\u0004\b\u0013\u0010\u0014R\u001b\u0010\u0016\u001a\u00020\u00178BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u0010\u001a\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00020\u001cX\u0094\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e\u00a8\u0006)"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/activity/AffairEditActivity;", "Lcom/mredrock/cyxbs/common/ui/BaseViewModelActivity;", "Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel;", "()V", "affairTransitionAnimHelper", "Lcom/mredrock/cyxbs/course/ui/AffairTransitionAnimHelper;", "isFragmentActivity", "", "()Z", "mBinding", "Lcom/mredrock/cyxbs/course/databinding/CourseActivityEditAffairBinding;", "mRemindSelectDialog", "Lcom/mredrock/cyxbs/course/component/RemindSelectDialog;", "getMRemindSelectDialog", "()Lcom/mredrock/cyxbs/course/component/RemindSelectDialog;", "mRemindSelectDialog$delegate", "Lkotlin/Lazy;", "mTimeSelectDialog", "Lcom/mredrock/cyxbs/course/component/TimeSelectDialog;", "getMTimeSelectDialog", "()Lcom/mredrock/cyxbs/course/component/TimeSelectDialog;", "mTimeSelectDialog$delegate", "mWeekSelectDialog", "Lcom/mredrock/cyxbs/course/component/WeekSelectDialog;", "getMWeekSelectDialog", "()Lcom/mredrock/cyxbs/course/component/WeekSelectDialog;", "mWeekSelectDialog$delegate", "viewModelClass", "Ljava/lang/Class;", "getViewModelClass", "()Ljava/lang/Class;", "forward", "", "initActivity", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "postAffair", "Companion", "module_course_debug"})
public final class AffairEditActivity extends com.mredrock.cyxbs.common.ui.BaseViewModelActivity<com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel> {
    private com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding mBinding;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Class<com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel> viewModelClass = null;
    private final kotlin.Lazy mWeekSelectDialog$delegate = null;
    private final kotlin.Lazy mTimeSelectDialog$delegate = null;
    private final kotlin.Lazy mRemindSelectDialog$delegate = null;
    private com.mredrock.cyxbs.course.ui.AffairTransitionAnimHelper affairTransitionAnimHelper;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String AFFAIR_INFO = "affairInfo";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WEEK_NUM = "weekString";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TIME_NUM = "timeNum";
    public static final com.mredrock.cyxbs.course.ui.activity.AffairEditActivity.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    @java.lang.Override()
    public boolean isFragmentActivity() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected java.lang.Class<com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel> getViewModelClass() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.component.WeekSelectDialog getMWeekSelectDialog() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.component.TimeSelectDialog getMTimeSelectDialog() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.component.RemindSelectDialog getMRemindSelectDialog() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initActivity() {
    }
    
    /**
     * 不断进行下一步，根据状态执行相应动画
     */
    private final void forward() {
    }
    
    /**
     * 不断进行后退，根据状态执行相应动画，或者直接退出activity
     */
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    /**
     * 最后一步上传事务
     */
    private final void postAffair() {
    }
    
    public AffairEditActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/activity/AffairEditActivity$Companion;", "", "()V", "AFFAIR_INFO", "", "TIME_NUM", "WEEK_NUM", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}