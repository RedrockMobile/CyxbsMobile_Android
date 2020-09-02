package com.mredrock.cyxbs.common.slide;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.customview.widget.ViewDragHelper;

import com.mredrock.cyxbs.common.R;

/**
 * 这个Activity实现了可以滑动左侧边缘退出Activity的功能，类似iOS的交互行为。
 * <p>
 * <p>调用 {@link #setSlideable(boolean)} 方法来设置是否支持滑动 </p>
 * <p>调用 {@link #setPreviousActivitySlideFollow(boolean)}
 * 方法来设置前一个activity是否跟随滑动 </p>
 */
@Keep
public abstract class AbsSlideableActivity extends AppCompatActivity implements SlideableLayout.SlidingListener, PageSlideable {

    public static final int SLIDE_TYPE_NORMAL = 0;
    public static final int SLIDE_BOTTOM_UP = 1;
    public static final int SLIDE_BOTTOM_FADE_UP = 2;

    private int mSlideType = SLIDE_TYPE_NORMAL;

    protected boolean mStatusStopped = false;

    /**
     * 侧滑退出监听
     */
    public interface OnSlideFinishListener {
        /**
         * 当退出时调用，如果要中断退出，返回true
         */
        boolean onFinish();
    }


    public interface OnPreRemoveViewListener {
        void onBeforeRemoveViews();
    }

    /**
     * 侧滑绘制监听
     */
    public interface OnSlideDrawListener {
        /**
         * 当可滑动的View绘制的时候调用
         */
        void onSlideableViewDraw();
    }

    /**
     * 侧滑退出功能的总开关
     */
    public static final boolean ENABLE = true;

    /**
     * TAG
     */
    private static final String TAG = "SlideActivity";

    private static final float ACTIVITY_TRANSITION_SCALE_PROPORTION_DEFAULT = 1F;
    private static final float ACTIVITY_TRANSITION_SCALE_PROPORTION_98 = 0.98F;

    /**
     * 后面Activity的预览View的初始偏移量
     */
    private float mBackPreviewViewInitOffset;

    /**
     * 是否可以滑动，默认可以滑动
     */
    private boolean mSlideable = ENABLE;

    /**
     * 后面的Activity是否跟随滑动
     */
    private boolean mPreviousActivitySlideFollow = true;

    /**
     * 前一个界面的activity
     */
    private Activity mPreviousActivity;

    /**
     * 是否需要查找activity，如果前一个activity没有的话，就没必要每次查找
     */
    private boolean mNeedFindActivityFlag = true;

    /**
     * 是否要停止activity
     */
    private boolean mNeedFinishActivityFlag = false;

    /**
     * SlideFrameLayout对象
     */
    protected SlideableLayout mSlideableLayout;

    /**
     * 退出监听
     */
    private OnSlideFinishListener mOnSlideFinishListener;

    /**
     * 在Activity finish之前，先将内容View移除 之前的监听操作
     */
    private OnPreRemoveViewListener mPreRemoveViewListener;

    /**
     * Activity的生命周期回调
     */
    private LifeCycleMonitor mActivityLifecycleCallbacks = new LifeCycleMonitor.Stub() {

        @Override
        public void onDestroy() {
            onPreviousActivityDestroyed();
        }
    };

