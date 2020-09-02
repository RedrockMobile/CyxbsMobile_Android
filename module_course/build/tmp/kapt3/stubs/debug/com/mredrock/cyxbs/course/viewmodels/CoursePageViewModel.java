package com.mredrock.cyxbs.course.viewmodels;

import java.lang.System;

/**
 * Created by anriku on 2018/8/21.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u0017B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0015\u001a\u00020\u0016J\b\u0010\b\u001a\u00020\u0016H\u0002J\b\u0010\u000f\u001a\u00020\u0016H\u0002R\"\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\f\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0004\u00a8\u0006\u0018"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/CoursePageViewModel;", "Landroidx/lifecycle/ViewModel;", "mWeek", "", "(I)V", "daysOfMonth", "", "", "getDaysOfMonth", "()[Ljava/lang/String;", "setDaysOfMonth", "([Ljava/lang/String;)V", "[Ljava/lang/String;", "month", "Landroidx/lifecycle/MutableLiveData;", "getMonth", "()Landroidx/lifecycle/MutableLiveData;", "nowWeek", "getNowWeek", "()I", "setNowWeek", "getDate", "", "DateViewModelFactory", "module_course_debug"})
public final class CoursePageViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> month = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String[] daysOfMonth;
    private int nowWeek = 0;
    private final int mWeek = 0;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getMonth() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getDaysOfMonth() {
        return null;
    }
    
    public final void setDaysOfMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String[] p0) {
    }
    
    public final int getNowWeek() {
        return 0;
    }
    
    public final void setNowWeek(int p0) {
    }
    
    /**
     * 此方法用于通过[SchoolCalendar]来进行日期的获取
     */
    public final void getDate() {
    }
    
    /**
     * 获取当前月份
     */
    private final void getMonth() {
    }
    
    /**
     * 此方法用于获取当前周各天的号数。
     */
    private final void getDaysOfMonth() {
    }
    
    public CoursePageViewModel(int mWeek) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\'\u0010\u0005\u001a\u0002H\u0006\"\n\b\u0000\u0010\u0006*\u0004\u0018\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00060\tH\u0016\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/mredrock/cyxbs/course/viewmodels/CoursePageViewModel$DateViewModelFactory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "mWeek", "", "(I)V", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "module_course_debug"})
    public static final class DateViewModelFactory implements androidx.lifecycle.ViewModelProvider.Factory {
        private final int mWeek = 0;
        
        @kotlin.Suppress(names = {"UNCHECKED_CAST"})
        @java.lang.Override()
        public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
        java.lang.Class<T> modelClass) {
            return null;
        }
        
        public DateViewModelFactory(int mWeek) {
            super();
        }
    }
}