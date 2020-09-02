package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * [ScheduleView] is used to display the course. The use of it is very easy. you can only implement
 * the [ScheduleView.Adapter].
 *
 * @attr [R.styleable.ScheduleView_elementGap] represent the gap between two elements.
 * @attr [R.styleable.ScheduleView_touchViewColor] represent the touchView's color
 * @attr [R.styleable.ScheduleView_touchViewDrawable] represent the touchView's drawable.
 * @attr [R.styleable.ScheduleView_noCourseDrawable] represent the drawable to be display when there.
 * isn't courses.
 * @attr [R.styleable.ScheduleView_heightAtLowDpi] represent the ScheduleView's height when it work.
 * at low dpi device.
 * @attr [R.styleable.ScheduleView_highlightColor] represent the ScheduleView's today highlight color.
 * @attr [R.styleable.ScheduleView_emptyTextSize] represent no course the displayed text' size.
 * @attr [R.styleable.ScheduleView_emptyText] represent no course the displayed text.
 * @attr [R.styleable.ScheduleView_isDisplayCourse] true: display course; false: display no course invite.
 *
 * @attr [R.attr.ScheduleViewStyle] represent the style name of Schedule's View, so you can use a style
 * instead of write it directly in the XML.
 *
 * Created by anriku on 2018/8/14.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u00a4\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 r2\u00020\u0001:\u0004qrstB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007Js\u0010A\u001a\u00020\u00112\u0006\u0010B\u001a\u00020C2O\b\u0002\u0010D\u001aI\u0012\u0013\u0012\u00110F\u00a2\u0006\f\bG\u0012\b\bH\u0012\u0004\b\b(I\u0012\u0013\u0012\u00110\u0017\u00a2\u0006\f\bG\u0012\b\bH\u0012\u0004\b\b(J\u0012\u0013\u0012\u00110\u0017\u00a2\u0006\f\bG\u0012\b\bH\u0012\u0004\b\b(K\u0012\u0004\u0012\u00020\u0011\u0018\u00010E2\u0010\b\u0002\u0010L\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010@H\u0002J\u001a\u0010M\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\t2\b\b\u0002\u0010O\u001a\u00020,H\u0002J\b\u0010P\u001a\u00020\u0011H\u0002J\b\u0010Q\u001a\u00020\u0011H\u0002J\u0006\u0010R\u001a\u00020\u0011J\b\u0010S\u001a\u00020\u0011H\u0002J\b\u0010T\u001a\u00020\u0011H\u0002J\b\u0010U\u001a\u00020VH\u0002J \u0010W\u001a\u00020\u00112\u0006\u0010B\u001a\u00020C2\u0006\u0010X\u001a\u00020\u00172\u0006\u0010Y\u001a\u00020\u0017H\u0014J\u0006\u0010Z\u001a\u00020\u0011J\u0010\u0010[\u001a\u00020\u00112\u0006\u0010\\\u001a\u00020]H\u0014J0\u0010^\u001a\u00020\u00112\u0006\u0010_\u001a\u00020,2\u0006\u0010`\u001a\u00020\u00172\u0006\u0010a\u001a\u00020\u00172\u0006\u0010b\u001a\u00020\u00172\u0006\u0010c\u001a\u00020\u0017H\u0014J\u0018\u0010d\u001a\u00020\u00112\u0006\u0010e\u001a\u00020\u00172\u0006\u0010f\u001a\u00020\u0017H\u0014J\u0010\u0010g\u001a\u00020,2\u0006\u0010h\u001a\u00020iH\u0016J\b\u0010j\u001a\u00020,H\u0016J\b\u0010k\u001a\u00020\u0011H\u0002J \u0010l\u001a\u00020m2\u0006\u0010n\u001a\u00020\u00172\u0006\u0010o\u001a\u00020\u00172\u0006\u0010p\u001a\u00020\u0017H\u0002R(\u0010\n\u001a\u0004\u0018\u00010\t2\b\u0010\b\u001a\u0004\u0018\u00010\t@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR(\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0010X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001e\u001a\u00020\u001f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\"\u0010#\u001a\u0004\b \u0010!R\u001b\u0010$\u001a\u00020%8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b(\u0010#\u001a\u0004\b&\u0010\'R\u000e\u0010)\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020,X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u000200X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u00103\u001a\u00020%8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b5\u0010#\u001a\u0004\b4\u0010\'R\u001b\u00106\u001a\u0002078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b:\u0010#\u001a\u0004\b8\u00109R\u000e\u0010;\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010=\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010?\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010@X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006u"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleView;", "Landroid/view/ViewGroup;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "value", "Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;", "adapter", "getAdapter", "()Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;", "setAdapter", "(Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;)V", "adapterChangeListener", "Lkotlin/Function1;", "", "getAdapterChangeListener", "()Lkotlin/jvm/functions/Function1;", "setAdapterChangeListener", "(Lkotlin/jvm/functions/Function1;)V", "courseItemCount", "", "mBasicElementHeight", "mBasicElementWidth", "mElementGap", "mEmptyText", "", "mEmptyTextSize", "mEmptyTextView", "Landroid/widget/TextView;", "getMEmptyTextView", "()Landroid/widget/TextView;", "mEmptyTextView$delegate", "Lkotlin/Lazy;", "mEndPoint", "Landroid/graphics/PointF;", "getMEndPoint", "()Landroid/graphics/PointF;", "mEndPoint$delegate", "mHeightAtLowDpi", "mHighlightColor", "mIsDisplayCourse", "", "mIsEmpty", "mNoCourseDrawableResId", "mPaint", "Landroid/graphics/Paint;", "mScheduleViewHeight", "mScheduleViewWidth", "mStartPoint", "getMStartPoint", "mStartPoint$delegate", "mTouchView", "Landroid/widget/ImageView;", "getMTouchView", "()Landroid/widget/ImageView;", "mTouchView$delegate", "mTouchViewColor", "mTouchViewDrawableResId", "noCourseImageHeight", "noCourseImageWidth", "touchClickListener", "Lkotlin/Function0;", "actionItemInfoStatus", "child", "Landroid/view/View;", "infoFound", "Lkotlin/Function3;", "Lcom/mredrock/cyxbs/course/component/ScheduleView$ScheduleItem;", "Lkotlin/ParameterName;", "name", "itemViewInfo", "row", "column", "infoNotFound", "addCourseView", "notNullAdapter", "isCheckChange", "addNoCourseView", "checkIsNoCourse", "clearTouchView", "initScheduleView", "initTouchView", "layoutTransition", "Landroid/animation/LayoutTransition;", "measureChild", "parentWidthMeasureSpec", "parentHeightMeasureSpec", "notifyDataChange", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onLayout", "changed", "left", "top", "right", "bottom", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "performClick", "showAnimationForTheFirstTime", "zoomImage", "Landroid/graphics/drawable/Drawable;", "resId", "w", "h", "Adapter", "Companion", "ScheduleItem", "TouchPositionData", "module_course_debug"})
public final class ScheduleView extends android.view.ViewGroup {
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super com.mredrock.cyxbs.course.component.ScheduleView.Adapter, kotlin.Unit> adapterChangeListener;
    private kotlin.jvm.functions.Function0<kotlin.Unit> touchClickListener;
    
    /**
     * 接收一个继承了本类内部抽象Adapter的类对象，
     * 以此把获取数据和itemView的逻辑和itemView数据获取（仿造ViewPager或RecyclerView）
     */
    @org.jetbrains.annotations.Nullable()
    private com.mredrock.cyxbs.course.component.ScheduleView.Adapter adapter;
    private final kotlin.Lazy mStartPoint$delegate = null;
    private final kotlin.Lazy mEndPoint$delegate = null;
    private final kotlin.Lazy mTouchView$delegate = null;
    private android.graphics.Paint mPaint;
    private boolean mIsEmpty = true;
    private final kotlin.Lazy mEmptyTextView$delegate = null;
    private int courseItemCount = 0;
    private int mScheduleViewWidth = 0;
    private int mScheduleViewHeight = 0;
    private int mBasicElementWidth = 0;
    private int mBasicElementHeight = 0;
    private boolean mIsDisplayCourse = true;
    private int mElementGap = 0;
    private int mTouchViewColor = 0;
    private int mHeightAtLowDpi = 0;
    private int mTouchViewDrawableResId = 0;
    private int mHighlightColor = 0;
    private int mNoCourseDrawableResId = 0;
    private java.lang.String mEmptyText;
    private int mEmptyTextSize;
    private int noCourseImageHeight = 0;
    private int noCourseImageWidth = 0;
    public static final int CLICK_RESPONSE_DISTANCE = 10;
    public static final com.mredrock.cyxbs.course.component.ScheduleView.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<com.mredrock.cyxbs.course.component.ScheduleView.Adapter, kotlin.Unit> getAdapterChangeListener() {
        return null;
    }
    
    public final void setAdapterChangeListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.mredrock.cyxbs.course.component.ScheduleView.Adapter, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.mredrock.cyxbs.course.component.ScheduleView.Adapter getAdapter() {
        return null;
    }
    
    public final void setAdapter(@org.jetbrains.annotations.Nullable()
    com.mredrock.cyxbs.course.component.ScheduleView.Adapter value) {
    }
    
    private final android.graphics.PointF getMStartPoint() {
        return null;
    }
    
    private final android.graphics.PointF getMEndPoint() {
        return null;
    }
    
    private final android.widget.ImageView getMTouchView() {
        return null;
    }
    
    private final android.widget.TextView getMEmptyTextView() {
        return null;
    }
    
    private final void initScheduleView() {
    }
    
    private final android.animation.LayoutTransition layoutTransition() {
        return null;
    }
    
    /**
     * Do some init work for the [mTouchView].
     */
    private final void initTouchView() {
    }
    
    private final void addNoCourseView() {
    }
    
    @java.lang.Override()
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }
    
    @java.lang.Override()
    protected void measureChild(@org.jetbrains.annotations.NotNull()
    android.view.View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
    }
    
    /**
     * 动态更新
     */
    public final void notifyDataChange() {
    }
    
    /**
     */
    private final void addCourseView(com.mredrock.cyxbs.course.component.ScheduleView.Adapter notNullAdapter, boolean isCheckChange) {
    }
    
    /**
     * 检查是否需要添加没课提示图片
     */
    private final void checkIsNoCourse() {
    }
    
    private final void actionItemInfoStatus(android.view.View child, kotlin.jvm.functions.Function3<? super com.mredrock.cyxbs.course.component.ScheduleView.ScheduleItem, ? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> infoFound, kotlin.jvm.functions.Function0<kotlin.Unit> infoNotFound) {
    }
    
    @java.lang.Override()
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }
    
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    @java.lang.Override()
    public boolean performClick() {
        return false;
    }
    
    /**
     * 缩放图片大小
     */
    private final android.graphics.drawable.Drawable zoomImage(int resId, int w, int h) {
        return null;
    }
    
    public final void clearTouchView() {
    }
    
    private final void showAnimationForTheFirstTime() {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    public ScheduleView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public ScheduleView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    /**
     * This abstract class is the adapter of [ScheduleView]. If you use the ScheduleView, you should
     * give the ScheduleView a implemented Adapter.
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\u0004\u0018\u00010\u0004H&\u00a2\u0006\u0002\u0010\u0005J \u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH&J\u001a\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004H&J \u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004H\u0016J\b\u0010\u0011\u001a\u00020\u000fH\u0016J \u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u00132\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u0015H\u0016\u00a8\u0006\u0016"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleView$Adapter;", "", "()V", "getHighLightPosition", "", "()Ljava/lang/Integer;", "getItemView", "Landroid/view/View;", "row", "column", "container", "Landroid/view/ViewGroup;", "getItemViewInfo", "Lcom/mredrock/cyxbs/course/component/ScheduleView$ScheduleItem;", "initItemView", "", "view", "notifyDataChange", "onTouchViewClick", "Lkotlin/Function0;", "position", "Lcom/mredrock/cyxbs/course/component/ScheduleView$TouchPositionData;", "module_course_debug"})
    public static abstract class Adapter {
        
        /**
         * This method is used to get the ItemView which will be display in the [ScheduleView].
         *
         * @param row The ItemView's row.
         * @param column The ItemView's column.
         * @param container There return the [ScheduleView] as the ItemView's container.
         */
        @org.jetbrains.annotations.NotNull()
        public abstract android.view.View getItemView(int row, int column, @org.jetbrains.annotations.NotNull()
        android.view.ViewGroup container);
        
        /**
         * This method is used to get the ItemView's Info.
         * @param row The ItemView's row.
         * @param column The ItemView's column.
         * @return [ScheduleItem] is a class which you should map your data to it. And [ScheduleView]
         * use it to determine the ItemView's size.
         */
        @org.jetbrains.annotations.Nullable()
        public abstract com.mredrock.cyxbs.course.component.ScheduleView.ScheduleItem getItemViewInfo(int row, int column);
        
        /**
         * This method is used to implement the [ScheduleView.mTouchView]'s Click Listener.
         *
         * @return It is a Function Object.
         * 1.It is used to set the [mTouchView]'s onClickListener if the return is not null.
         * 2.If it return null, the mTouchView won't be displayed.
         */
        @org.jetbrains.annotations.Nullable()
        public kotlin.jvm.functions.Function0<kotlin.Unit> onTouchViewClick(@org.jetbrains.annotations.NotNull()
        android.view.View view, @org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.component.ScheduleView.TouchPositionData position) {
            return null;
        }
        
        /**
         * This method is used to get the position which will be highlighted.
         *
         * @return not null: the highlight position.
         * null: don't highlight.
         */
        @org.jetbrains.annotations.Nullable()
        public abstract java.lang.Integer getHighLightPosition();
        
        public void notifyDataChange() {
        }
        
        public void initItemView(@org.jetbrains.annotations.NotNull()
        android.view.View view, int row, int column) {
        }
        
        public Adapter() {
            super();
        }
    }
    
    /**
     * This class represents the ItemSize going to display
     *
     * @param itemWidth 表示一个课占多宽
     * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
     * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\u0002\u0010\u0006J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0001H\u00c6\u0003J\'\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0001H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0016"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleView$ScheduleItem;", "", "itemWidth", "", "itemHeight", "uniqueSign", "(IILjava/lang/Object;)V", "getItemHeight", "()I", "getItemWidth", "getUniqueSign", "()Ljava/lang/Object;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "module_course_debug"})
    public static final class ScheduleItem {
        private final int itemWidth = 0;
        private final int itemHeight = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.Object uniqueSign = null;
        
        public final int getItemWidth() {
            return 0;
        }
        
        public final int getItemHeight() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.Object getUniqueSign() {
            return null;
        }
        
        public ScheduleItem(int itemWidth, int itemHeight, @org.jetbrains.annotations.NotNull()
        java.lang.Object uniqueSign) {
            super();
        }
        
        public ScheduleItem() {
            super();
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.Object component3() {
            return null;
        }
        
        /**
         * This class represents the ItemSize going to display
         *
         * @param itemWidth 表示一个课占多宽
         * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
         * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
         */
        @org.jetbrains.annotations.NotNull()
        public final com.mredrock.cyxbs.course.component.ScheduleView.ScheduleItem copy(int itemWidth, int itemHeight, @org.jetbrains.annotations.NotNull()
        java.lang.Object uniqueSign) {
            return null;
        }
        
        /**
         * This class represents the ItemSize going to display
         *
         * @param itemWidth 表示一个课占多宽
         * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
         * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
         */
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        /**
         * This class represents the ItemSize going to display
         *
         * @param itemWidth 表示一个课占多宽
         * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
         * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
         */
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        /**
         * This class represents the ItemSize going to display
         *
         * @param itemWidth 表示一个课占多宽
         * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
         * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
         */
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object p0) {
            return false;
        }
    }
    
    /**
     * 为了记录touchView的位置
     * @param
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleView$TouchPositionData;", "", "position", "", "(I)V", "getPosition", "()I", "setPosition", "component1", "copy", "equals", "", "other", "hashCode", "toString", "", "module_course_debug"})
    public static final class TouchPositionData {
        private int position;
        
        public final int getPosition() {
            return 0;
        }
        
        public final void setPosition(int p0) {
        }
        
        public TouchPositionData(int position) {
            super();
        }
        
        public final int component1() {
            return 0;
        }
        
        /**
         * 为了记录touchView的位置
         * @param
         */
        @org.jetbrains.annotations.NotNull()
        public final com.mredrock.cyxbs.course.component.ScheduleView.TouchPositionData copy(int position) {
            return null;
        }
        
        /**
         * 为了记录touchView的位置
         * @param
         */
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        /**
         * 为了记录touchView的位置
         * @param
         */
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        /**
         * 为了记录touchView的位置
         * @param
         */
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object p0) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleView$Companion;", "", "()V", "CLICK_RESPONSE_DISTANCE", "", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}