    /**
     * 结束activity的任务
     */
    private Runnable mFinishTask = new Runnable() {
        @Override
        public void run() {
            mNeedFinishActivityFlag = false;

            // 如果退出监听返回true，中断退出
            if (mOnSlideFinishListener != null && mOnSlideFinishListener.onFinish()) {
                return;
            }

            if (mStatusStopped) {
                return;
            }
            // 1.调用Activity的finish方法
            finish();
//            onBackPressed();

            // 2.禁用动画
            AbsSlideableActivity.super.overridePendingTransition(R.anim.common_translate_none,
                    R.anim.common_translate_none);//8.0上传0会黑屏，用R.anim.none代替
        }
    };

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        if (getRootViewId() != View.NO_ID && view != null && view.getId() == View.NO_ID) {
            view.setId(getRootViewId());
        }
        View contentView = onCreateContentView(view);
        super.setContentView(contentView);
    }

    protected int getRootViewId() {
        return View.NO_ID;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        View contentView = onCreateContentView(view);
//        if (contentView != null  && contentView.getId() == View.NO_ID) {
//            contentView.setId(R.id.content_view_wrapper);
//        }
        super.setContentView(contentView, params);
    }

    /**
     * 安装侧滑装饰布局
     *
     * @param contentView 原始的内容view
     * @return 装饰完后的view
     */

    protected View onCreateContentView(final View contentView) {
//        if (!ENABLE) {
//            return contentView;
//        }
        if (mSlideable) {
            // 如果找不到前一个activity的content view，则不能滑动，典型的场景就是由外部app打开单独的一界面
            // 例如从通知栏中打开消息中心界面，所以可能当前进程就一个消息中心的activity，此时就不能滑动退出
            Pair<View, Activity> previewActivityInfo = getPreviousActivityInfo();
            if (previewActivityInfo == null) {
                mSlideable = false;
            }
        }


        //默认添加mSlideFrameLayout，兼容ISwipeBackContext
        if (!mSlideable) {
            return contentView;
//            return super.onCreateContentView(contentView);
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        mSlideableLayout = new SlideableLayout(this);

        // 初始化
        mSlideableLayout.setSlideable(mSlideable);
        mSlideableLayout.addSlidingListener(this);
        mSlideableLayout.addView(/*super.onCreateContentView(*/contentView/*)*/, SlideableLayout.LayoutParams.MATCH_PARENT, SlideableLayout.LayoutParams.MATCH_PARENT);
        if (scrollableListener != null) {
            mSlideableLayout.setScrollableListener(scrollableListener);
            scrollableListener = null;
        }
        // 设置横竖滑动样式
        updateSlideFrameLayout();

        return mSlideableLayout;
    }

    final public void setSlideType(int type) {
        if (mSlideType == type) {
            return;
        }
        mSlideType = type;
        updateSlideFrameLayout();
    }

    private void updateSlideFrameLayout() {
        if (mSlideableLayout == null) {
            return;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        switch (mSlideType) {
            case SLIDE_TYPE_NORMAL:
                mBackPreviewViewInitOffset = -(1.f / 3) * metrics.widthPixels;
                mSlideableLayout.setActivityTransitionScaleProportion(ACTIVITY_TRANSITION_SCALE_PROPORTION_DEFAULT);
                mSlideableLayout.setGravity(Gravity.LEFT);
                break;
            case SLIDE_BOTTOM_UP:
                mBackPreviewViewInitOffset = -(1.f / 3) * metrics.heightPixels;
                mSlideableLayout.setActivityTransitionScaleProportion(ACTIVITY_TRANSITION_SCALE_PROPORTION_98);
                mSlideableLayout.setGravity(Gravity.TOP);
                break;
            case SLIDE_BOTTOM_FADE_UP:
                mBackPreviewViewInitOffset = -(1.f / 3) * metrics.heightPixels;
                mSlideableLayout.setActivityTransitionScaleProportion(ACTIVITY_TRANSITION_SCALE_PROPORTION_DEFAULT);
                mSlideableLayout.setGravity(Gravity.TOP);
                break;

        }
    }

    @Override
    public void onSlideStateChanged(int state) {
        if (state == ViewDragHelper.STATE_DRAGGING) {//Start to drag
            getSlideFrameLayout().clearFocus();
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (mSlideableLayout == null) {
            return;
        }
        // 记录需要关闭当前界面的flag
        mNeedFinishActivityFlag = slideOffset >= 1.0f;
        if (slideOffset <= 0) {
            offsetPreviousSnapshot(null, 0, 0);
        } else if (slideOffset < 1) {
            switch (mSlideType) {
                case SLIDE_TYPE_NORMAL:
                    offsetPreviousSnapshot(getPreviousActivityInfo(), mBackPreviewViewInitOffset * (1 - slideOffset), 0);
                    break;
                case SLIDE_BOTTOM_UP:
                case SLIDE_BOTTOM_FADE_UP:
                    offsetPreviousSnapshot(getPreviousActivityInfo(), 0, 0);
                    break;
            }
        } else {
            offsetPreviousSnapshot(getPreviousActivityInfo(), 0, 0);

            // Modified by YinZhong 2016/10/11 begin

            // 1. 先将内容View移除，只绘制上一个Activity的快照
            // 避免在finish过程中，偶现白屏闪屏。这里将除快照View以外的其他View都移除掉，这样在finish过程中只绘制快照View，避免闪屏白屏现象。
            final int childCount = mSlideableLayout.getChildCount();
            if (childCount >= 2) {
                if (mPreRemoveViewListener != null) {
                    mPreRemoveViewListener.onBeforeRemoveViews();
                }
                mSlideableLayout.removeViews(1, childCount - 1);
            }

            //
            // FixBug：在某此系统上面（例如YunOS），滑动退出时可能会闪一下，原因是
            // 调用 finish() 方法的时机太早了，当滑动松开手后，当前 activity 上面的 SlideFrameLayout
            // 仍然会继续滑动一段距离，而在这个过程中，SlideFrameLayout 中的绘制上一个 activity 内容的 PreviewView
            // 可能会继续绘制，就会看起来闪烁一下，如果将 PreviewView 背景设置为红色，就会清晰看到这样的效果。
            //
            // 解决办法：在这里先记录需要关闭 activity 的标志量，在滑动结束后 continueSettling(view, boolean) 里面
            // 再执行真正的关闭界面的操作，当然，这里为了确保界面能关闭，做了一个延迟的任务
            // 这个方案目前来看不需要了，使用移除非快照View的所有其他View的方式可以解决

            // 2. 简单的post下，避免调用栈太深，不做延迟
            mSlideableLayout.post(mFinishTask);
            // Modified by YinZhong 2016/10/11 end
        }
    }

    @Override
    public void continueSettling(View panel, boolean settling) {
        // 如果需要关闭 activity 并且滚动结束时
        if (mNeedFinishActivityFlag && !settling && mSlideableLayout != null) {
            mNeedFinishActivityFlag = false;
            // 移除task任务
            mSlideableLayout.removeCallbacks(mFinishTask);
            mSlideableLayout.post(mFinishTask);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    /**
     * 是否可以滑动关闭
     */
    @Override
    public boolean isSlideable() {
        return mSlideable;
    }

    /**
     * 设置是否可以滑动关闭Activity
     */
    @Override
    public void setSlideable(boolean slideable) {
        if (mSlideable == slideable) {
            return;
        }
        mSlideable = slideable;

        if (mSlideableLayout != null) {
            mSlideableLayout.setSlideable(slideable);
        }
    }

    /**
     * 设置退出监听
     *
     * @param listener
     */
    public void setOnSlideFinishListener(OnSlideFinishListener listener) {
        mOnSlideFinishListener = listener;
    }

    /**
     * 退出之前，移除view之前，做操作
     *
     * @param listener
     */
    public void setOnPreRemoveViewListener(OnPreRemoveViewListener listener) {
        mPreRemoveViewListener = listener;
    }


    /**
     * 获得侧滑view
     *
     * @return
     */
    @Nullable
    public SlideableLayout getSlideFrameLayout() {
        return mSlideableLayout;
    }

    /**
     * 下一层的Activity是否跟着滑动，默认为true
     *
     * @param flag true/false
     */
    public void setPreviousActivitySlideFollow(boolean flag) {
        mPreviousActivitySlideFollow = flag;
    }

    /**
     * 重置侧滑状态
     */
    public void resetSlideState() {
        if (mSlideableLayout != null) {
            mSlideableLayout.reset();
        }
    }

    /**
     * 得到上一个activity的一些和侧滑相关的信息
     *
     * @return 前一个界面的content view和LayerType
     */
    private Pair<View, Activity> getPreviousActivityInfo() {
        final Activity previousActivity = getPreviousPreviewActivity();
        if (previousActivity != null) {
            return Pair.create(previousActivity.findViewById(android.R.id.content), previousActivity);
        }
        return null;
    }

    /**
     * 得到前一个preview的activity
     *
     * @return activity
     */
    private Activity getPreviousPreviewActivity() {
        Activity previousActivity = mPreviousActivity;
        if (previousActivity != null && previousActivity.isFinishing()) {
            previousActivity = null;
            mPreviousActivity = null;
        }

        if (previousActivity == null && mNeedFindActivityFlag) {
            previousActivity = ActivityStack.getPreviousActivity(this);
            mPreviousActivity = previousActivity;
            if (previousActivity == null) {
                mNeedFindActivityFlag = false;
            }

            // 如果前一个activity销毁后，主动则变量置为null，防止内存泄漏和滑动退出的异常情况
            if (previousActivity instanceof LifeCycleInvoker) {
                ((LifeCycleInvoker) previousActivity).registerLifeCycleMonitor(mActivityLifecycleCallbacks);
            }
        }

        return previousActivity;
    }

    /**
     * 移动前一个activity的快照
     */
    private void offsetPreviousSnapshot(Pair<View, Activity> snapshot, float translateX, float translateY) {
        if (mSlideableLayout != null) {
            // 如果前一个界面不跟随一起滑动的话，把平移的值设置为0
            if (!mPreviousActivitySlideFollow) {
                translateX = 0;
            }
            View snapshotView = null;
            Drawable background = null;
            if (snapshot != null) {
                snapshotView = snapshot.first;
                final Activity activity = snapshot.second;
                if (snapshotView != null && activity instanceof OnSlideDrawListener) {
                    ((OnSlideDrawListener) activity).onSlideableViewDraw();
                }
                // 获得Activity decorView的背景，将其设置在快照view上，避免快照view透明
                if (activity != null) {
                    background = activity.getWindow().getDecorView().getBackground();
                }
            }
            mSlideableLayout.offsetPreviousSnapshot(snapshotView, translateX, 0, background);
        }
    }

    /**
     * 前一个activity销毁时调用
     */
    private void onPreviousActivityDestroyed() {

        release();

        // 尝试去新找一个预览activity
        mPreviousActivity = getPreviousPreviewActivity();

        // 找不到前一个activity，则不能滑动
        if (mPreviousActivity == null) {
            mNeedFindActivityFlag = false;
            setSlideable(false);
        }
    }

    /**
     * 释放一些状态
     */
    private void release() {
        // 去掉activity生命周期的回调，防止当前界面被前一个界面引用
        if (mPreviousActivity instanceof LifeCycleInvoker) {
            ((LifeCycleInvoker) mPreviousActivity).unregisterLifeCycleMonitor(mActivityLifecycleCallbacks);
        }
        // 避免在finish过程中，偶现白屏闪屏，这里不清空上一个Activity的快照，这样可以继续绘制上一个Activity的快照来防止出现白屏闪屏现象。
        //if (mSlideFrameLayout != null) {
        //    mSlideFrameLayout.offsetPreviousSnapshot(null, ViewCompat.LAYER_TYPE_NONE, 0);
        //}
        mPreviousActivity = null;
    }

    private SlideableLayout.ScrollableListener scrollableListener;

    public void setScrollableListener(SlideableLayout.ScrollableListener scrollableListener) {
        if (mSlideableLayout != null) {
            mSlideableLayout.setScrollableListener(scrollableListener);
        } else {
            this.scrollableListener = scrollableListener;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mSlideableLayout != null) {
            mSlideableLayout.onFinished();
        }
    }

    public static void setSlideable(boolean isSlideable, View view) {
        Activity activity = ViewUtils.getActivity(view);
        setSlideable(isSlideable, activity);
    }

    public static void setSlideable(boolean isSlideable, Activity activity) {
        if (activity instanceof AbsSlideableActivity) {
            AbsSlideableActivity slideBackActivity = (AbsSlideableActivity) activity;
            slideBackActivity.setSlideable(isSlideable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStatusStopped = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStatusStopped = false;
    }
}
