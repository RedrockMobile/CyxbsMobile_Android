package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * @author Jovines
 * @create 2020-02-21 1:28 PM
 *
 *
 * 描述:课表头部可拉动提示，别问为什么要写这个动态的东西
 *     问的话那还要从一只蝙蝠说起（闲的）
 *     具体描述一下，这是一个会从平的提示小条变化到上箭头或者下箭头的自定义View
 *     本来是用在课表的头部中心的，但是发现在BottomSheet上会
 *     出现性能问题，所以就放弃使用了
 *     todo 初步判定是因为不断的[invalidate]绘制会造成BottomSheet轻微卡顿
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000 02\u00020\u0001:\u00010B\u0011\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007B#\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0010\u0010)\u001a\u00020\f2\u0006\u0010*\u001a\u00020\fH\u0002J\b\u0010+\u001a\u00020,H\u0002J\u0010\u0010-\u001a\u00020,2\u0006\u0010.\u001a\u00020/H\u0014R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0011\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\u0010\u001a\u0004\b\u0012\u0010\u000eR\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u0019@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u000e\u0010\u001f\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010 \u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\t@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u000e\u0010%\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010&\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b(\u0010\u0010\u001a\u0004\b\'\u0010\u000e\u00a8\u00061"}, d2 = {"Lcom/mredrock/cyxbs/course/component/RedRockTipsView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "bottomWayAnimation", "Landroid/animation/ValueAnimator;", "getBottomWayAnimation", "()Landroid/animation/ValueAnimator;", "bottomWayAnimation$delegate", "Lkotlin/Lazy;", "centerWayAnimation", "getCenterWayAnimation", "centerWayAnimation$delegate", "leftPath", "Landroid/graphics/Path;", "paint", "Landroid/graphics/Paint;", "value", "", "position", "getPosition", "()F", "setPosition", "(F)V", "rightPath", "state", "getState", "()I", "setState", "(I)V", "tipColor", "topWayAnimation", "getTopWayAnimation", "topWayAnimation$delegate", "animSetting", "animation", "init", "", "onDraw", "canvas", "Landroid/graphics/Canvas;", "Companion", "module_course_debug"})
public final class RedRockTipsView extends android.view.View {
    private final android.graphics.Paint paint = null;
    private final android.graphics.Path leftPath = null;
    private final android.graphics.Path rightPath = null;
    private int tipColor = -16777216;
    private float position = 0.0F;
    
    /**
     * 使用这个View只用设置相应的状态就好了
     */
    private int state = 1;
    
    /**
     * 将该view的状态从向上指或者向下指的状态恢复到平的状态
     */
    private final kotlin.Lazy centerWayAnimation$delegate = null;
    
    /**
     * 将状态变化成向下指
     */
    private final kotlin.Lazy bottomWayAnimation$delegate = null;
    
    /**
     * 将状态变化成向上指
     */
    private final kotlin.Lazy topWayAnimation$delegate = null;
    public static final int UP = 0;
    public static final int CENTER = 1;
    public static final int BOTTOM = 2;
    public static final com.mredrock.cyxbs.course.component.RedRockTipsView.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    public final float getPosition() {
        return 0.0F;
    }
    
    public final void setPosition(float value) {
    }
    
    public final int getState() {
        return 0;
    }
    
    public final void setState(int value) {
    }
    
    /**
     * 将该view的状态从向上指或者向下指的状态恢复到平的状态
     */
    private final android.animation.ValueAnimator getCenterWayAnimation() {
        return null;
    }
    
    /**
     * 将状态变化成向下指
     */
    private final android.animation.ValueAnimator getBottomWayAnimation() {
        return null;
    }
    
    /**
     * 将状态变化成向上指
     */
    private final android.animation.ValueAnimator getTopWayAnimation() {
        return null;
    }
    
    private final void init() {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final android.animation.ValueAnimator animSetting(android.animation.ValueAnimator animation) {
        return null;
    }
    
    public RedRockTipsView(@org.jetbrains.annotations.Nullable()
    android.content.Context context) {
        super(null);
    }
    
    public RedRockTipsView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    public RedRockTipsView(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyleAttr) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/mredrock/cyxbs/course/component/RedRockTipsView$Companion;", "", "()V", "BOTTOM", "", "CENTER", "UP", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}