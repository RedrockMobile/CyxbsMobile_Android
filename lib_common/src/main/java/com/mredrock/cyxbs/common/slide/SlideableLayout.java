package com.mredrock.cyxbs.common.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.mredrock.cyxbs.common.BaseApp;
import com.mredrock.cyxbs.common.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 处理向右滑动的逻辑，移动整个View，请参数{@link SlidingPaneLayout}
 *
 */
public class SlideableLayout extends ViewGroup {

    /**
     * 滑动的监听器
     */
    public interface SlidingListener {

        /**
         * @see {@link ViewDragHelper.Callback#onViewDragStateChanged(int)}
         */
        void onSlideStateChanged(int state);

        /**
         * 当View滑动时调用
         *
         * @param panel       view
         * @param slideOffset [0, 1]
         */
        void onPanelSlide(View panel, float slideOffset);

        /**
         * @param panel    panel
         * @param settling settling
         */
        void continueSettling(View panel, boolean settling);
    }

    public static class SlidingAdapter implements SlidingListener {

        @Override
        public void onSlideStateChanged(int state) {
        }

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
        }

        @Override
        public void continueSettling(View panel, boolean settling) {
        }
    }

    private int mGravity = Gravity.START;

    /**
     * dips per second
     */
    private static final int MIN_FLING_VELOCITY_HORIZONTAL = 400;
    private static final int MIN_FLING_VELOCITY_VERTICAL = 15;

    /**
     * 默认触发滑动边缘的size，单位dp, 小于0时表示全屏都能触发滑动
     */
    private static final int DEFAULT_EDGE_SIZE = -1;// 建议30dp

    /**
     * 默认阴影的资源
     */
    private static final int DEFAULT_SHADOW_RES_LEFT = R.drawable.common_shape_sliding_back_shadow;
    private static final int DEFAULT_SHADOW_RES_TOP = R.drawable.common_shape_sliding_back_shadow_top;

    /**
     * 边缘的阴影drawable
     */
    private Drawable mShadowDrawable;

    /**
     * 是否可以滑动
     */
    private boolean mCanSlide = true;

    /**
     * 可滑动的View
     */
    private View mSlideableView;

    /**
     * 滑动的偏移比例
     */
    private float mSlideOffset;

    /**
     * 滑动的范围
     */
    private int mSlideRange;

    /**
     * 是否不能拖拽
     */
    private boolean mIsUnableToDrag;

    /**
     * 初始的X
     */
    private float mInitialMotionX;

    /**
     * 初始的Y
     */
    private float mInitialMotionY;

    /**
     * 可以响应滑动事件的边缘大小，如果按下的坐标的x大于这个值的话，就不会响应滑动事件
     */
    private float mEdgeSize;

    /**
     * 滑动的listener
     */
    private List<SlidingListener> mSlidingListeners;

    /**
     * ViewDragHelper
     */
    private final ViewDragHelper mDragHelper;

    /**
     * Panel是否是打开的状态
     */
    private boolean mPreservedOpenState;

    /**
     * 是否是第一次发生布局
     */
    private boolean mFirstLayout = true;

    /**
     * Rect
     */
    private final Rect mTmpRect = new Rect();

    /**
     * Priview view
     */
    private final PreviewView mPreviousSnapshotView;

    /**
     * Runnable集合
     */
    private final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList<>();

    /**
     * 是否绘制左侧阴影
     */
    private boolean mDrawShadow = false;

    /**
     * SlidingPanelLayoutImpl
     */
    private static final SlidingPanelLayoutImpl IMPL;

    /**
     * 新转场动画，前一个activity退出时的缩放比例
     */
    private float mActivityScaleProportion = 0.98f;

    static {
        final int deviceVersion = Build.VERSION.SDK_INT;
        if (deviceVersion >= 17) {
            IMPL = new SlidingPanelLayoutImplJBMR1();
        } else if (deviceVersion >= 16) {
            IMPL = new SlidingPanelLayoutImplJB();
        } else {
            IMPL = new SlidingPanelLayoutImplBase();
        }
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public SlideableLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public SlideableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlideableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        mDragHelper = ViewDragHelper.create(this, 0.5f, new DragHelperCallback());

        setGravity(mGravity);

        mPreviousSnapshotView = new PreviewView(context);
        addView(mPreviousSnapshotView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 添加侧滑监听
     *
     * @param slidingListener
     */
    public void addSlidingListener(SlidingListener slidingListener) {
        if (slidingListener == null) {
            return;
        }
        if (mSlidingListeners == null) {
            mSlidingListeners = new ArrayList<>();
        }
        mSlidingListeners.add(slidingListener);
    }

    public void setActivityTransitionScaleProportion(float proportion) {
        mActivityScaleProportion = proportion;
    }

    /**
     * 移除滑动监听
     *
     * @param slidingListener
     */
    public void removeSlidingListener(SlidingListener slidingListener) {
        if (mSlidingListeners == null || slidingListener == null) {
            return;
        }
        mSlidingListeners.remove(slidingListener);
    }

    /**
     * 设置滑动边缘的大小，用来响应滑动事件
     *
     * @param offset
     */
    public void setEdgeSize(int offset) {
        mEdgeSize = offset;
    }

    /**
     * 是否可以滑动
     *
     * @return
     */
    public boolean isSlideable() {
        return mCanSlide;
    }

    /**
     * 设置是否可以滑动，默认为true
     *
     * @param b true/false
     */
    public void setSlideable(boolean b) {
        if (mCanSlide == b) {
            return;
        }
        mCanSlide = b;
        // 重置一下
        reset();
    }

    /**
     * 设置边缘的阴影的资源
     *
     * @param resId
     */
    public void setShadowResource(int resId) {
        mShadowDrawable = getResources().getDrawable(resId);
    }

    /**
     * 获得滑动范围
     *
     * @return
     */
    public int getSlideRange() {
        return mSlideRange;
    }

    /**
     * 调用滑动的监听器
     *
     * @param panel
     */
    private void dispatchOnPanelSlide(View panel) {
        if (mSlidingListeners != null) {
            for (SlidingListener listener : mSlidingListeners) {
                listener.onPanelSlide(panel, mSlideOffset);
            }
        }

        // 决定是否需要绘制阴影
        if (mSlideOffset > 0 && mSlideOffset < 1) {
            // 拖拽过程中
            mDrawShadow = true;
        } else {
            mDrawShadow = false;
        }
    }

    /**
     * @param panel
     */
    private void updateObscuredViewsVisibility(View panel) {
        final int startBound = getPaddingLeft();
        final int endBound = getWidth() - getPaddingRight();
        final int topBound = getPaddingTop();
        final int bottomBound = getHeight() - getPaddingBottom();
        final int left;
        final int right;
        final int top;
        final int bottom;
        if (panel != null && viewIsOpaque(panel)) {
            left = panel.getLeft();
            right = panel.getRight();
            top = panel.getTop();
            bottom = panel.getBottom();
        } else {
            left = right = top = bottom = 0;
        }

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);

            if (child == panel) {
                // There are still more children above the panel but they won't be affected.
                break;
            }

            final int clampedChildLeft = Math.max(startBound, child.getLeft());
            final int clampedChildTop = Math.max(topBound, child.getTop());
            final int clampedChildRight = Math.min(endBound, child.getRight());
            final int clampedChildBottom = Math.min(bottomBound, child.getBottom());
            final int vis;
            if (clampedChildLeft >= left && clampedChildTop >= top &&
                    clampedChildRight <= right && clampedChildBottom <= bottom) {
                vis = INVISIBLE;
            } else {
                vis = VISIBLE;
            }
            child.setVisibility(vis);
            Log.d("SlideFrameLayout", "updateObscuredViewsVisibility " + child.toString() + " " + vis);
        }
    }

    /**
     * 设置所有的child都可见
     */
    private void setAllChildrenVisible() {
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == INVISIBLE) {
                child.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp 的单位
     * @return px(像素)的单位
     */
    private int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    /**
     * 判断指定的View是否是不透明
     *
     * @param v
     * @return
     */
    private static boolean viewIsOpaque(View v) {
        if (ViewCompat.isOpaque(v)) {
            return true;
        }

        // View#isOpaque didn't take all valid opaque scrollbar modes into account
        // before API 18 (JB-MR2). On newer devices rely solely on isOpaque above and return false
        // here. On older devices, check the view's background drawable directly as a fallback.
        if (Build.VERSION.SDK_INT >= 18) {
            return false;
        }

        final Drawable bg = v.getBackground();
        if (bg != null) {
            return bg.getOpacity() == PixelFormat.OPAQUE;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;

        ArrayList<DisableLayerRunnable> runnables = new ArrayList<>(mPostedRunnables);
        for (DisableLayerRunnable dlr : runnables) {
            dlr.run();
        }

        mPostedRunnables.clear();
    }

    @SuppressWarnings("Range")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(heightMode == MeasureSpec.UNSPECIFIED){
            heightMode = MeasureSpec.AT_MOST;
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            if (isInEditMode()) {
                // Don't crash the layout editor. Consume all of the space if specified
                // or pick a magic number from thin air otherwise.
                // TODO Better communication with tools of this bogus state.
                // It will crash on a real device.
                if (widthMode == MeasureSpec.AT_MOST) {
                    widthMode = MeasureSpec.EXACTLY;
                } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                    widthMode = MeasureSpec.EXACTLY;
                    widthSize = 300;
                }
            } else {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            }
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (isInEditMode()) {
                // Don't crash the layout editor. Pick a magic number from thin air instead.
                // TODO Better communication with tools of this bogus state.
                // It will crash on a real device.
                if (heightMode == MeasureSpec.UNSPECIFIED) {
                    heightMode = MeasureSpec.AT_MOST;
                    heightSize = 300;
                }
            } else {
                throw new IllegalStateException("Height must not be UNSPECIFIED");
            }
        }

        int layoutHeight = 0;
        int maxLayoutHeight = -1;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                layoutHeight = maxLayoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
                break;
            case MeasureSpec.AT_MOST:
                maxLayoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
                break;
            default:
                break;
        }

        float weightSum = 0;
        boolean canSlide = false;
        final int widthAvailable = widthSize - getPaddingLeft() - getPaddingRight();
        int widthRemaining = widthAvailable;
        final int childCount = getChildCount();

        // We'll find the current one below.
        mSlideableView = null;

        // First pass. Measure based on child LayoutParams width/height.
        // Weight will incur a second pass.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getVisibility() == GONE) {
                continue;
            }

            if (lp.weight > 0) {
                weightSum += lp.weight;
                // If we have no width, weight is the only contributor to the final size.
                // Measure this view on the weight pass only.
                if (lp.width == 0) {
                    continue;
                }
            }

            int childWidthSpec;
            final int horizontalMargin = lp.leftMargin + lp.rightMargin;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin,
                        MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.FILL_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthAvailable - horizontalMargin,
                        MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.AT_MOST);
            } else if (lp.height == LayoutParams.FILL_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (heightMode == MeasureSpec.AT_MOST && childHeight > layoutHeight) {
                layoutHeight = Math.min(childHeight, maxLayoutHeight);
            }

            widthRemaining -= childWidth;
            canSlide |= lp.slideable = widthRemaining < 0;
            if (lp.slideable) {
                mSlideableView = child;
            }
        }

        // Resolve weight and make sure non-sliding panels are smaller than the full screen.
        if (canSlide || weightSum > 0) {
            final int fixedPanelWidthLimit = widthAvailable;

            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (child.getVisibility() == GONE) {
                    continue;
                }

                final boolean skippedFirstPass = lp.width == 0 && lp.weight > 0;
                final int measuredWidth = skippedFirstPass ? 0 : child.getMeasuredWidth();
                if (canSlide && child != mSlideableView) {
                    if (lp.width < 0 && (measuredWidth > fixedPanelWidthLimit || lp.weight > 0)) {
                        // Fixed panels in a sliding configuration should
                        // be clamped to the fixed panel limit.
                        final int childHeightSpec;
                        if (skippedFirstPass) {
                            // Do initial height measurement if we skipped measuring this view
                            // the first time around.
                            if (lp.height == LayoutParams.WRAP_CONTENT) {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                        MeasureSpec.AT_MOST);
                            } else if (lp.height == LayoutParams.FILL_PARENT) {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                        MeasureSpec.EXACTLY);
                            } else {
                                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
                                        MeasureSpec.EXACTLY);
                            }
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(
                                    child.getMeasuredHeight(), MeasureSpec.EXACTLY);
                        }
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                                fixedPanelWidthLimit, MeasureSpec.EXACTLY);
                        child.measure(childWidthSpec, childHeightSpec);
                    }
                } else if (lp.weight > 0) {
                    int childHeightSpec;
                    if (lp.width == 0) {
                        // This was skipped the first time; figure out a real height spec.
                        if (lp.height == LayoutParams.WRAP_CONTENT) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                    MeasureSpec.AT_MOST);
                        } else if (lp.height == LayoutParams.FILL_PARENT) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
                                    MeasureSpec.EXACTLY);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
                                    MeasureSpec.EXACTLY);
                        }
                    } else {
                        childHeightSpec = MeasureSpec.makeMeasureSpec(
                                child.getMeasuredHeight(), MeasureSpec.EXACTLY);
                    }

                    if (canSlide) {
                        // Consume available space
                        final int horizontalMargin = lp.leftMargin + lp.rightMargin;
                        final int newWidth = widthAvailable - horizontalMargin;
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                                newWidth, MeasureSpec.EXACTLY);
                        if (measuredWidth != newWidth) {
                            child.measure(childWidthSpec, childHeightSpec);
                        }
                    } else {
                        // Distribute the extra width proportionally similar to LinearLayout
                        final int widthToDistribute = Math.max(0, widthRemaining);
                        final int addedWidth = (int) (lp.weight * widthToDistribute / weightSum);
                        final int childWidthSpec = MeasureSpec.makeMeasureSpec(
                                measuredWidth + addedWidth, MeasureSpec.EXACTLY);
                        child.measure(childWidthSpec, childHeightSpec);
                    }
                }
            }
        }

        final int measuredWidth = widthSize;
        final int measuredHeight = layoutHeight + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measuredWidth, measuredHeight);
        mCanSlide &= canSlide;

        if (mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE && !mCanSlide) {
            // Cancel scrolling in progress, it's no longer relevant.
            safeAbortDrag();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mGravity == Gravity.LEFT) {
            onLayoutLeft(changed, l, t, r, b);
        } else if (mGravity == Gravity.TOP) {
            onLayoutTop(changed, l, t, r, b);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        for (int i = 0, size = getChildCount(); i < size; i++) {
//            View child = getChildAt(i);
////            Log.d("SlideFrameLayout", "dispatchDraw " + child.toString()+" "+child.isOpaque());
//        }
    }

    protected void onLayoutLeft(boolean changed, int l, int t, int r, int b) {
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

        final int width = r - l;
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();

        final int childCount = getChildCount();
        int xStart = paddingLeft;
        int nextXStart = xStart;

        if (mFirstLayout) {
            mSlideOffset = mCanSlide && mPreservedOpenState ? 1.f : 0.f;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childWidth = child.getMeasuredWidth();
            int offset = 0;

            if (lp.slideable) {
                final int margin = lp.leftMargin + lp.rightMargin;
                final int range = Math.min(nextXStart, width - paddingRight) - xStart - margin;
                mSlideRange = range;
                final int lpMargin = lp.leftMargin;
                final int pos = (int) (range * mSlideOffset);
                xStart += pos + lpMargin;
                mSlideOffset = (float) pos / mSlideRange;
            } else {
                xStart = nextXStart;
            }

            final int childLeft = xStart - offset;
            final int childRight = childLeft + childWidth;

            final int childTop = paddingTop;
            final int childBottom = childTop + child.getMeasuredHeight();
            try {
                child.layout(childLeft, paddingTop, childRight, childBottom);
            } catch (Exception e) {
            }
//            Log.d("SlideFrameLayout", "onLayoutLeft " + child.toString());
            nextXStart += child.getWidth();


        }

        if (mFirstLayout) {
            updateObscuredViewsVisibility(mSlideableView);
        }

        mFirstLayout = false;
        tryMoveToEndAndSlideIn();
    }

    protected void onLayoutTop(boolean changed, int l, int t, int r, int b) {
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);

        final int height = b - t;
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        final int childCount = getChildCount();
        int yStart = paddingTop;
        int nextYStart = yStart;

        if (mFirstLayout) {
            mSlideOffset = mCanSlide && mPreservedOpenState ? 1.f : 0.f;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childHeight = child.getMeasuredHeight();
            int offset = 0;

            if (lp.slideable) {
                final int margin = lp.topMargin + lp.bottomMargin;
                final int range = Math.min(nextYStart, height - paddingBottom) - yStart - margin;
                mSlideRange = range;
                final int lpMargin = lp.topMargin;
                final int pos = (int) (range * mSlideOffset);
                yStart += pos + lpMargin;
                mSlideOffset = (float) pos / mSlideRange;
            } else {
                yStart = nextYStart;
            }

            final int childLeft = paddingLeft;
            final int childRight = childLeft + child.getMeasuredWidth();

            final int childTop = yStart - offset;
            final int childBottom = childTop + childHeight;
            child.layout(childLeft, paddingTop, childRight, childBottom);

            nextYStart += child.getHeight();
        }

        if (mFirstLayout) {
            updateObscuredViewsVisibility(mSlideableView);
        }

        mFirstLayout = false;
        tryMoveToEndAndSlideIn();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Recalculate sliding panes and their details
        if (w != oldw) {
            mFirstLayout = true;
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !mCanSlide) {
            mPreservedOpenState = child == mSlideableView;
        }
    }

    MotionEvent actionDown = null;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEdgeSize > 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                    ((mGravity == Gravity.LEFT && ev.getX() > mEdgeSize)
                            || (mGravity == Gravity.TOP && ev.getY() > mEdgeSize))) {
                int dragState = mDragHelper.getViewDragState();
                // 如果正在拖放的话，截断所有事件
                if (dragState == ViewDragHelper.STATE_DRAGGING
                        || dragState == ViewDragHelper.STATE_SETTLING) {
                    return true;
                }

                safeAbortDrag();
                mIsUnableToDrag = true;
                return false;
            }
        }

