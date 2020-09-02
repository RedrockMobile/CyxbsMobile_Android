package com.mredrock.cyxbs.common.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mredrock.cyxbs.common.R;

/**
 * Created by Jay on 2017/8/12.
 */

public class RatioLayout extends FrameLayout {
    private float mRatio;

    public RatioLayout(@NonNull Context context) {
        super(context);
    }

    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mRatio = array.getFloat(R.styleable.RatioLayout_ratio, -1);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio > 0) {
            if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                int size = MeasureSpec.getSize(widthMeasureSpec);
                resetChildrenLayoutParams(true, size);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (size * mRatio)
                        , MeasureSpec.EXACTLY);
            } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                int size = MeasureSpec.getSize(heightMeasureSpec);
                resetChildrenLayoutParams(false, size);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (size * mRatio)
                        , MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetChildrenLayoutParams(boolean dependOnWidth, int size) {
        int width = dependOnWidth ? size : (int) (size / mRatio);
        int height = dependOnWidth ? (int) (size * mRatio) : size;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            params.height = height;
        }
    }
}
