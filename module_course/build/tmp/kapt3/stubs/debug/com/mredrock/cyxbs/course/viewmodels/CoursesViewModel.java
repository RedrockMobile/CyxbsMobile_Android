package com.mredrock.cyxbs.course.viewmodels;

import java.lang.System;

/**
 * [CoursesViewModel]获取课表所遇到的坑有必要在这里做一下记录。
 * 请注意使用Room的时候，如果[io.reactivex.Observer]没有及时的被dispose
 * 掉。在以后数据库中的数据发生变化后。对应的[io.reactivex.Observable]会继续给其[io.reactivex.Observer]发射
 * 数据。为了使[io.reactivex.Observer]使用一次后就是被回收掉，可以使用[ExecuteOnceObserver].
 *
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0014\u0018\u0000 j2\u00020\u0001:\u0001jB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010R\u001a\u00020AJ\u0006\u0010S\u001a\u00020AJ\u001c\u0010T\u001a\u00020\u00162\u0012\u0010U\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050W0VH\u0003J\b\u0010X\u001a\u00020AH\u0002J\b\u0010Y\u001a\u00020AH\u0002J\b\u0010Z\u001a\u00020AH\u0002J\u0012\u0010[\u001a\u00020A2\b\b\u0002\u0010\\\u001a\u00020\u0016H\u0002J\"\u0010]\u001a\u00020A2\u0006\u0010^\u001a\u0002062\b\b\u0002\u0010_\u001a\u00020\u00162\b\b\u0002\u0010`\u001a\u00020\u0016J\b\u0010a\u001a\u00020AH\u0002J\b\u0010b\u001a\u00020\u0016H\u0002J\u0010\u0010c\u001a\u00020A2\u0006\u0010d\u001a\u00020\u0013H\u0002J\u0006\u0010e\u001a\u00020AJ\b\u0010f\u001a\u00020AH\u0002J\b\u0010g\u001a\u00020\u0016H\u0002J\u0010\u0010h\u001a\u00020A2\u0006\u0010i\u001a\u00020\u0013H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0014R\u001a\u0010\u0015\u001a\u00020\u0016X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0017\"\u0004\b\u0018\u0010\u0019R!\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00160\u00128FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u001a\u0010\u0014R!\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00130\u001e8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b \u0010\u001c\u001a\u0004\b\u001d\u0010\u001fR\u0017\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0014R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0014R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0014R\u001a\u0010$\u001a\u00020\u0016X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u0017\"\u0004\b%\u0010\u0019R\u001b\u0010&\u001a\u00020\'8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b*\u0010\u001c\u001a\u0004\b(\u0010)R\u001d\u0010+\u001a\u0004\u0018\u00010,8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b/\u0010\u001c\u001a\u0004\b-\u0010.R\u0016\u00100\u001a\b\u0012\u0004\u0012\u00020\u001601X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u00102R\u000e\u00103\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u00105\u001a\u000206X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R\u000e\u0010;\u001a\u000206X\u0082.\u00a2\u0006\u0002\n\u0000R \u0010<\u001a\b\u0012\u0004\u0012\u0002060\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b=\u0010\u0014\"\u0004\b>\u0010?R\u0017\u0010@\u001a\b\u0012\u0004\u0012\u00020A0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010\u001fR\u0019\u0010C\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bD\u0010\u0014R\u0017\u0010E\u001a\b\u0012\u0004\u0012\u0002060\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u0010\u0014R\u0017\u0010G\u001a\b\u0012\u0004\u0012\u0002060\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010\u0014R \u0010I\u001a\b\u0012\u0004\u0012\u00020\u00130\u001eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bJ\u0010\u001f\"\u0004\bK\u0010LR\u0017\u0010M\u001a\b\u0012\u0004\u0012\u00020\u00160\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010\u001fR\u001f\u0010O\u001a\u0010\u0012\f\u0012\n P*\u0004\u0018\u000106060\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u0010\u0014\u00a8\u0006k"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/CoursesViewModel;", "Lcom/mredrock/cyxbs/common/viewmodel/BaseViewModel;", "()V", "affairs", "Ljava/util/ArrayList;", "Lcom/mredrock/cyxbs/course/network/Course;", "allCoursesData", "", "getAllCoursesData", "()Ljava/util/List;", "courseState", "Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment$CourseState;", "getCourseState", "()Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment$CourseState;", "setCourseState", "(Lcom/mredrock/cyxbs/course/ui/fragment/CourseContainerEntryFragment$CourseState;)V", "courses", "isAffair", "Landroidx/databinding/ObservableField;", "", "()Landroidx/databinding/ObservableField;", "isFirstLoadItemAnim", "", "()Z", "setFirstLoadItemAnim", "(Z)V", "isGetOthers", "isGetOthers$delegate", "Lkotlin/Lazy;", "isShowBackPresentWeek", "Landroidx/lifecycle/MutableLiveData;", "()Landroidx/lifecycle/MutableLiveData;", "isShowBackPresentWeek$delegate", "isShowCurrentNoCourseTip", "isShowCurrentSchedule", "isShowPresentTips", "isTeaCourse", "setTeaCourse", "mCourseApiService", "Lcom/mredrock/cyxbs/course/network/CourseApiService;", "getMCourseApiService", "()Lcom/mredrock/cyxbs/course/network/CourseApiService;", "mCourseApiService$delegate", "mCoursesDatabase", "Lcom/mredrock/cyxbs/course/database/ScheduleDatabase;", "getMCoursesDatabase", "()Lcom/mredrock/cyxbs/course/database/ScheduleDatabase;", "mCoursesDatabase$delegate", "mDataGetStatus", "", "[Ljava/lang/Boolean;", "mIsGettingData", "mIsGottenFromInternet", "mUserName", "", "getMUserName", "()Ljava/lang/String;", "setMUserName", "(Ljava/lang/String;)V", "mUserNum", "mWeekTitle", "getMWeekTitle", "setMWeekTitle", "(Landroidx/databinding/ObservableField;)V", "notifyCourseDataChange", "", "getNotifyCourseDataChange", "nowCourse", "getNowCourse", "nowCoursePlace", "getNowCoursePlace", "nowCourseTime", "getNowCourseTime", "nowWeek", "getNowWeek", "setNowWeek", "(Landroidx/lifecycle/MutableLiveData;)V", "schoolCalendarUpdated", "getSchoolCalendarUpdated", "tomorrowTips", "kotlin.jvm.PlatformType", "getTomorrowTips", "buildHeadData", "clearCache", "courseAbnormalErrorHandling", "coursesFromInternet", "Lcom/mredrock/cyxbs/course/network/CourseApiWrapper;", "", "getAffairsDataFromDatabase", "getAffairsDataFromInternet", "getCoursesDataFromDatabase", "getCoursesDataFromInternet", "isForceFetch", "getSchedulesDataFromLocalThenNetwork", "userNum", "isGetOther", "direct", "getSchedulesFromInternet", "isContinueExecution", "isGetAllData", "index", "refreshAffairFromInternet", "resetGetStatus", "stopIntercept", "updateNowWeek", "networkNowWeek", "Companion", "module_course_debug"})
public final class CoursesViewModel extends com.mredrock.cyxbs.common.viewmodel.BaseViewModel {
    @org.jetbrains.annotations.NotNull()
    private com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState courseState = com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState.OrdinaryCourse;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> schoolCalendarUpdated = null;
    private java.lang.String mUserNum;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String mUserName = "";
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy isGetOthers$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.mredrock.cyxbs.course.network.Course> allCoursesData = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<kotlin.Unit> notifyCourseDataChange = null;
    
    /**
     * 用于单独储存真正的用于展示的课表数据
     *
     * 下面是一些无关紧要的话可以不看，只是解释这个值的重要性
     * 为什么有了上面这个值还需要这个呢
     * 因为后端和教务在线总是时不时各种抽风，跟服务端提需求我又怕打架，但是每次抽风之后是用户遭殃,
     * 也是客户端人员首先被喷，为了防止客户端被喷的次数，尽管我做了课表拉取错误处理，
     * 我还是要做一个当从服务端拉取的数据突然变为0但是本地是有课而且课表版本没有变化状态码正常时的处理
     * 这样子可以避免那些本来就没课的同学
     */
    private final java.util.ArrayList<com.mredrock.cyxbs.course.network.Course> courses = null;
    private final java.util.ArrayList<com.mredrock.cyxbs.course.network.Course> affairs = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<com.mredrock.cyxbs.course.network.Course> nowCourse = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.String> nowCourseTime = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.String> nowCoursePlace = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.Integer> isAffair = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.String> tomorrowTips = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.Integer> isShowCurrentSchedule = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.Integer> isShowCurrentNoCourseTip = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.lang.Integer> isShowPresentTips = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy isShowBackPresentWeek$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private androidx.lifecycle.MutableLiveData<java.lang.Integer> nowWeek;
    private boolean isFirstLoadItemAnim = true;
    private final kotlin.Lazy mCoursesDatabase$delegate = null;
    private final kotlin.Lazy mCourseApiService$delegate = null;
    private final java.lang.Boolean[] mDataGetStatus = {false, false};
    private boolean mIsGettingData = false;
    private boolean mIsGottenFromInternet = false;
    @org.jetbrains.annotations.NotNull()
    private androidx.databinding.ObservableField<java.lang.String> mWeekTitle;
    private boolean isTeaCourse = false;
    public static final int COURSE_TAG = 0;
    public static final int AFFAIR_TAG = 1;
    public static final com.mredrock.cyxbs.course.viewmodels.CoursesViewModel.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public final com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState getCourseState() {
        return null;
    }
    
    public final void setCourseState(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.ui.fragment.CourseContainerEntryFragment.CourseState p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> getSchoolCalendarUpdated() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMUserName() {
        return null;
    }
    
    public final void setMUserName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.Boolean> isGetOthers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.mredrock.cyxbs.course.network.Course> getAllCoursesData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<kotlin.Unit> getNotifyCourseDataChange() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<com.mredrock.cyxbs.course.network.Course> getNowCourse() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.String> getNowCourseTime() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.String> getNowCoursePlace() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.Integer> isAffair() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.String> getTomorrowTips() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.Integer> isShowCurrentSchedule() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.Integer> isShowCurrentNoCourseTip() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.Integer> isShowPresentTips() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Integer> isShowBackPresentWeek() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Integer> getNowWeek() {
        return null;
    }
    
    public final void setNowWeek(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.MutableLiveData<java.lang.Integer> p0) {
    }
    
    public final boolean isFirstLoadItemAnim() {
        return false;
    }
    
    public final void setFirstLoadItemAnim(boolean p0) {
    }
    
    private final com.mredrock.cyxbs.course.database.ScheduleDatabase getMCoursesDatabase() {
        return null;
    }
    
    private final com.mredrock.cyxbs.course.network.CourseApiService getMCourseApiService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.lang.String> getMWeekTitle() {
        return null;
    }
    
    public final void setMWeekTitle(@org.jetbrains.annotations.NotNull()
    androidx.databinding.ObservableField<java.lang.String> p0) {
    }
    
    public final boolean isTeaCourse() {
        return false;
    }
    
    public final void setTeaCourse(boolean p0) {
    }
    
    /**
     * 此方法用于加载数据
     * 优先从数据库中获取Course和Affair数据，等待数据库中获取完毕之后
     * 再从网络上获取
     *
     * @param userNum 当显示他人课表的时候就传入对应的的学号。默认为空，之后会为其赋值对应的帐号。
     * @param direct 如果有需要的时候，可以传入true，跳过从数据库加载，直接从网络上加载
     */
    public final void getSchedulesDataFromLocalThenNetwork(@org.jetbrains.annotations.NotNull()
    java.lang.String userNum, boolean isGetOther, boolean direct) {
    }
    
    /**
     * 是否可以继续获取数据
     * @return true 表示不可以，正在获取数据
     */
    private final boolean isContinueExecution() {
        return false;
    }
    
    /**
     * 当对事务进行增删改的时候所调用的，可直接只更新事务不更新课表
     * 其实这里也没有更新课表的必要，在用户打开app之后课表发生改变这种事几率太小
     */
    public final void refreshAffairFromInternet() {
    }
    
    /**
     * 此方法用于对重新从服务器上获取数据，
     * 这个方法只可以在第一次获取数据时在获取途中调用，不可用于公用方法直接调用然后通过网络更新数据
     * 如果需要跳过数据库直接通过网络更新数据请使用[getSchedulesDataFromLocalThenNetwork]传入合适的参数
     */
    private final void getSchedulesFromInternet() {
    }
    
    /**
     * 此方法用于获取数据库中的课程数据。
     */
    private final void getCoursesDataFromDatabase() {
    }
    
    /**
     * 此方法用于获取数据库中的事务数据。
     */
    private final void getAffairsDataFromDatabase() {
    }
    
    /**
     * 此方法用于从服务器中获取课程数据
     */
    private final void getCoursesDataFromInternet(boolean isForceFetch) {
    }
    
    /**
     * 课表的容错处理
     *
     * @param coursesFromInternet 直接从网络上拉取的课表数据
     * 因为有一个list的序列化和字符串对比，不建议在主线程调用这个方法，所以加上这个注解
     * 当然，你非要主线程调用那也没办法，你把注解去掉吧,其实退一步说也没啥大的计算操作
     */
    @androidx.annotation.WorkerThread()
    private final boolean courseAbnormalErrorHandling(com.mredrock.cyxbs.course.network.CourseApiWrapper<java.util.List<com.mredrock.cyxbs.course.network.Course>> coursesFromInternet) {
        return false;
    }
    
    /**
     * 此方法用于从服务器上获取事务数据
     */
    private final void getAffairsDataFromInternet() {
    }
    
    /**
     * 这个方法用于判断是尝试获取了课程和事务,之所以要这样是因为事务和课表分成了两个接口，同时请求
     * @param index 0代表获取了课表，1代表获取了事务
     *
     * 这里解释一下为什么这里要加上【同步】，虽然浪费一丢丢性能，但是这是很有必要的，
     * 这个方法会分别在异步【获取到事务和获取到课程】后调用，[mDataGetStatus]变量中的内容所有线程都可以更改的
     * 但此时就可能出现同步问题，最明显的影响就是获取了所有的数据但是没有进入第一个判断语句，从而导致不能显示课表
     * 实测，出现这种问题的概率很大，在我这几天尽百次的打开当中其中有4次出现了未进入第一个判断语句从到导致
     * [courses]有数据，但是[allCoursesData]没有，课表无法显示
     */
    private final synchronized void isGetAllData(int index) {
    }
    
    public final void buildHeadData() {
    }
    
    /**
     * 此方法用于重置课程获取状态
     */
    private final void resetGetStatus() {
    }
    
    /**
     * 用来更新周数和开学第一天service
     */
    private final void updateNowWeek(int networkNowWeek) {
    }
    
    /**
     * 获取数据完毕，不再拦截
     * @return 返回值没啥意思，这里是被用来表示不拦截
     */
    private final boolean stopIntercept() {
        return false;
    }
    
    public final void clearCache() {
    }
    
    public CoursesViewModel() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/CoursesViewModel$Companion;", "", "()V", "AFFAIR_TAG", "", "COURSE_TAG", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}