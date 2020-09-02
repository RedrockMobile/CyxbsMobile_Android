package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * [FlexibleNumberPicker] is a NumberPicker which you can custom the color of the text displayed
 * ,you can select show a middle selector or a selection Divider and you can custom their color.
 *
 * @attr [R.styleable.FlexibleNumberPicker_selectorBkColor] the middle selector color
 * @attr [R.styleable.FlexibleNumberPicker_selectorWheelPaintColor] the color painting the text
 * @attr [R.styleable.FlexibleNumberPicker_isSelectionDividerShow] true: show the selection divider.
 * false: show the middle selector.
 *
 * Created by anriku on 2018/9/11.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\u0015\u001a\u00020\u0016H\u0003J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\b\u0010\u0018\u001a\u00020\u0016H\u0002J\u0010\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u000fH\u0002J\u0012\u0010\u001b\u001a\u00020\u00162\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014J0\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001f\u001a\u00020\u000f2\u0006\u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00132\u0006\u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u0013H\u0014J\b\u0010$\u001a\u00020\u0016H\u0002J\b\u0010%\u001a\u00020\u0016H\u0002R6\u0010\b\u001a*\u0012\u000e\b\u0000\u0012\n \u000b*\u0004\u0018\u00010\n0\n \u000b*\u0014\u0012\u000e\b\u0000\u0012\n \u000b*\u0004\u0018\u00010\n0\n\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/mredrock/cyxbs/course/component/FlexibleNumberPicker;", "Landroid/widget/NumberPicker;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "mClazz", "Ljava/lang/Class;", "", "kotlin.jvm.PlatformType", "mDrawSelectorBkPaint", "Landroid/graphics/Paint;", "mIsShowSelectionDivider", "", "mRect", "Landroid/graphics/Rect;", "mSelectorBkColor", "", "mSelectorWheelPaintColor", "avoidWarningUsingReflection", "", "initEditText", "initFlexibleNumberPicker", "isSelectionDividerShow", "isShow", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onLayout", "changed", "left", "top", "right", "bottom", "prepareDrawSelectorBk", "setSelectorWheelPaintColor", "module_course_debug"})
public final class FlexibleNumberPicker extends android.widget.NumberPicker {
    private final java.lang.Class mClazz = null;
    private int mSelectorBkColor;
    private int mSelectorWheelPaintColor;
    private android.graphics.Paint mDrawSelectorBkPaint;
    private boolean mIsShowSelectionDivider = false;
    private android.graphics.Rect mRect;
    private java.util.HashMap _$_findViewCache;
    
    private final void initFlexibleNumberPicker() {
    }
    
    @java.lang.Override()
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.Nullable()
    android.graphics.Canvas canvas) {
    }
    
    /**
     * This function is used to init the EditText's color and make the EditText is unable to edit.
     * If you don't init at there the beginning of the EditText color will be black.
     * Want to get more understanding? go to the source code!!!
     */
    private final void initEditText() {
    }
    
    /**
     * This function is used to get the size of the middle selector's rect drawn by onDraw later.
     * it should be invoked at onLayout. because the field mSelectorElementHeight in [NumberPicker]
     * is determined in the onLayout. And to get more perfect performance you shouldn't call this
     * in onDraw.
     */
    private final void prepareDrawSelectorBk() {
    }
    
    /**
     * This function is used to set the color of the paint painting the EditText
     */
    private final void setSelectorWheelPaintColor() {
    }
    
    /**
     * This function is used to decide whether the selectionDivider will be shown.
     * @param isShow true: show the selectionDivider but the selectorBk won't be drawn.
     *              false: the selectionBk will be drawn but the selectionDivider won't be drawn.
     */
    private final void isSelectionDividerShow(boolean isShow) {
    }
    
    /**
     * This method is used to avoid warning, if the app uses the reflection. Because in Android Pie
     * if you use the reflection, you will get a warning. But this isn't a good method for a long time.
     * So we should find a good method.
     */
    @android.annotation.SuppressLint(value = {"PrivateApi", "DiscouragedPrivateApi"})
    private final void avoidWarningUsingReflection() {
    }
    
    public FlexibleNumberPicker(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public FlexibleNumberPicker(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
}