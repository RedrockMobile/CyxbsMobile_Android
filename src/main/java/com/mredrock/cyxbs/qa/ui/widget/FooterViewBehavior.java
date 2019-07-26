package com.mredrock.cyxbs.qa.ui.widget;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jay on 2017/8/3.
 */

public class FooterViewBehavior extends CoordinatorLayout.Behavior {
    public FooterViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translation = Math.abs(dependency.getTop());
        child.setTranslationY(translation);
        return true;
    }
}
