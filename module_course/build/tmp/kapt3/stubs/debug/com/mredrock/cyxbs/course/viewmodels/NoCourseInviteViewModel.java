package com.mredrock.cyxbs.course.viewmodels;

import java.lang.System;

/**
 * Created by anriku on 2018/10/2.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u0017B%\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0013\u001a\u00020\u00142\u0010\b\u0002\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0016R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR)\u0010\f\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u000f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00030\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u0018"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/NoCourseInviteViewModel;", "Landroidx/lifecycle/ViewModel;", "mStuNumList", "", "", "nameList", "(Ljava/util/List;Ljava/util/List;)V", "mCourseApiService", "Lcom/mredrock/cyxbs/course/network/CourseApiService;", "kotlin.jvm.PlatformType", "getNameList", "()Ljava/util/List;", "studentsCourseMap", "Landroidx/databinding/ObservableField;", "", "", "Lcom/mredrock/cyxbs/course/network/Course;", "getStudentsCourseMap", "()Landroidx/databinding/ObservableField;", "getCourses", "", "onFinish", "Lkotlin/Function0;", "Factory", "module_course_debug"})
public final class NoCourseInviteViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.databinding.ObservableField<java.util.Map<java.lang.Integer, java.util.List<com.mredrock.cyxbs.course.network.Course>>> studentsCourseMap = null;
    private final com.mredrock.cyxbs.course.network.CourseApiService mCourseApiService = null;
    private final java.util.List<java.lang.String> mStuNumList = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> nameList = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.databinding.ObservableField<java.util.Map<java.lang.Integer, java.util.List<com.mredrock.cyxbs.course.network.Course>>> getStudentsCourseMap() {
        return null;
    }
    
    /**
     * 此方法用于获取各个学生的学期课表
     */
    public final void getCourses(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFinish) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getNameList() {
        return null;
    }
    
    public NoCourseInviteViewModel(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> mStuNumList, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> nameList) {
        super();
    }
    
    public NoCourseInviteViewModel() {
        super();
    }
    
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B!\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0006J\'\u0010\u0007\u001a\u0002H\b\"\n\b\u0000\u0010\b*\u0004\u0018\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\b0\u000bH\u0016\u00a2\u0006\u0002\u0010\fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/NoCourseInviteViewModel$Factory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "mStuNumList", "", "", "mNameList", "(Ljava/util/List;Ljava/util/List;)V", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "module_course_debug"})
    public static final class Factory implements androidx.lifecycle.ViewModelProvider.Factory {
        private final java.util.List<java.lang.String> mStuNumList = null;
        private final java.util.List<java.lang.String> mNameList = null;
        
        @java.lang.Override()
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
        java.lang.Class<T> modelClass) {
            return null;
        }
        
        public Factory(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> mStuNumList, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> mNameList) {
            super();
        }
    }
}