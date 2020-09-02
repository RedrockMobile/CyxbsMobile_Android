package com.mredrock.cyxbs.course.viewmodels;

import java.lang.System;

/**
 * Created by anriku on 2018/9/9.
 */
@kotlin.Suppress(names = {"RemoveExplicitTypeArguments"})
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001OB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010B\u001a\u00020\u00192\u0006\u0010C\u001a\u00020\u0019H\u0002J\b\u0010D\u001a\u00020EH\u0002J\u000e\u0010F\u001a\u00020E2\u0006\u0010G\u001a\u00020HJ\u0016\u0010I\u001a\u00020E2\u0006\u00108\u001a\u00020\u00052\u0006\u0010\u0003\u001a\u00020\u0005J\b\u0010$\u001a\u00020EH\u0002J\u000e\u0010J\u001a\u00020E2\u0006\u0010K\u001a\u00020\u0019J\u000e\u0010L\u001a\u00020E2\u0006\u0010K\u001a\u00020\u0019J\u0016\u0010M\u001a\u00020E2\f\u0010N\u001a\b\u0012\u0004\u0012\u00020\u00190<H\u0002R \u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR!\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR#\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u000f\u001a\u0004\b\u0013\u0010\u0014R-\u0010\u0016\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00190\u00180\u00178FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001c\u0010\u000f\u001a\u0004\b\u001a\u0010\u001bR!\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00190\u00178FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u000f\u001a\u0004\b\u001e\u0010\u001bR\u001c\u0010 \u001a\u0004\u0018\u00010!X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001e\u0010\'\u001a\u00020\u00192\u0006\u0010&\u001a\u00020\u0019@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R!\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b,\u0010\u000f\u001a\u0004\b+\u0010\rR\u0017\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0007R\u001a\u0010/\u001a\u000200X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b1\u00102\"\u0004\b3\u00104R!\u00105\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b7\u0010\u000f\u001a\u0004\b6\u0010\rR \u00108\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b9\u0010\u0007\"\u0004\b:\u0010\tR&\u0010;\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050<0\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b=\u0010\u0007\"\u0004\b>\u0010\tR!\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\bA\u0010\u000f\u001a\u0004\b@\u0010\r\u00a8\u0006P"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel;", "Lcom/mredrock/cyxbs/common/viewmodel/BaseViewModel;", "()V", "content", "Landroidx/lifecycle/MutableLiveData;", "", "getContent", "()Landroidx/lifecycle/MutableLiveData;", "setContent", "(Landroidx/lifecycle/MutableLiveData;)V", "dayOfWeekArray", "", "getDayOfWeekArray", "()[Ljava/lang/String;", "dayOfWeekArray$delegate", "Lkotlin/Lazy;", "mCourseApiService", "Lcom/mredrock/cyxbs/course/network/CourseApiService;", "kotlin.jvm.PlatformType", "getMCourseApiService", "()Lcom/mredrock/cyxbs/course/network/CourseApiService;", "mCourseApiService$delegate", "mPostClassAndDays", "", "Lkotlin/Pair;", "", "getMPostClassAndDays", "()Ljava/util/List;", "mPostClassAndDays$delegate", "mPostWeeks", "getMPostWeeks", "mPostWeeks$delegate", "passedAffairInfo", "Lcom/mredrock/cyxbs/course/network/Course;", "getPassedAffairInfo", "()Lcom/mredrock/cyxbs/course/network/Course;", "setPassedAffairInfo", "(Lcom/mredrock/cyxbs/course/network/Course;)V", "<set-?>", "postRemind", "getPostRemind", "()I", "remindArray", "getRemindArray", "remindArray$delegate", "selectedRemindString", "getSelectedRemindString", "status", "Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel$Status;", "getStatus", "()Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel$Status;", "setStatus", "(Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel$Status;)V", "timeArray", "getTimeArray", "timeArray$delegate", "title", "getTitle", "setTitle", "titleCandidateList", "", "getTitleCandidateList", "setTitleCandidateList", "weekArray", "getWeekArray", "weekArray$delegate", "getRemindPosition", "remindValue", "getTitleCandidate", "", "initData", "editActivity", "Lcom/mredrock/cyxbs/course/ui/activity/AffairEditActivity;", "postOrModifyAffair", "setRemindSelectString", "position", "setThePostRemind", "setTimeSelected", "timeSelectPositions", "Status", "module_course_debug"})
public final class EditAffairViewModel extends com.mredrock.cyxbs.common.viewmodel.BaseViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> selectedRemindString = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy dayOfWeekArray$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy weekArray$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy timeArray$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy remindArray$delegate = null;
    private final kotlin.Lazy mCourseApiService$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private androidx.lifecycle.MutableLiveData<java.lang.String> title;
    @org.jetbrains.annotations.NotNull()
    private androidx.lifecycle.MutableLiveData<java.lang.String> content;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy mPostClassAndDays$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy mPostWeeks$delegate = null;
    private int postRemind = 0;
    @org.jetbrains.annotations.Nullable()
    private com.mredrock.cyxbs.course.network.Course passedAffairInfo;
    @org.jetbrains.annotations.NotNull()
    private com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel.Status status = com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel.Status.TitleStatus;
    @org.jetbrains.annotations.NotNull()
    private androidx.lifecycle.MutableLiveData<java.util.List<java.lang.String>> titleCandidateList;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getSelectedRemindString() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getDayOfWeekArray() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getWeekArray() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getTimeArray() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getRemindArray() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.network.CourseApiService getMCourseApiService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getTitle() {
        return null;
    }
    
    public final void setTitle(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.MutableLiveData<java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getContent() {
        return null;
    }
    
    public final void setContent(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.MutableLiveData<java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<kotlin.Pair<java.lang.Integer, java.lang.Integer>> getMPostClassAndDays() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Integer> getMPostWeeks() {
        return null;
    }
    
    public final int getPostRemind() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.mredrock.cyxbs.course.network.Course getPassedAffairInfo() {
        return null;
    }
    
    public final void setPassedAffairInfo(@org.jetbrains.annotations.Nullable()
    com.mredrock.cyxbs.course.network.Course p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel.Status getStatus() {
        return null;
    }
    
    public final void setStatus(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel.Status p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.util.List<java.lang.String>> getTitleCandidateList() {
        return null;
    }
    
    public final void setTitleCandidateList(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.MutableLiveData<java.util.List<java.lang.String>> p0) {
    }
    
    /**
     * 此方用于对[com.mredrock.cyxbs.course.ui.EditAffairActivity]进行周数选择、课程时间选择等相关内容的初始化。
     * 比如说从[com.mredrock.cyxbs.common.component.ScheduleView]中通过点击touchView、已有的事务进来要对其
     * 信息显示初始化。
     *
     * @param editActivity [com.mredrock.cyxbs.course.ui.EditAffairActivity]
     */
    public final void initData(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.ui.activity.AffairEditActivity editActivity) {
    }
    
    /**
     * 此方法用于提交新的事务或者是修改已有事务
     *
     * @param activity [com.mredrock.cyxbs.course.ui.EditAffairActivity]用于在完成后将其finish掉。
     * @param title 事务标题
     * @param content 事务内容
     */
    public final void postOrModifyAffair(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String content) {
    }
    
    /**
     * 获取事务标题候选
     */
    private final void getTitleCandidate() {
    }
    
    /**
     * 此方法用于根据提醒的的位置来设置对应的将要post的提醒值
     * @param position 对应的[remindArray]的位置
     */
    public final void setThePostRemind(int position) {
    }
    
    /**
     * 此方法用于对要被修改的事务的信息进行重现显示。
     */
    private final void setPassedAffairInfo() {
    }
    
    /**
     * 此方法用于根据具体的提醒时间获取在[remindArray]中的位置。
     */
    private final int getRemindPosition(int remindValue) {
        return 0;
    }
    
    /**
     * @param timeSelectPositions 对应的课程字符串的position
     */
    private final void setTimeSelected(java.util.List<java.lang.Integer> timeSelectPositions) {
    }
    
    /**
     * 此方法用于设定提醒字符串。
     * @param position 在[remindArray]中的索引位置
     */
    public final void setRemindSelectString(int position) {
    }
    
    public EditAffairViewModel() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/EditAffairViewModel$Status;", "", "(Ljava/lang/String;I)V", "TitleStatus", "ContentStatus", "AllDoneStatus", "module_course_debug"})
    public static enum Status {
        /*public static final*/ TitleStatus /* = new TitleStatus() */,
        /*public static final*/ ContentStatus /* = new ContentStatus() */,
        /*public static final*/ AllDoneStatus /* = new AllDoneStatus() */;
        
        Status() {
        }
    }
}