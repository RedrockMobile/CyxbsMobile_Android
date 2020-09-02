package com.mredrock.cyxbs.course.component;

import java.lang.System;

/**
 * Created by anriku on 2018/8/21.
 * 描述：该View使用viewPager下面添加小点实现
 * ViewPager 默认顶满父布局
 * ViewPager 的 item 默认顶满父布局
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001:\u0005!\"#$%B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\u0018\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001bH\u0014J\u0010\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J\u0010\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u001f\u001a\u00020 H\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R(\u0010\u0012\u001a\u0004\u0018\u00010\u00112\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016\u00a8\u0006&"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView;", "Landroid/widget/RelativeLayout;", "mContext", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "mDotsView", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$DotsView;", "mLayoutInflater", "Landroid/view/LayoutInflater;", "mViewPager", "Landroidx/viewpager/widget/ViewPager;", "mViewpagerAdapter", "Landroidx/viewpager/widget/PagerAdapter;", "value", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;", "scheduleDetailViewAdapter", "getScheduleDetailViewAdapter", "()Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;", "setScheduleDetailViewAdapter", "(Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;)V", "initScheduleDetailView", "", "onMeasure", "widthMeasureSpec", "", "heightMeasureSpec", "setMultiContent", "setOneContent", "schedule", "Lcom/mredrock/cyxbs/course/network/Course;", "Adapter", "DotsView", "ScheduleDetailOnPageChangeListener", "ViewPagerAdapter", "WarpHeightViewPager", "module_course_debug"})
public final class ScheduleDetailView extends android.widget.RelativeLayout {
    private androidx.viewpager.widget.ViewPager mViewPager;
    private android.view.LayoutInflater mLayoutInflater;
    private androidx.viewpager.widget.PagerAdapter mViewpagerAdapter;
    private com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView mDotsView;
    @org.jetbrains.annotations.Nullable()
    private com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter scheduleDetailViewAdapter;
    private java.util.HashMap _$_findViewCache;
    
    @org.jetbrains.annotations.Nullable()
    public final com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter getScheduleDetailViewAdapter() {
        return null;
    }
    
    public final void setScheduleDetailViewAdapter(@org.jetbrains.annotations.Nullable()
    com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter value) {
    }
    
    /**
     * This function is used to display the content when schedules' size == 1
     * @param schedule the schedule going to be displayed.
     */
    private final void setOneContent(com.mredrock.cyxbs.course.network.Course schedule) {
    }
    
    /**
     * This function is used to display the the content when schedules' size > 1
     * @param scheduleDetailViewAdapter [ScheduleDetailView] get the data from [Adapter]
     */
    private final void setMultiContent(com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter scheduleDetailViewAdapter) {
    }
    
    private final void initScheduleDetailView() {
    }
    
    @java.lang.Override()
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }
    
    public ScheduleDetailView(@org.jetbrains.annotations.NotNull()
    android.content.Context mContext) {
        super(null);
    }
    
    public ScheduleDetailView(@org.jetbrains.annotations.NotNull()
    android.content.Context mContext, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    /**
     * This Adapter is the adapter of ViewPager displaying the schedules. There use a proxy. Let the
     * [ViewPagerAdapter]'s work done by [Adapter]
     *
     * @param mContext [Context]
     * @param mScheduleDetailViewAdapter [Adapter] which gets the data [ScheduleDetailView] needs.
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ \u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0011H\u0016J\u0018\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u001a"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$ViewPagerAdapter;", "Landroidx/viewpager/widget/PagerAdapter;", "mContext", "Landroid/content/Context;", "mScheduleDetailViewAdapter", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;", "parent", "Landroid/view/ViewGroup;", "(Landroid/content/Context;Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;Landroid/view/ViewGroup;)V", "mLayoutInflater", "Landroid/view/LayoutInflater;", "getParent", "()Landroid/view/ViewGroup;", "destroyItem", "", "container", "position", "", "any", "", "getCount", "instantiateItem", "isViewFromObject", "", "view", "Landroid/view/View;", "module_course_debug"})
    public static final class ViewPagerAdapter extends androidx.viewpager.widget.PagerAdapter {
        private final android.view.LayoutInflater mLayoutInflater = null;
        private final android.content.Context mContext = null;
        private final com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter mScheduleDetailViewAdapter = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.ViewGroup parent = null;
        
        @java.lang.Override()
        public boolean isViewFromObject(@org.jetbrains.annotations.NotNull()
        android.view.View view, @org.jetbrains.annotations.NotNull()
        java.lang.Object any) {
            return false;
        }
        
        @java.lang.Override()
        public int getCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.Object instantiateItem(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup container, int position) {
            return null;
        }
        
        @java.lang.Override()
        public void destroyItem(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup container, int position, @org.jetbrains.annotations.NotNull()
        java.lang.Object any) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.ViewGroup getParent() {
            return null;
        }
        
        public ViewPagerAdapter(@org.jetbrains.annotations.NotNull()
        android.content.Context mContext, @org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.component.ScheduleDetailView.Adapter mScheduleDetailViewAdapter, @org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent) {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0014\u00a8\u0006\n"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$WarpHeightViewPager;", "Landroidx/viewpager/widget/ViewPager;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "onMeasure", "", "widthMeasureSpec", "", "heightMeasureSpec", "module_course_debug"})
    public static final class WarpHeightViewPager extends androidx.viewpager.widget.ViewPager {
        private java.util.HashMap _$_findViewCache;
        
        @java.lang.Override()
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        }
        
        public WarpHeightViewPager(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J \u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0006H\u0016J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0006H\u0016\u00a8\u0006\r"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$ScheduleDetailOnPageChangeListener;", "Landroidx/viewpager/widget/ViewPager$OnPageChangeListener;", "()V", "onPageScrollStateChanged", "", "state", "", "onPageScrolled", "position", "positionOffset", "", "positionOffsetPixels", "onPageSelected", "module_course_debug"})
    public static abstract class ScheduleDetailOnPageChangeListener implements androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        
        @java.lang.Override()
        public void onPageScrollStateChanged(int state) {
        }
        
        @java.lang.Override()
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        
        @java.lang.Override()
        public void onPageSelected(int position) {
        }
        
        public ScheduleDetailOnPageChangeListener() {
            super();
        }
    }
    
    /**
     * This Adapter is used to get the data [ScheduleDetailView] needs.
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H\'J\b\u0010\b\u001a\u00020\u0007H\'J\u000e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH&J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0007H&J\u0018\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH&\u00a8\u0006\u0014"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$Adapter;", "", "addDotsView", "Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$DotsView;", "container", "Landroid/view/ViewGroup;", "getAffairDetailLayout", "", "getCourseDetailLayout", "getSchedules", "", "Lcom/mredrock/cyxbs/course/network/Course;", "setCurrentFocusDot", "", "dotsView", "position", "setScheduleContent", "itemView", "Landroid/view/View;", "itemViewInfo", "module_course_debug"})
    public static abstract interface Adapter {
        
        /**
         * This function is used to set the itemView's View of the [mViewPager].
         *
         * @param itemView The view will be displayed by [mViewPager]
         * @param itemViewInfo The info going to be displayed.
         */
        public abstract void setScheduleContent(@org.jetbrains.annotations.NotNull()
        android.view.View itemView, @org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.network.Course itemViewInfo);
        
        /**
         * This function is used to get the schedules' info
         * @return Schedules going to be displayed.
         */
        @org.jetbrains.annotations.NotNull()
        public abstract java.util.List<com.mredrock.cyxbs.course.network.Course> getSchedules();
        
        /**
         * This function is used to add specific dots.
         * @param container [ScheduleDetailView]
         */
        @org.jetbrains.annotations.NotNull()
        public abstract com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView addDotsView(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup container);
        
        /**
         * This function is used to set the current focused dot.
         *
         * @param dotsView the dotsView
         * @param position The position of the dot.
         */
        public abstract void setCurrentFocusDot(@org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.course.component.ScheduleDetailView.DotsView dotsView, int position);
        
        /**
         * This function is used to get the CourseDetailLayout.
         */
        @androidx.annotation.LayoutRes()
        public abstract int getCourseDetailLayout();
        
        /**
         * This function is used to get the AffairDetailLayout.
         */
        @androidx.annotation.LayoutRes()
        public abstract int getAffairDetailLayout();
    }
    
    /**
     * The View which implement SimpleDotsView should extends the [DotsView].
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0011\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004B\u001b\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007B#\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH&\u00a8\u0006\u000e"}, d2 = {"Lcom/mredrock/cyxbs/course/component/ScheduleDetailView$DotsView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "setCurrentFocusDot", "", "position", "module_course_debug"})
    public static abstract class DotsView extends android.view.View {
        private java.util.HashMap _$_findViewCache;
        
        /**
         * This function is used to set the focus dot.
         * @param position the position of the focus dot.
         */
        public abstract void setCurrentFocusDot(int position);
        
        public DotsView(@org.jetbrains.annotations.Nullable()
        android.content.Context context) {
            super(null);
        }
        
        public DotsView(@org.jetbrains.annotations.Nullable()
        android.content.Context context, @org.jetbrains.annotations.Nullable()
        android.util.AttributeSet attrs) {
            super(null);
        }
        
        public DotsView(@org.jetbrains.annotations.Nullable()
        android.content.Context context, @org.jetbrains.annotations.Nullable()
        android.util.AttributeSet attrs, int defStyleAttr) {
            super(null);
        }
    }
}