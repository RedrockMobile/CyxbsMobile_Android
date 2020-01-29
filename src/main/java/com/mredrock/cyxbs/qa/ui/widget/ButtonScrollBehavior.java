package com.mredrock.cyxbs.qa.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

/**
 * Create by yyfbe at 2020-01-26
 * 上滑消失，下滑出现
 */
public class ButtonScrollBehavior extends CoordinatorLayout.Behavior {
    public ButtonScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        //垂直滚动时监听
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0) {
            animateOut(child);
        } else if (dyConsumed < 0) {
            animateIn(child);
        }
    }

    //下移出屏幕
    private void animateOut(View view) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;
        view.animate().translationY(view.getHeight() + bottomMargin).setInterpolator(new LinearInterpolator()).start();
    }

    //显示到屏幕
    private void animateIn(View view) {
        view.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
}
