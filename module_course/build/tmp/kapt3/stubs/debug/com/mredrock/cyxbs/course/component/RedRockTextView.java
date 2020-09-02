package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * This is a TextView which display the text like cy's course column or week column. There are
 * three custom attrs. It doesn't adaptive multiline text, because I think it is not necessary
 *
 * @attr [R.styleable.RedRockTextView_orientation] this attr is used to set the orientation of the RedRockTextView
 * @attr [R.styleable.RedRockTextView_offsetBetweenText] this attr is used to set the gap between the texts
 *
 * isOthers attrs are the same with TextView.you can set the displayedStrings attr and the offset_between_text
 * attr in the code.
 *
 * Created by anriku on 2018/8/14.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\r\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 <2\u00020\u0001:\u0001<B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u00100\u001a\u000201H\u0002J\b\u00102\u001a\u000201H\u0002J\b\u00103\u001a\u000201H\u0002J\u0012\u00104\u001a\u0002012\b\u00105\u001a\u0004\u0018\u000106H\u0014J\u0018\u00107\u001a\u0002012\u0006\u00108\u001a\u00020\u00152\u0006\u00109\u001a\u00020\u0015H\u0014J\u0019\u0010:\u001a\u0002012\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\u0002\u0010\u000fR2\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t@FX\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0010\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0016\u001a\u00020\u00178BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0018\u0010\u0019R\u001e\u0010\u001c\u001a\u0012\u0012\u0004\u0012\u00020\u001e0\u001dj\b\u0012\u0004\u0012\u00020\u001e`\u001fX\u0082.\u00a2\u0006\u0002\n\u0000R\u001e\u0010 \u001a\u00020\u00152\u0006\u0010\b\u001a\u00020\u0015@BX\u0082\u000e\u00a2\u0006\b\n\u0000\"\u0004\b!\u0010\"R*\u0010#\u001a\u0004\u0018\u00010\u00152\b\u0010\b\u001a\u0004\u0018\u00010\u0015@FX\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010(\u001a\u0004\b$\u0010%\"\u0004\b&\u0010\'R\u001a\u0010)\u001a\u00020\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010\"R\u001b\u0010-\u001a\u00020\u00178BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b/\u0010\u001b\u001a\u0004\b.\u0010\u0019\u00a8\u0006="}, d2 = {"Lcom/mredrock/cyxbs/course/component/RedRockTextView;", "Landroidx/appcompat/widget/AppCompatTextView;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "value", "", "", "displayedStrings", "getDisplayedStrings", "()[Ljava/lang/CharSequence;", "setDisplayedStrings", "([Ljava/lang/CharSequence;)V", "[Ljava/lang/CharSequence;", "mElementHeight", "", "mElementWidth", "mOrientation", "", "mPaint", "Landroid/graphics/Paint;", "getMPaint", "()Landroid/graphics/Paint;", "mPaint$delegate", "Lkotlin/Lazy;", "mTextBounds", "Ljava/util/ArrayList;", "Landroid/graphics/Rect;", "Lkotlin/collections/ArrayList;", "offsetBetweenText", "setOffsetBetweenText", "(I)V", "position", "getPosition", "()Ljava/lang/Integer;", "setPosition", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "selectColor", "getSelectColor", "()I", "setSelectColor", "selectTextPaint", "getSelectTextPaint", "selectTextPaint$delegate", "computeBounds", "", "dealWithWrapContent", "initRedRockTextView", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "setStrings", "strings", "Companion", "module_course_debug"})
public final class RedRockTextView extends androidx.appcompat.widget.AppCompatTextView {
    private final kotlin.Lazy mPaint$delegate = null;
    private final kotlin.Lazy selectTextPaint$delegate = null;
    private float mElementWidth = 0.0F;
    private float mElementHeight = 0.0F;
    private java.util.ArrayList<android.graphics.Rect> mTextBounds;
    @org.jetbrains.annotations.NotNull()
    private java.lang.CharSequence[] displayedStrings = {};
    private int mOrientation = 0;
    private int offsetBetweenText = 0;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer position;
    private int selectColor = 16777215;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final com.mredrock.cyxbs.course.component.RedRockTextView.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    private final android.graphics.Paint getMPaint() {
        return null;
    }
    
    private final android.graphics.Paint getSelectTextPaint() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.CharSequence[] getDisplayedStrings() {
        return null;
    }
    
    public final void setDisplayedStrings(@org.jetbrains.annotations.NotNull()
    java.lang.CharSequence[] value) {
    }
    
    private final void setOffsetBetweenText(int value) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getPosition() {
        return null;
    }
    
    public final void setPosition(@org.jetbrains.annotations.Nullable()
    java.lang.Integer value) {
    }
    
    public final int getSelectColor() {
        return 0;
    }
    
    public final void setSelectColor(int p0) {
    }
    
    private final void initRedRockTextView() {
    }
    
    @java.lang.Override()
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }
    
    /**
     * This function is used to compute the displayedStrings bounds.
     */
    private final void computeBounds() {
    }
    
    /**
     * This function is used to deal wrap_content problem
     */
    private final void dealWithWrapContent() {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.Nullable()
    android.graphics.Canvas canvas) {
    }
    
    /**
     * This function is to set the displayedStrings which will be displayed.
     * @param strings displayedStrings going to be displayed.
     */
    public final void setStrings(@org.jetbrains.annotations.NotNull()
    java.lang.CharSequence[] strings) {
    }
    
    public RedRockTextView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public RedRockTextView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/course/component/RedRockTextView$Companion;", "", "()V", "HORIZONTAL", "", "VERTICAL", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}