//        onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);
        // Preserve the open state based on the last view that was touched.
        if (!mCanSlide && action == MotionEvent.ACTION_DOWN && getChildCount() > 1) {
            // After the first things will be slideable.
            final View secondChild = getChildAt(1);
            if (secondChild != null) {
                mPreservedOpenState = !mDragHelper.isViewUnder(secondChild,
                        (int) ev.getX(), (int) ev.getY());
            }
        }

        if (!mCanSlide || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
            safeCancelDrag();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            safeCancelDrag();
            return false;
        }

        boolean interceptTap = false;
        boolean interceptForce = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsUnableToDrag = false;
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;

                if (mDragHelper.isViewUnder(mSlideableView, (int) x, (int) y) &&
                        isDimmed(mSlideableView)) {
                    interceptTap = true;
                }
                actionDown = ev;
//                mDragHelper.processTouchEvent(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
//                if (adx > slop && ady > adx) {
//                    safeCancelDrag();
//                    mIsUnableToDrag = true;
//                    return false;
//                }
                //减少滑动返回误操作率
                if (mGravity == Gravity.LEFT) {
                    if (adx > slop && adx * 0.5f > ady) {
                        //start to drag.
                    } else if (ady > slop) {
                        // The finger has moved enough in the vertical
                        // direction to be counted as a drag...  abort
                        // any attempt to drag horizontally, to work correctly
                        // with children that have scrolling containers.
                        mIsUnableToDrag = true;
                        return false;
                    } else {
                        return false;
                    }
                } else if (mGravity == Gravity.TOP) {
                    if (ady > slop && ady * 0.8f > adx) {
                        //start to drag.
                        interceptForce = !canInnerLayoutScrollVertically(
                                (int) (y - mInitialMotionY),
                                ev.getRawX() + mInitialMotionX - x,
                                ev.getRawY() + mInitialMotionY - y);
                    } else if (adx > slop) {
                        // The finger has moved enough in the vertical
                        // direction to be counted as a drag...  abort
                        // any attempt to drag horizontally, to work correctly
                        // with children that have scrolling containers.
                        mIsUnableToDrag = true;
                        return false;
                    } else {
                        return false;
                    }
                }
            }
        }

        boolean interceptForDrag;
        try {
            interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Throwable ignore) {
            // crash:http://mobile.umeng.com/apps/49a200ac12e85e76a5e55475/error_types/show?error_type_id=57455e5a67e58e21ca002a94_1133930091426665997_5.7.7
            // google issue:https://code.google.com/p/android/issues/detail?id=212945&hl=zh-cn
            // Released in 24.1.0.
            // 所以这里先简单catch下，等以后升级了support-v4包到24.1.0以后就不会有这个问题了。
            interceptForDrag = false;
        }

        return interceptForDrag || interceptTap || interceptForce;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEdgeSize > 0) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                    ((mGravity == Gravity.LEFT && ev.getX() > mEdgeSize)
                            || (mGravity == Gravity.TOP && ev.getY() > mEdgeSize))) {
                return false;
            }
        }

        if (!mCanSlide) {
            return super.onTouchEvent(ev);
        }

        if (actionDown != null) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                actionDown = null;
            } else {
                mDragHelper.processTouchEvent(actionDown);
                final float x = actionDown.getX();
                final float y = actionDown.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                actionDown = null;
            }
        }

        try {
            mDragHelper.processTouchEvent(ev);
        } catch (Throwable ignore) {
            // crash:http://mobile.umeng.com/apps/49a200ac12e85e76a5e55475/error_types/show?error_type_id=57455e5a67e58e21ca002a94_1133930091426665997_5.7.7
            // google issue:https://code.google.com/p/android/issues/detail?id=212945&hl=zh-cn
            // Released in 24.1.0.
            // 所以这里先简单catch下，等以后升级了support-v4包到24.1.0以后就不会有这个问题了。
        }

        final int action = ev.getAction();
        boolean wantTouchEvents = true;

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                break;
            }
        }

        return wantTouchEvents;
    }

    /**
     * 当Panel被拖拽时调用
     *
     */
    private void onPanelDragged(int newStart) {
        if (mSlideableView == null) {
            // This can happen if we're aborting motion during layout because everything now fits.
            mSlideOffset = 0;
            return;
        }

        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

        int paddingStart = 0;
        int lpMargin = 0;
        int startBound = 0;
        if (mGravity == Gravity.LEFT) {
            paddingStart = getPaddingLeft();
            lpMargin = lp.leftMargin;
        } else if (mGravity == Gravity.TOP) {
            paddingStart = getPaddingTop();
            lpMargin = lp.topMargin;
        }
        startBound = paddingStart + lpMargin;

        mSlideOffset = (float) (newStart - startBound) / mSlideRange;

        dispatchOnPanelSlide(mSlideableView);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        boolean result;
        final int save = canvas.save();

        if (mCanSlide && !lp.slideable && mSlideableView != null) {
            // Clip against the slider; no sense drawing what will immediately be covered.
            canvas.getClipBounds(mTmpRect);
            if (mGravity == Gravity.LEFT) {
                mTmpRect.right = Math.min(mTmpRect.right, mSlideableView.getLeft());
            } else if (mGravity == Gravity.TOP) {
                mTmpRect.bottom = Math.min(mTmpRect.bottom, mSlideableView.getTop());
            }
            if (mNeedClipRect) {
                canvas.clipRect(mTmpRect);
            }
        }

//        Log.d("SlideFrameLayout", " drawChild " + mNeedClipRect + " " + mTmpRect + " " + child);
        result = super.drawChild(canvas, child, drawingTime);

        canvas.restoreToCount(save);

        return result;
    }

    /**
     * 刷新
     *
     * @param v
     */
    private void invalidateChildRegion(View v) {
        IMPL.invalidateChildRegion(this, v);
    }

    /**
     * Smoothly animate mDraggingPane to the target X position within its range.
     *
     * @param slideOffset position to animate to
     * @param velocity    initial velocity in case of fling, or 0.
     */
    boolean smoothSlideTo(float slideOffset, int velocity) {
        if (!mCanSlide) {
            // Nothing to do.
            return false;
        }

        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

        int startBound = getPaddingLeft() + lp.leftMargin;
        int x = (int) (startBound + slideOffset * mSlideRange);

        if (mDragHelper.smoothSlideViewTo(mSlideableView, x, mSlideableView.getTop())) {
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private static Field sScrollerOfViewDragHelper;

    private Field getScrollerOfViewDragHelper() {
        if (sScrollerOfViewDragHelper != null) {
            return sScrollerOfViewDragHelper;
        }
        Class<ViewDragHelper> claz = ViewDragHelper.class;
        Field field = null;
        try {
            field = claz.getDeclaredField("scroller");
            field.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
        if (field == null) {
            try {
                field = claz.getDeclaredField("mScroller");
                field.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (field == null) {
            Log.e("SlideFrameLayout", "no scroller filed found");
        }
        sScrollerOfViewDragHelper = field;
        return sScrollerOfViewDragHelper;
    }

    private boolean smoothSlideTo(float slideOffset, int velocity, int duration) {
        if (!mCanSlide) {
            // Nothing to do.
            return false;
        }
        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();
        int startBound = getPaddingLeft() + lp.leftMargin;
        int finalLeft = (int) (startBound + slideOffset * mSlideRange);
        int finalTop = mSlideableView.getTop();
        int startLeft = mSlideableView.getLeft();
        int startTop = mSlideableView.getTop();
        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;
        if (mDragHelper.smoothSlideViewTo(mSlideableView, finalLeft, finalTop)) {
            if (duration > 0) {
                Field scrollerField = getScrollerOfViewDragHelper();
                if (scrollerField != null) {
                    try {
                        Object scroller = scrollerField.get(mDragHelper);
                        if (scroller instanceof OverScroller) {
                            OverScroller scrollerCompat = (OverScroller) scroller;
                            scrollerCompat.startScroll(startLeft, startTop, dx, dy, duration);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            setAllChildrenVisible();
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    public interface SlideInFinishCallback {
        /**
         * 滑入动画结束
         */
        void onSlideInFinish();
    }

    private boolean isSlideIn = false;
    private OverScroller mOldScroller;

    private OverScroller getScroller() {
        if (mDragHelper == null) {
            return null;
        }
        Field field = getScrollerOfViewDragHelper();
        if (field != null) {
            try {
                return (OverScroller) field.get(mDragHelper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void setScroller(OverScroller scrollerCompat) {
        if (scrollerCompat == null || mDragHelper == null) {
            return;
        }
        Field field = getScrollerOfViewDragHelper();
        if (field != null) {
            try {
                field.set(mDragHelper, scrollerCompat);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean mMoveToRightAndSlideIn = false;
    private SlideInParams mSlideInParams;

    private static class SlideInParams {
        SlideInFinishCallback slideInFinishCallback;
        int duration;
        Interpolator interpolator;
    }

    /**
     * 把当前位置移动到最右边
     */
    public void moveToRightAndSlideIn(final SlideInFinishCallback slideInFinishCallback,
                                      int duration, Interpolator interpolator) {
        if (!mCanSlide) {
            return;
        }
        mMoveToRightAndSlideIn = true;

        mSlideInParams = new SlideInParams();
        mSlideInParams.interpolator = interpolator;
        mSlideInParams.duration = duration;
        mSlideInParams.interpolator = interpolator;

        tryMoveToEndAndSlideIn();
    }

    private void tryMoveToEndAndSlideIn() {
        if (mMoveToRightAndSlideIn && mSlideableView != null &&
                mSlideableView.getLayoutParams() != null && mSlideRange > 0 && mPreviousSnapshotView != null) {
            mMoveToRightAndSlideIn = false;

            if (mGravity == Gravity.LEFT) {
                final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();
                int startBound = getPaddingLeft() + lp.leftMargin;
                int endBound = startBound + mSlideRange;
                int offset = endBound - mSlideableView.getLeft();
                mSlideableView.offsetLeftAndRight(offset);
                post(new Runnable() {
                    @Override
                    public void run() {
                        smoothSlideInFromRight(mSlideInParams.slideInFinishCallback, mSlideInParams.duration, mSlideInParams.interpolator);
                        mSlideInParams = null;
                    }
                });
            } else if (mGravity == Gravity.TOP) {
                final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();
                int startBound = getPaddingTop() + lp.topMargin;
                int endBound = startBound + mSlideRange;
                int offset = endBound - mSlideableView.getTop();
                mSlideableView.offsetLeftAndRight(offset);
                post(new Runnable() {
                    @Override
                    public void run() {
                        smoothSlideInFromBottom(mSlideInParams.slideInFinishCallback, mSlideInParams.duration, mSlideInParams.interpolator);
                        mSlideInParams = null;
                    }
                });
            }
        }
    }

    /**
     * 从右边平滑进入
     *
     * @param slideInFinishCallback 滑动进入动画结束的回调
     * @param interpolator
     */
    public void smoothSlideInFromRight(final SlideInFinishCallback slideInFinishCallback,
                                       int duration, Interpolator interpolator) {
        if (!mCanSlide || mSlideRange <= 0 || mSlideableView == null || mPreviousSnapshotView == null) {
            return;
        }
        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();
        int startBound = getPaddingLeft() + lp.leftMargin;
        int endBound = startBound + mSlideRange;
        //先滑动到最右边
        int offset = endBound - mSlideableView.getLeft();
        mSlideableView.offsetLeftAndRight(offset);
        mPreviousSnapshotView.enableShadowColor = false;
        //然后动画从最右边进入，平滑滑动到左边的位置
        isSlideIn = true;
        if (interpolator != null) {
            OverScroller newScroller = new OverScroller(BaseApp.Companion.getContext(), interpolator);
            mOldScroller = getScroller();
            if (mOldScroller != null) {
                setScroller(newScroller);
                addSlidingListener(new SlidingAdapter() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        if (state == ViewDragHelper.STATE_IDLE) {
                            Log.w("SlideFrameLayout", "slide in animation is over");
                            if (mSlidingListeners != null && mSlidingListeners.contains(this)) {
                                removeSlidingListener(this);
                                if (mOldScroller != null) {
                                    setScroller(mOldScroller);
                                }
                            }
                        }
                    }
                });
            }
        }
        smoothSlideTo(0f, 0, duration);
        if (slideInFinishCallback != null) {
            final SlidingListener listener = new SlidingAdapter() {
                @Override
                public void onSlideStateChanged(int state) {
                    if (state == ViewDragHelper.STATE_IDLE) {
                        onSlideInFinish(this, slideInFinishCallback);
                    }
                }
            };
            addSlidingListener(listener);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果一段时间之后还没有回调slideInFinishCallback，则手动调用一下
                    onSlideInFinish(listener, slideInFinishCallback);
                }
            }, 500);
        }
    }

    /**
     * 从右边平滑进入
     *
     * @param slideInFinishCallback 滑动进入动画结束的回调
     * @param interpolator
     */
    public void smoothSlideInFromBottom(final SlideInFinishCallback slideInFinishCallback,
                                        int duration, Interpolator interpolator) {
        if (!mCanSlide || mSlideRange <= 0 || mSlideableView == null || mPreviousSnapshotView == null) {
            return;
        }
        final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();
        int startBound = getPaddingTop() + lp.topMargin;
        int endBound = startBound + mSlideRange;
        //先滑动到最右边
        int offset = endBound - mSlideableView.getTop();
        mSlideableView.offsetTopAndBottom(offset);
        mPreviousSnapshotView.enableShadowColor = false;
        //然后动画从最右边进入，平滑滑动到左边的位置
        isSlideIn = true;
        if (interpolator != null) {
            OverScroller newScroller = new OverScroller(BaseApp.Companion.getContext(), interpolator);
            mOldScroller = getScroller();
            if (mOldScroller != null) {
                setScroller(newScroller);
                addSlidingListener(new SlidingAdapter() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        if (state == ViewDragHelper.STATE_IDLE) {
                            Log.w("SlideFrameLayout", "slide in animation is over");
                            if (mSlidingListeners != null && mSlidingListeners.contains(this)) {
                                removeSlidingListener(this);
                                if (mOldScroller != null) {
                                    setScroller(mOldScroller);
                                }
                            }
                        }
                    }
                });
            }
        }
        smoothSlideTo(0f, 0, duration);
        if (slideInFinishCallback != null) {
            final SlidingListener listener = new SlidingAdapter() {
                @Override
                public void onSlideStateChanged(int state) {
                    if (state == ViewDragHelper.STATE_IDLE) {
                        onSlideInFinish(this, slideInFinishCallback);
                    }
                }
            };
            addSlidingListener(listener);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果一段时间之后还没有回调slideInFinishCallback，则手动调用一下
                    onSlideInFinish(listener, slideInFinishCallback);
                }
            }, 500);
        }
    }

    private void onSlideInFinish(SlidingListener listener, SlideInFinishCallback slideInFinishCallback) {
        if (mSlidingListeners != null && mSlidingListeners.contains(listener)) {
            removeSlidingListener(listener);
            mPreviousSnapshotView.enableShadowColor = true;
            isSlideIn = false;
            slideInFinishCallback.onSlideInFinish();
        }
    }

    @Override
    public void computeScroll() {
        boolean settling = mDragHelper.continueSettling(true);
        if (mSlidingListeners != null) {
            for (SlidingListener listener : mSlidingListeners) {
                listener.continueSettling(this, settling);
            }
        }

        if (settling) {
            if (!mCanSlide) {
                safeAbortDrag();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        drawShadow(c);
    }

    /**
     * Draw the left side shadow drawable
     *
     * @param canvas canvas
     */
    protected void drawShadow(Canvas canvas) {
        // 不能滑动时，没必要绘制阴影  或者是进入动画的过程中，也不画阴影
        if (!mCanSlide || isSlideIn) {
            return;
        }

        if (!mDrawShadow) {
            return;
        }

        if (mShadowDrawable == null) {
            return;
        }

        final View shadowView = getChildCount() > 1 ? getChildAt(1) : null;
        if (shadowView == null) {
            // No need to draw a shadow if we don't have one.
            return;
        }

        if (mGravity == Gravity.LEFT) {
            final int top = shadowView.getTop();
            final int bottom = shadowView.getBottom();

            final int shadowWidth = mShadowDrawable.getIntrinsicWidth();
            final int right = shadowView.getLeft();
            final int left = right - shadowWidth;

            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(canvas);
        } else if (mGravity == Gravity.TOP) {
            final int shadowHeight = mShadowDrawable.getIntrinsicHeight();
            final int right = shadowView.getLeft();
            final int left = shadowView.getRight();
            final int bottom = shadowView.getBottom();
            final int top = bottom - shadowHeight;

            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(canvas);
        }
    }

    boolean isDimmed(View child) {
        if (child == null) {
            return false;
        }
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return mCanSlide && lp.dimWhenOffset && mSlideOffset > 0;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void safeCancelDrag() {
        try {
            actionDown = null;
            mDragHelper.cancel();
        } catch (Throwable ignore) {
        }
    }

    private void safeAbortDrag() {
        try {
            mDragHelper.abort();
        } catch (Throwable ignore) {
        }
    }

    /**
     * 重置，还原到初始状态
     */
    public void reset() {
        mPreservedOpenState = false;
        mFirstLayout = true;
        mSlideOffset = 0.0f;
        safeAbortDrag();
        requestLayout();
        dispatchOnPanelSlide(mSlideableView);
    }

    /**
     * 移动上一个界面的截图
     *
     * @param snapshotView 上一个界面view
     * @param translateX   平移位移
     * @param background   快照的背景
     */
    public void offsetPreviousSnapshot(View snapshotView, float translateX, float translateY, Drawable background) {
        if (mPreviousSnapshotView != null) {
            // view变化是时候才重新设置background
            if (mPreviousSnapshotView.getHostView() != snapshotView) {
                final Drawable.ConstantState constantState = background != null ? background.getConstantState() : null;
                if (constantState != null) {
                    background = constantState.newDrawable(snapshotView != null ? snapshotView.getResources() : getResources());
                }
                mPreviousSnapshotView.setBackgroundDrawable(background);
            }
            mPreviousSnapshotView.setHostView(snapshotView);
            mPreviousSnapshotView.invalidate();
            mPreviousSnapshotView.setTranslationX(translateX);
            mPreviousSnapshotView.setTranslationY(translateY);
        }
    }

    /**
     * 移动上一个界面的截图
     *
     * @param translateX 平移位移
     * @param background 快照的背景
     */
    public void offsetPreviousSnapshot(float translateX, float translateY, Drawable background) {
        if (mPreviousSnapshotView != null) {
            final Drawable.ConstantState constantState = background != null ? background.getConstantState() : null;
            if (constantState != null) {
                background = constantState.newDrawable(getResources());
            }
            mPreviousSnapshotView.setBackgroundDrawable(background);
            mPreviousSnapshotView.invalidate();
            mPreviousSnapshotView.setTranslationX(translateX);
            mPreviousSnapshotView.setTranslationY(translateY);
        }
    }


    OnDragFinishListener onDragFinishListener;

    public interface OnDragFinishListener {
        void onDragCancel();

        void onDragOk(float offset);
    }

    public void setOnDragFinishListener(OnDragFinishListener onDragFinishListener) {
        this.onDragFinishListener = onDragFinishListener;
    }

    /**
     * ViewDragHelper.Callback implementation
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (mIsUnableToDrag) {
                return false;
            }

            return ((LayoutParams) child.getLayoutParams()).slideable;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                if (mSlideOffset == 0) {
                    updateObscuredViewsVisibility(mSlideableView);
                    mPreservedOpenState = false;
                } else {
                    mPreservedOpenState = true;
                }
            }

            if (mSlidingListeners != null) {
                List<SlidingListener> copy = new ArrayList<>(mSlidingListeners);
                for (SlidingListener listener : copy) {
                    listener.onSlideStateChanged(state);
                }
            }
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            // Make all child views visible in preparation for sliding things around
            setAllChildrenVisible();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            if (mGravity == Gravity.LEFT) {
                onPanelDragged(left);
            } else if (mGravity == Gravity.TOP) {
                onPanelDragged(top);
            }
            invalidate();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            final LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            if (mGravity == Gravity.LEFT) {
                mSlideOffset = Math.max(Math.abs(releasedChild.getLeft() / (float) mSlideRange), Math.abs(releasedChild.getTop() / (float) mSlideRange));
                int left = getPaddingLeft() + lp.leftMargin;
                if (isReleaseToFinishPage(xvel) || isReleaseToFinishPage(yvel)) {
                    left += mSlideRange;
                    if (onDragFinishListener != null) {
                        onDragFinishListener.onDragOk(mSlideOffset);
                    } else {
                        mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
                    }
                } else {
                    if (onDragFinishListener != null) {
                        onDragFinishListener.onDragCancel();
                    }
                    mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
                }

            } else if (mGravity == Gravity.TOP) {
                int top = getPaddingTop() + lp.topMargin;
                if (isReleaseToFinishPage(xvel) || isReleaseToFinishPage(yvel)) {
                    top += mSlideRange;
                    if (onDragFinishListener != null) {
                        onDragFinishListener.onDragOk(mSlideOffset);
                    } else {
                        mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
                    }
                } else {
                    if (onDragFinishListener != null) {
                        onDragFinishListener.onDragCancel();
                    }
                    mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
                }

            }

            invalidate();
        }

        private boolean isReleaseToFinishPage(float xvel) {
            return xvel > 0 || (xvel == 0 && mSlideOffset > 0.25f);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if (mGravity == Gravity.LEFT) {
                return mSlideRange;
            } else {
                return super.getViewHorizontalDragRange(child);
            }
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if (mGravity == Gravity.TOP) {
                return mSlideRange;
            } else {
                return super.getViewHorizontalDragRange(child);
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (mGravity == Gravity.LEFT) {
                final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

                int startBound = getPaddingLeft() + lp.leftMargin;
                int endBound = startBound + mSlideRange;
                final int newLeft = Math.min(Math.max(left, startBound), endBound);
                return newLeft;
            } else {
                // Make sure we never move views horizontally.
                // This could happen if the child has less height than its parent.
                return child.getLeft();
            }
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (mGravity == Gravity.TOP) {
                final LayoutParams lp = (LayoutParams) mSlideableView.getLayoutParams();

                int startBound = getPaddingTop() + lp.topMargin;
                int endBound = startBound + mSlideRange;
                final int newTop = Math.min(Math.max(top, startBound), endBound);
                return newTop;
            } else {
                // Make sure we never move views vertically.
                // This could happen if the child has less height than its parent.
                return child.getTop();
            }
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mSlideableView, pointerId);
        }
    }

    /**
     * Layout parameter
     */
    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{
                android.R.attr.layout_weight
        };

        /**
         * The weighted proportion of how much of the leftover space this child should consume after measurement.
         */
        public float weight = 0;

        /**
         * True if this pane is the slideable pane in the layout.
         */
        boolean slideable;
        /**
         * True if this view should be drawn dimmed
         * when it's been offset from its default position.
         */
        boolean dimWhenOffset;

        Paint dimPaint;


        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.weight = source.weight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, ATTRS);
            this.weight = a.getFloat(0, 0);
            a.recycle();
        }

    }

    interface SlidingPanelLayoutImpl {
        void invalidateChildRegion(SlideableLayout parent, View child);
    }

    static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
        public void invalidateChildRegion(SlideableLayout parent, View child) {
            ViewCompat.postInvalidateOnAnimation(parent, child.getLeft(), child.getTop(),
                    child.getRight(), child.getBottom());
        }
    }

    static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
        /*
         * Private API hacks! Nasty! Bad!
         *
         * In Jellybean, some optimizations in the hardware UI renderer
         * prevent a changed Paint on a View using a hardware layer from having
         * the intended effect. This twiddles some internal bits on the view to force
         * it to recreate the display list.
         */
        private Method mGetDisplayList;
        private Field mRecreateDisplayList;

        SlidingPanelLayoutImplJB() {
            try {
                mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
            try {
                mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                mRecreateDisplayList.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
        }

        @Override
        public void invalidateChildRegion(SlideableLayout parent, View child) {
            if (mGetDisplayList != null && mRecreateDisplayList != null) {
                try {
                    mRecreateDisplayList.setBoolean(child, true);
                    mGetDisplayList.invoke(child, (Object[]) null);
                } catch (Exception e) {
                }
            } else {
                // Slow path. REALLY slow path. Let's hope we don't get here.
                child.invalidate();
                return;
            }
            super.invalidateChildRegion(parent, child);
        }
    }

    static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
        @Override
        public void invalidateChildRegion(SlideableLayout parent, View child) {
            ViewCompat.setLayerPaint(child, ((LayoutParams) child.getLayoutParams()).dimPaint);
        }
    }

    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            final AccessibilityNodeInfoCompat superNode = AccessibilityNodeInfoCompat.obtain(info);
            super.onInitializeAccessibilityNodeInfo(host, superNode);
            copyNodeInfoNoChildren(info, superNode);
            superNode.recycle();

            info.setClassName(SlideableLayout.class.getName());
            info.setSource(host);

            final ViewParent parent = ViewCompat.getParentForAccessibility(host);
            if (parent instanceof View) {
                info.setParent((View) parent);
            }

            // This is a best-approximation of addChildrenForAccessibility()
            // that accounts for filtering.
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (!filter(child) && (child.getVisibility() == View.VISIBLE)) {
                    // Force importance to "yes" since we cano't read the value.
                    ViewCompat.setImportantForAccessibility(
                            child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    info.addChild(child);
                }
            }
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);

            event.setClassName(SlideableLayout.class.getName());
        }

        @Override
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
                                                       AccessibilityEvent event) {
            if (!filter(child)) {
                return super.onRequestSendAccessibilityEvent(host, child, event);
            }
            return false;
        }

        public boolean filter(View child) {
            return false;
        }

        /**
         * This should really be in AccessibilityNodeInfoCompat, but there unfortunately seem to be a few elements that
         * are not easily cloneable using the underlying API. Leave it private here as it's not general-purpose useful.
         */
        private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat dest,
                                            AccessibilityNodeInfoCompat src) {
            final Rect rect = mTmpRect;

            src.getBoundsInParent(rect);
            dest.setBoundsInParent(rect);

            src.getBoundsInScreen(rect);
            dest.setBoundsInScreen(rect);

            dest.setVisibleToUser(src.isVisibleToUser());
            dest.setPackageName(src.getPackageName());
            dest.setClassName(src.getClassName());
            dest.setContentDescription(src.getContentDescription());

            dest.setEnabled(src.isEnabled());
            dest.setClickable(src.isClickable());
            dest.setFocusable(src.isFocusable());
            dest.setFocused(src.isFocused());
            dest.setAccessibilityFocused(src.isAccessibilityFocused());
            dest.setSelected(src.isSelected());
            dest.setLongClickable(src.isLongClickable());

            dest.addAction(src.getActions());

            dest.setMovementGranularities(src.getMovementGranularities());
        }
    }

    private class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View childView) {
            mChildView = childView;
        }

        @Override
        public void run() {
            if (mChildView.getParent() == SlideableLayout.this) {
                mChildView.setLayerType(View.LAYER_TYPE_NONE, null);
                invalidateChildRegion(mChildView);
            }

            mPostedRunnables.remove(this);
        }
    }

    private Drawable mCustomDrawable;

    public void setCustomPreviewDrawable(Drawable customDrawable) {
        this.mCustomDrawable = customDrawable;
    }

    public void onFinished() {
        if (mPreviousSnapshotView != null) { // 侧滑结束，恢复受保护的view的可见状态
            if (isInUIThread()) {
                mPreviousSnapshotView.restoreProtectSurface();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mPreviousSnapshotView.restoreProtectSurface();
                    }
                });
            }
        }
    }

    /**
     * Preview custom view
     */
    private class PreviewView extends View {
        // 都是主线程访问，线程不安全的WeakHashMap应该也没问题
        private WeakHashMap<View, Integer> goneViews = new WeakHashMap<>();

        private WeakReference<View> mHostView = new WeakReference<>(null);
        private Paint maskPaint = new Paint();
        private int SHADOW_COLOR_ALPHA = 0x88;
        private boolean enableShadowColor = true;

        public PreviewView(Context context) {
            super(context);
        }

        /**
         * 设置要绘制的view
         *
         * @param view view
         */
        public void setHostView(View view) {
            if (mHostView.get() == view) {
                return;
            }
            mHostView.clear();
            mHostView = new WeakReference<>(view);
        }

        /**
         * 获得宿主view
         *
         * @return
         */
        public View getHostView() {
            return mHostView.get();
        }

        @Override
        public void draw(Canvas canvas) {
//            Log.d("SlideFrameLayout" ,"PreviewView draw");
            if ((mSlideOffset <= 0.0f || !mCanSlide) && !mForceDrawPreview) {
                if (mHostView.get() != null) {
                    mHostView.clear();
                }
                return;
            }
            try {
                final View view = mHostView.get();
                if (view != null) {
                    super.draw(canvas);
                    protectSurface(view);
                    if (!mForceDrawPreview) {

//                        canvas.drawColor(Color.BLACK);
                        canvas.save();
                        // mslideoffset 滑动进度
                        float canvasScaleProportion = mActivityScaleProportion + mSlideOffset * (1 - mActivityScaleProportion);
                        int width = getWidth();
                        int height = getHeight();
                        float px = (1 - canvasScaleProportion) * width / 2;
                        float py = (1 - canvasScaleProportion) * height / 2;
                        canvas.scale(canvasScaleProportion, canvasScaleProportion);
                        canvas.translate(px, py);
//                        canvas.scale(canvasScaleProportion, canvasScaleProportion, px, py);
                        view.draw(canvas);
                        canvas.restore();
                        int color = getMaskColor(1 - mSlideOffset);
                        maskPaint.setColor(color);
                        canvas.drawRect(new Rect(0, 0, width, height), maskPaint);
                    } else if (mForceDrawPreview) {//图集上下滑动触发
//                        canvas.drawColor(Color.BLACK);
                        float canvasScaleProportion = mActivityScaleProportion + mVerticalDragOffset * (1 - mActivityScaleProportion);
                        int width = getWidth();
                        int height = getHeight();
                        float px = (1 - canvasScaleProportion) * width / 2;
                        float py = (1 - canvasScaleProportion) * height / 2;
                        canvas.scale(canvasScaleProportion, canvasScaleProportion);
                        canvas.translate(px, py);
//                        canvas.scale(canvasScaleProportion, canvasScaleProportion, px, py);
                        view.draw(canvas);
                    } else {
                        view.draw(canvas);
                    }
                } else if (mCustomDrawable != null) {
                    if (!mForceDrawPreview) {
//                        canvas.drawColor(Color.BLACK);
                        canvas.save();
                        // mslideoffset 滑动进度
                        float canvasScaleProportion = mActivityScaleProportion + mSlideOffset * (1 - mActivityScaleProportion);
                        int width = getWidth();
                        int height = getHeight();
                        float px = (1 - canvasScaleProportion) * width / 2;
                        float py = (1 - canvasScaleProportion) * height / 2;
                        canvas.scale(canvasScaleProportion, canvasScaleProportion);
                        canvas.translate(px, py);
//                        canvas.scale(canvasScaleProportion, canvasScaleProportion, px, py);
                        mCustomDrawable.setBounds(0, 0, width, height);
                        mCustomDrawable.draw(canvas);
                        canvas.restore();
                        int color = getMaskColor(1 - mSlideOffset);
                        maskPaint.setColor(color);
                        canvas.drawRect(new Rect(0, 0, width, height), maskPaint);

                    }
                }
            } catch (Throwable ignore) {
            }
        }


        /**
         * 有些包含surface的view在onPause时，surface被回收，draw时在有些版本的机型上会native crash
         * 递归遍历所有的子view，如果是TextureView并且surface不存在了且是可见状态，则置为GONE，等侧滑完成后再恢复成VISIBLE状态
         *
         * @param view
         */
        protected void protectSurface(View view) {
            if (!shouldProtectView() || view == null) {
                return;
            }
            if (view instanceof TextureView) {
                if (view.getVisibility() != GONE && ((TextureView) view).getSurfaceTexture() == null) {
                    // 将被我们置为GONE的VISIBLE和INVISIBLE的view的WeakReference保存起来，同时保存其visibility
                    goneViews.put(view, view.getVisibility());
                    setViewVisibility(view, GONE);
                }
            } else if (view instanceof ViewGroup) {
                int count = ((ViewGroup) view).getChildCount();
                for (int i = 0; i < count; i++) {
                    protectSurface(((ViewGroup) view).getChildAt(i));
                }
            }
        }

        protected void restoreProtectSurface() {
            if (!shouldProtectView()) {
                return;
            }
            for (Map.Entry<View, Integer> entry : goneViews.entrySet()) {
                View view = entry.getKey();
                if (view != null && view.getVisibility() == GONE) {
                    setViewVisibility(view, entry.getValue());
                }
            }
        }

        protected boolean shouldProtectView() {
            return (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                    || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1
                    || Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
        }


        private int getMaskColor(float ratio) {
            int alpha;
            if (enableShadowColor) {
                alpha = onDragFinishListener != null ? (int) (ratio * 255) : (int) (SHADOW_COLOR_ALPHA * ratio);
            } else {
                alpha = 0;
            }
            return Color.argb(alpha, 0, 0, 0);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mHostView.get() != null) {
                mHostView.clear();
            }
        }
    }

    private boolean mNeedClipRect = true;
    private boolean mForceDrawPreview = false;
    private float mVerticalDragOffset = 0;

    public void setForceDrawPreview(boolean forceDrawPreview) {
        mForceDrawPreview = forceDrawPreview;
    }

    public void setNeedClipRect(boolean needClip) {
        mNeedClipRect = needClip;
    }

    /**
     * view draw
     */
    public void forceDrawPreviewView(float offset, boolean auto) {
        mForceDrawPreview = true;
        if (offset > 1) {
            offset = 1;
        }
        if (mForceDrawPreview && auto && offset <= mVerticalDragOffset) {
            return;
        }
        mVerticalDragOffset = offset;
        if (mPreviousSnapshotView != null) {
            mPreviousSnapshotView.invalidate();
        }
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        if (mGravity == Gravity.TOP) {
            mDragHelper.setMinVelocity(dip2px(MIN_FLING_VELOCITY_VERTICAL));
            setEdgeSize(DEFAULT_EDGE_SIZE < 0 ? getResources().getDisplayMetrics().heightPixels : dip2px(DEFAULT_EDGE_SIZE));
            setShadowResource(DEFAULT_SHADOW_RES_TOP);
        } else if (mGravity == Gravity.LEFT) {
            mDragHelper.setMinVelocity(dip2px(MIN_FLING_VELOCITY_HORIZONTAL));
            setEdgeSize(DEFAULT_EDGE_SIZE < 0 ? getResources().getDisplayMetrics().widthPixels : dip2px(DEFAULT_EDGE_SIZE));
            setShadowResource(DEFAULT_SHADOW_RES_LEFT);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private WeakReference<ScrollableListener> mScrollableListener;

    /**
     * 用一种比较脏的方法实现了竖屏嵌套滑动的处理，考虑换成 NestedScrollView
     *
     * @param scrollableListener
     */
    public void setScrollableListener(ScrollableListener scrollableListener) {
        mScrollableListener = new WeakReference<>(scrollableListener);
    }

    private boolean canInnerLayoutScrollVertically(int direction, float x, float y) {
        if (mScrollableListener != null) {
            ScrollableListener listener = mScrollableListener.get();
            return listener != null && listener.canInnerLayoutScrollVertically(direction, x, y);
        }
        return false;
    }

    private boolean canInnerLayoutScrollHorizontally(int direction, float x, float y) {
        if (mScrollableListener != null) {
            ScrollableListener listener = mScrollableListener.get();
            return listener != null && listener.canInnerLayoutScrollHorizontally(direction, x, y);
        }
        return false;
    }

    public interface ScrollableListener {
        boolean canInnerLayoutScrollVertically(int direction, float x, float y);

        boolean canInnerLayoutScrollHorizontally(int direction, float x, float y);
    }


    public final static void setViewVisibility(View v, int visiable) {
        if (v == null || v.getVisibility() == visiable || !visibilityValid(visiable)) {
            return;
        }
        v.setVisibility(visiable);
    }


    private static boolean visibilityValid(int visiable) {
        return visiable == View.VISIBLE || visiable == View.GONE || visiable == View.INVISIBLE;
    }


    public static boolean isInUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
