package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * SimpleDotsView is used as an indicator of the ViewPager or the slideshow.
 *
 * @attr [R.styleable.SimpleDotsView_dotGap] The gap between two dots.
 * @attr [R.styleable.SimpleDotsView_dotRadius] The radius of the a dot.
 *
 * Created by anriku on 2018/9/17.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u0015\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 *2\u00020\u0001:\u0001*B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007B!\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\b\u0010\u001f\u001a\u00020 H\u0002J\b\u0010!\u001a\u00020 H\u0002J\u0010\u0010\"\u001a\u00020 2\u0006\u0010#\u001a\u00020$H\u0014J\u0018\u0010%\u001a\u00020 2\u0006\u0010&\u001a\u00020\t2\u0006\u0010\'\u001a\u00020\tH\u0014J\u0010\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\tH\u0016R\u000e\u0010\u000b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R$\u0010\r\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\t@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u001a\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/mredrock/cyxbs/course/component/SimpleDotsView;", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$DotsView;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "currentFocusDot", "value", "dotsSize", "getDotsSize", "()I", "setDotsSize", "(I)V", "mDotGap", "mDotRadius", "mDotsColors", "", "getMDotsColors", "()[I", "mDotsColors$delegate", "Lkotlin/Lazy;", "mDotsViewHeight", "mDotsViewWidth", "mFirstDotLeftMargin", "mPaint", "Landroid/graphics/Paint;", "computeFirstDotLeftMargin", "", "initDotsView", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "setCurrentFocusDot", "position", "Companion", "module_course_debug"})
public final class SimpleDotsView extends com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView {
    private int mDotsViewWidth = 0;
    private int mDotsViewHeight = 0;
    private android.graphics.Paint mPaint;
    private int mDotRadius;
    private int mFirstDotLeftMargin = 0;
    private final kotlin.Lazy mDotsColors$delegate = null;
    private int currentFocusDot = 0;
    private int dotsSize = 0;
    private int mDotGap;
    private static final java.lang.String TAG = "SimpleDotsView";
    public static final com.mredrock.cyxbs.course.component.SimpleDotsView.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    private final int[] getMDotsColors() {
        return null;
    }
    
    public final int getDotsSize() {
        return 0;
    }
    
    public final void setDotsSize(int value) {
    }
    
    private final void initDotsView() {
    }
    
    /**
     * This function is used to set currentFocus dot.
     * @param position current focus dot's position.
     */
    @java.lang.Override()
    public void setCurrentFocusDot(int position) {
    }
    
    /**
     * This function is used to compute the SimpleDotsView's left margin.
     */
    private final void computeFirstDotLeftMargin() {
    }
    
    @java.lang.Override()
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    public SimpleDotsView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public SimpleDotsView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    public SimpleDotsView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyleAttr) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/course/component/SimpleDotsView$Companion;", "", "()V", "TAG", "", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}