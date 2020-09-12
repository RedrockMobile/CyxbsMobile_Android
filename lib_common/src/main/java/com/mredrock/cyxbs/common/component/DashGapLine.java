package com.mredrock.cyxbs.common.component;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import static com.mredrock.cyxbs.common.utils.extensions.ContextKt.dp2px;


public class DashGapLine extends View {
    private Paint circle;
    private Paint gap;
    private Path mPath;

    private int mWidth = 100;//默认的
    private int mHeight = 100;

    private int radius = (int) dp2px(getContext(),7);

    private boolean lineVisible = true;
    private int defaultCircleColor = Color.parseColor("#2921D1");
    private int defaultGapColor = Color.parseColor("#2921D1");


    public DashGapLine(Context context) {
        super(context);
        init();
    }

    public DashGapLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashGapLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        circle = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle.setColor(defaultCircleColor);
        circle.setStrokeWidth(dp2px(getContext(),5));
        circle.setStyle(Paint.Style.STROKE);

        gap = new Paint(Paint.ANTI_ALIAS_FLAG);
        gap.setColor(defaultGapColor);
        gap.setStrokeWidth(3);
        gap.setStyle(Paint.Style.STROKE);
        gap.setPathEffect(new DashPathEffect(new float[]{15,15},0));

        mPath = new Path();
    }

    public void setLineVisible(boolean lineVisible){
        this.lineVisible = lineVisible;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth + getPaddingLeft() + getPaddingRight(), mHeight + getPaddingBottom() + getPaddingTop());
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth + getPaddingLeft() + getPaddingRight(), heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight + getPaddingBottom() + getPaddingTop());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw circle
        int centerX = getWidth() / 2;
        //stroke为5，所以说要加上
        canvas.drawCircle(centerX, radius + dp2px(getContext(),5), radius, circle);
        //draw gap
        if(lineVisible){
            mPath.reset();
            mPath.moveTo(centerX, radius * 2 + 11);
            Log.d("exam",getHeight()+"");
            mPath.lineTo(centerX, getHeight());
            canvas.drawPath(mPath, gap);
        }
    }

}

