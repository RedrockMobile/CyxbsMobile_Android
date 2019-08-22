package com.mredrock.cyxbs.mine.util.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static java.lang.Math.abs;

/**
 * Created By jay68 on 2018/5/4.
 * 一个能绘制圆角矩形（圆角能内凹）并自带阴影的Drawable（可设置阴影颜色）
 * 对于阴影的隐藏没有想到更好的实现方法，目前的方法会造成以此drawable作为背景的View发生内容错位，需要配合padding使用
 */
public class RectShadowDrawable extends Drawable {
    private Rect mRadius;
    private int mShadowRadius;
    private int mOffsetX, mOffsetY;
    private boolean[] mHideShadow;

    private Paint mPaint;
    private Path mShape;
    private RectF mRectF;

    private View mView;

    public RectShadowDrawable(Rect radius, @ColorInt int backgroundColor, @ColorInt int shadowColor,
                              int shadowRadius, int offsetX, int offsetY) {
        this(radius, backgroundColor, shadowColor, shadowRadius, offsetX, offsetY, new boolean[]{false, false, false, false}, null);
    }

    public RectShadowDrawable(Rect radius, @ColorInt int backgroundColor, @ColorInt int shadowColor,
                              int shadowRadius, int offsetX, int offsetY, boolean[] hideShadow, View view) {
        mRadius = radius;
        mShadowRadius = shadowRadius;
        mOffsetX = offsetX;
        mOffsetY = offsetY;
        mHideShadow = hideShadow;

        mPaint = new Paint();
        mPaint.setColor(backgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setShadowLayer(shadowRadius, offsetX, offsetY, shadowColor);

        mShape = new Path();
        mView = view;
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRectF = new RectF(left - mOffsetX, top - mOffsetY, right - mOffsetX, bottom - mOffsetY);
        mRectF.left += (mHideShadow != null && mHideShadow[0]) ? 0 : mShadowRadius;
        mRectF.top += (mHideShadow != null && mHideShadow[1]) ? 0 : mShadowRadius;
        mRectF.right -= (mHideShadow != null && mHideShadow[2]) ? 0 : mShadowRadius;
        mRectF.bottom -= (mHideShadow != null && mHideShadow[3]) ? 0 : mShadowRadius;
        resetShape(mRectF);
    }

    private void resetShape(RectF rectF) {
        mShape.reset();
        mShape.moveTo(rectF.left, rectF.top + abs(mRadius.left));
        mShape.lineTo(rectF.left, rectF.bottom - abs(mRadius.bottom));
        if (mRadius.bottom < 0) {
            RectF temp = new RectF(rectF.left + mRadius.bottom, rectF.bottom + mRadius.bottom, rectF.left - mRadius.bottom, rectF.bottom - mRadius.bottom);
            mShape.arcTo(temp, -90, 90, false);
        } else if (mRadius.bottom > 0) {
            RectF temp = new RectF(rectF.left, rectF.bottom - 2 * mRadius.bottom, rectF.left + 2 * mRadius.bottom, rectF.bottom);
            mShape.arcTo(temp, 180, -90, false);
        }
        mShape.lineTo(rectF.right - abs(mRadius.right), rectF.bottom);
        if (mRadius.right < 0) {
            RectF temp = new RectF(rectF.right + mRadius.right, rectF.bottom + mRadius.right, rectF.right - mRadius.right, rectF.bottom - mRadius.right);
            mShape.arcTo(temp, 180, 90, false);
        } else if (mRadius.right > 0) {
            RectF temp = new RectF(rectF.right - 2 * mRadius.right, rectF.bottom - 2 * mRadius.right, rectF.right, rectF.bottom);
            mShape.arcTo(temp, 90, -90, false);
        }
        mShape.lineTo(rectF.right, rectF.top + abs(mRadius.top));
        if (mRadius.top < 0) {
            RectF temp = new RectF(rectF.right + mRadius.top, rectF.top + mRadius.top, rectF.right - mRadius.top, rectF.top - mRadius.top);
            mShape.arcTo(temp, 90, 90, false);
        } else if (mRadius.top > 0) {
            RectF temp = new RectF(rectF.right - 2 * mRadius.top, rectF.top, rectF.right, rectF.top + 2 * mRadius.top);
            mShape.arcTo(temp, 0, -90, false);
        }
        mShape.lineTo(mRectF.left + abs(mRadius.left), mRectF.top);
        if (mRadius.left < 0) {
            RectF temp = new RectF(rectF.left + mRadius.left, rectF.top + mRadius.left, rectF.left - mRadius.left, rectF.top - mRadius.left);
            mShape.arcTo(temp, 0, 90, false);
        } else if (mRadius.left > 0) {
            RectF temp = new RectF(rectF.left, rectF.top, rectF.left + 2 * mRadius.left, rectF.top + 2 * mRadius.left);
            mShape.arcTo(temp, -90, -90, false);
        }
        mShape.close();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(mShape, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
