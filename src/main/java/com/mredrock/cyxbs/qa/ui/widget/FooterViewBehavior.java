package com.mredrock.cyxbs.qa.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Jay on 2017/8/3.
 */

public class FooterViewBehavior extends CoordinatorLayout.Behavior {
    public FooterViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NotNull CoordinatorLayout parent, @NotNull View child, @NotNull View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NotNull CoordinatorLayout parent, View child, View dependency) {
        float translation = Math.abs(dependency.getTop());
        child.setTranslationY(translation);
        return true;
    }
}
