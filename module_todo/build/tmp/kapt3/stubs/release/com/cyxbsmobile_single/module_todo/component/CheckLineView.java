package com.cyxbsmobile_single.module_todo.component;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 14:34
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B%\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0018\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010-\u001a\u00020.H\u0002J\u0012\u0010/\u001a\u00020+2\b\u0010-\u001a\u0004\u0018\u00010.H\u0014J \u00100\u001a\u00020+2\u0006\u0010\u0013\u001a\u00020\u00122\u0010\b\u0002\u00101\u001a\n\u0012\u0004\u0012\u00020+\u0018\u000102J\u000e\u00103\u001a\u00020+2\u0006\u0010\u0013\u001a\u00020\u0012R\u001a\u0010\t\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u001a\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001b\u001a\u00020\u001c8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001f\u0010\u0019\u001a\u0004\b\u001d\u0010\u001eR\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010&\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\'\u0010\u000b\"\u0004\b(\u0010\rR\u000e\u0010)\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00064"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/component/CheckLineView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyle", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "checkedColor", "getCheckedColor", "()I", "setCheckedColor", "(I)V", "checkedLineWidth", "", "curAnimeProcess", "firstEndOfAnime", "", "isChecked", "leftCircleRectF", "Landroid/graphics/RectF;", "getLeftCircleRectF", "()Landroid/graphics/RectF;", "leftCircleRectF$delegate", "Lkotlin/Lazy;", "leftRadius", "paint", "Landroid/graphics/Paint;", "getPaint", "()Landroid/graphics/Paint;", "paint$delegate", "selectAnime", "Landroid/animation/ValueAnimator;", "startAngle", "sweepAngle", "unSelectAnime", "uncheckLineWidth", "uncheckedColor", "getUncheckedColor", "setUncheckedColor", "withCheckLine", "drawLeftArc", "", "process", "canvas", "Landroid/graphics/Canvas;", "onDraw", "setStatusWithAnime", "onSuccess", "Lkotlin/Function0;", "setStatusWithoutAnime", "module_todo_release"})
public final class CheckLineView extends android.view.View {
    private final kotlin.Lazy paint$delegate = null;
    private float startAngle = 40.0F;
    private float curAnimeProcess = 0.0F;
    private int checkedColor = 0;
    private int uncheckedColor = 0;
    private float leftRadius = 0.0F;
    private boolean isChecked = false;
    private float sweepAngle = 0.0F;
    private android.animation.ValueAnimator selectAnime;
    private android.animation.ValueAnimator unSelectAnime;
    private final kotlin.Lazy leftCircleRectF$delegate = null;
    private boolean firstEndOfAnime = true;
    private float uncheckLineWidth = 0.0F;
    private float checkedLineWidth = 0.0F;
    private boolean withCheckLine = true;
    private java.util.HashMap _$_findViewCache;
    
    private final android.graphics.Paint getPaint() {
        return null;
    }
    
    public final int getCheckedColor() {
        return 0;
    }
    
    public final void setCheckedColor(int p0) {
    }
    
    public final int getUncheckedColor() {
        return 0;
    }
    
    public final void setUncheckedColor(int p0) {
    }
    
    private final android.graphics.RectF getLeftCircleRectF() {
        return null;
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.Nullable()
    android.graphics.Canvas canvas) {
    }
    
    public final void setStatusWithoutAnime(boolean isChecked) {
    }
    
    public final void setStatusWithAnime(boolean isChecked, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    private final void drawLeftArc(float process, android.graphics.Canvas canvas) {
    }
    
    public CheckLineView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyle) {
        super(null);
    }
    
    public CheckLineView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    public CheckLineView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
}