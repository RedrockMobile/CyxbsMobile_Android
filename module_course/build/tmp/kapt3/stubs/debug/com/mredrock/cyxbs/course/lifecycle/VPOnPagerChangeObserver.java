package com.mredrock.cyxbs.course.lifecycle;

import java.lang.System;

/**
 * This class is used to automatically add and remove [ViewPager.OnPageChangeListener] in the [ViewPager].
 *
 * Created by anriku on 2018/9/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012 \b\u0002\u0010\b\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\t\u0012\u0014\b\u0002\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\fJ\b\u0010\u000f\u001a\u00020\u0007H\u0002R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\b\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/course/lifecycle/VPOnPagerChangeObserver;", "", "mViewPager", "Landroidx/viewpager/widget/ViewPager;", "mOnPageScrollStateChanged", "Lkotlin/Function1;", "", "", "mOnPageScrolled", "Lkotlin/Function3;", "", "mOnPageSelected", "(Landroidx/viewpager/widget/ViewPager;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function1;)V", "onPagerChangeListener", "Landroidx/viewpager/widget/ViewPager$OnPageChangeListener;", "addOnPageChangeListener", "module_course_debug"})
public final class VPOnPagerChangeObserver {
    private androidx.viewpager.widget.ViewPager.OnPageChangeListener onPagerChangeListener;
    private final androidx.viewpager.widget.ViewPager mViewPager = null;
    private final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> mOnPageScrollStateChanged = null;
    private final kotlin.jvm.functions.Function3<java.lang.Integer, java.lang.Float, java.lang.Integer, kotlin.Unit> mOnPageScrolled = null;
    private final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> mOnPageSelected = null;
    
    private final void addOnPageChangeListener() {
    }
    
    public VPOnPagerChangeObserver(@org.jetbrains.annotations.NotNull()
    androidx.viewpager.widget.ViewPager mViewPager, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> mOnPageScrollStateChanged, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.Float, ? super java.lang.Integer, kotlin.Unit> mOnPageScrolled, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> mOnPageSelected) {
        super();
    }
}