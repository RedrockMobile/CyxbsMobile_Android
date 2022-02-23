package com.cyxbsmobile_single.module_todo.adapter.slide_callback;

import java.lang.System;

/**
 * @date 2021-08-14
 * @author Sca RayleighZ
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001:\u0002GHB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u00103\u001a\u00020\n2\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u000207H\u0016J\u0010\u00108\u001a\u00020\u00182\u0006\u00109\u001a\u00020\u0018H\u0016J\u0010\u0010:\u001a\u00020\u00182\u0006\u00106\u001a\u000207H\u0016J@\u0010;\u001a\u00020<2\u0006\u0010=\u001a\u00020>2\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u0002072\u0006\u0010?\u001a\u00020\u00182\u0006\u0010@\u001a\u00020\u00182\u0006\u0010A\u001a\u00020\n2\u0006\u0010B\u001a\u00020\u0010H\u0016J \u0010C\u001a\u00020\u00102\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u0002072\u0006\u0010D\u001a\u000207H\u0016J\u0018\u0010E\u001a\u00020<2\u0006\u00106\u001a\u0002072\u0006\u0010F\u001a\u00020\nH\u0016R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\f\"\u0004\b\u0016\u0010\u000eR\u001a\u0010\u0017\u001a\u00020\u0018X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001d\u001a\u00020\u0018X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001a\"\u0004\b\u001f\u0010\u001cR\u001a\u0010 \u001a\u00020!X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R#\u0010&\u001a\n (*\u0004\u0018\u00010\'0\'8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b+\u0010,\u001a\u0004\b)\u0010*R\u001a\u0010-\u001a\u00020.X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102\u00a8\u0006I"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback;", "Landroidx/recyclerview/widget/ItemTouchHelper$Callback;", "()V", "curStatus", "Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$CurStatus;", "getCurStatus", "()Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$CurStatus;", "setCurStatus", "(Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$CurStatus;)V", "delWidth", "", "getDelWidth", "()I", "setDelWidth", "(I)V", "isFirstTimeReleaseFinger", "", "()Z", "setFirstTimeReleaseFinger", "(Z)V", "itemWidth", "getItemWidth", "setItemWidth", "onStartItemPos", "", "getOnStartItemPos", "()F", "setOnStartItemPos", "(F)V", "onTouchDx", "getOnTouchDx", "setOnTouchDx", "touchFingerCount", "", "getTouchFingerCount", "()J", "setTouchFingerCount", "(J)V", "transAnime", "Landroid/animation/ValueAnimator;", "kotlin.jvm.PlatformType", "getTransAnime", "()Landroid/animation/ValueAnimator;", "transAnime$delegate", "Lkotlin/Lazy;", "userIntent", "Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$UserIntent;", "getUserIntent", "()Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$UserIntent;", "setUserIntent", "(Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$UserIntent;)V", "getMovementFlags", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "viewHolder", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "getSwipeEscapeVelocity", "defaultValue", "getSwipeThreshold", "onChildDraw", "", "c", "Landroid/graphics/Canvas;", "dX", "dY", "actionState", "isCurrentlyActive", "onMove", "target", "onSwiped", "direction", "CurStatus", "UserIntent", "module_todo_debug"})
public final class SlideCallback extends androidx.recyclerview.widget.ItemTouchHelper.Callback {
    @org.jetbrains.annotations.NotNull()
    private com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.CurStatus curStatus = com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.CurStatus.CLOSE;
    private int delWidth = 0;
    private int itemWidth = 0;
    @org.jetbrains.annotations.NotNull()
    private com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.UserIntent userIntent = com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.UserIntent.UNDEFINE;
    private boolean isFirstTimeReleaseFinger = true;
    private float onTouchDx = 0.0F;
    private long touchFingerCount = 0L;
    private float onStartItemPos = 0.0F;
    private final kotlin.Lazy transAnime$delegate = null;
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.CurStatus getCurStatus() {
        return null;
    }
    
    public final void setCurStatus(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.CurStatus p0) {
    }
    
    public final int getDelWidth() {
        return 0;
    }
    
    public final void setDelWidth(int p0) {
    }
    
    public final int getItemWidth() {
        return 0;
    }
    
    public final void setItemWidth(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.UserIntent getUserIntent() {
        return null;
    }
    
    public final void setUserIntent(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.UserIntent p0) {
    }
    
    public final boolean isFirstTimeReleaseFinger() {
        return false;
    }
    
    public final void setFirstTimeReleaseFinger(boolean p0) {
    }
    
    public final float getOnTouchDx() {
        return 0.0F;
    }
    
    public final void setOnTouchDx(float p0) {
    }
    
    public final long getTouchFingerCount() {
        return 0L;
    }
    
    public final void setTouchFingerCount(long p0) {
    }
    
    public final float getOnStartItemPos() {
        return 0.0F;
    }
    
    public final void setOnStartItemPos(float p0) {
    }
    
    private final android.animation.ValueAnimator getTransAnime() {
        return null;
    }
    
    @java.lang.Override()
    public int getMovementFlags(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        return 0;
    }
    
    @java.lang.Override()
    public boolean onMove(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
        return false;
    }
    
    /**
     * 侧滑逻辑
     * 1、不使用此类自带的动画效果，所有动画效果均由手撸
     * 2、状态转换判断逻辑：
     * if(dX > 0)
     *     判定为右滑
     *         if(距离 > 删除按钮宽度的1/2)
     *             判定为转换为关闭状态，并触发动画
     *         else
     *             判定为转换为起始状态，并触发动画
     * else
     *     判定为左滑
     *         if(距离 > 删除按钮宽度的1/2)
     *             判定为转换为打开状态，并触发动画
     *         else
     *             判定为转换为起始状态，并触发动画
     */
    @java.lang.Override()
    public void onChildDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas c, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }
    
    @java.lang.Override()
    public float getSwipeThreshold(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        return 0.0F;
    }
    
    @java.lang.Override()
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.0F;
    }
    
    @java.lang.Override()
    public void onSwiped(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
    }
    
    public SlideCallback() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$UserIntent;", "", "(Ljava/lang/String;I)V", "RIGHT", "LEFT", "UNDEFINE", "module_todo_debug"})
    public static enum UserIntent {
        /*public static final*/ RIGHT /* = new RIGHT() */,
        /*public static final*/ LEFT /* = new LEFT() */,
        /*public static final*/ UNDEFINE /* = new UNDEFINE() */;
        
        UserIntent() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/slide_callback/SlideCallback$CurStatus;", "", "(Ljava/lang/String;I)V", "OPEN", "CLOSE", "module_todo_debug"})
    public static enum CurStatus {
        /*public static final*/ OPEN /* = new OPEN() */,
        /*public static final*/ CLOSE /* = new CLOSE() */;
        
        CurStatus() {
        }
    }
}