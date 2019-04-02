package com.mredrock.cyxbs.discover.electricity.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.anko.DimensionsKt;

/**
 * Created by ：AceMurder
 * Created on ：2017/2/27
 * Created for : CircleViewTest.
 * Enjoy it !!!
 */

public class ElectricCircleView extends View {
    private final Paint paint;
    private final Context context;
    private float rate = 0f / 150f;
    private RectF rectF;
    private int centerX;
    private int centerY;

    private String text = "00.00";
    private String usage = "0";


    public ElectricCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
        this.paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        rectF = new RectF();

    }

    public void drawWithData(float money, String text, String usage) {
        this.text = text;
        this.rate = money / 150;
        this.usage = usage;
        this.paint.setAntiAlias(true); //消除锯齿
        this.paint.setStyle(Paint.Style.STROKE);  //绘制空心圆或 空心矩形
        rectF = new RectF();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rate >= 0.97f)
            rate = 0.97f;
        if (rate <= 0.03)
            rate = 0.03f;

        centerX = getWidth() / 2;
        centerY = getWidth() / 2;
        int innerCircleR = dip2px(110); //内圆半径
        int ringWidth = dip2px(25);   //圆环宽度
        int outSideWidth = innerCircleR + ringWidth / 2;
        int outSideWidthLittle = innerCircleR + ringWidth / 2 - dip2px(10);
        int count = (int) (rate * 12);
        drawInnerCircle(canvas, ringWidth, innerCircleR);
        drawArc(canvas, outSideWidth, 180, rate * 180, Color.parseColor("#12d0ff"));
        drawArc(canvas, outSideWidth, 180 + rate * 180, 180 - rate * 180, Color.parseColor("#2912d0ff"));
        drawShortLine(canvas, outSideWidth, outSideWidthLittle, 0, 11 - count, Color.parseColor("#2912d0ff"));
        drawShortLine(canvas, outSideWidth, outSideWidthLittle, 11 - count, 11, Color.parseColor("#12d0ff"));
        drawLittleCircle(canvas, outSideWidth);
        drawMoneyText(canvas, text);
        drawUsageText(canvas, usage);
        super.onDraw(canvas);
    }

    private void drawMoneyText(Canvas canvas, String text) {
        paint.setColor(Color.parseColor("#12d0ff"));
        paint.setTextSize(dip2px(40));
        float baseLine = centerY + dip2px(5);

        if (text.length() == 7) {
            canvas.drawText(text, centerX - dip2px(85), baseLine, paint);
            paint.setTextSize(dip2px(15));
            canvas.drawText("元", centerX + dip2px(60), baseLine, paint);
        } else if (text.length() == 6) {
            canvas.drawText(text, centerX - dip2px(70), baseLine, paint);
            paint.setTextSize(dip2px(15));
            canvas.drawText("元", centerX + dip2px(55), baseLine, paint);
        } else if (text.length() == 5) {
            canvas.drawText(text, centerX - dip2px(55), baseLine, paint);
            paint.setTextSize(dip2px(15));
            canvas.drawText("元", centerX + dip2px(50), baseLine, paint);
        } else {
            canvas.drawText(text, centerX - dip2px(45), baseLine, paint);
            paint.setTextSize(dip2px(15));
            canvas.drawText("元", centerX + dip2px(40), baseLine, paint);
        }
    }

    private void drawUsageText(Canvas canvas, String usage) {
        paint.setColor(Color.parseColor("#93b3c2"));
        paint.setTextSize(dip2px(20));
        float baseLine = centerY + dip2px(50);
        if (usage == null)
            usage = "0";
        if (usage.length() == 1) {
            canvas.drawText(usage + "度", centerX - dip2px( 20), baseLine, paint);
        } else if (usage.length() == 2) {
            canvas.drawText(usage + "度", centerX - dip2px( 25), baseLine, paint);
        } else {
            canvas.drawText(usage + "度", centerX - dip2px(30), baseLine, paint);
        }
    }


    private void drawInnerCircle(Canvas canvas, float ringWidth, float innerCircleR) {
        this.paint.setColor(Color.parseColor("#edfbff"));
        this.paint.setStrokeWidth(ringWidth);
        canvas.drawCircle(centerX, centerY, innerCircleR, this.paint);
    }

    private void drawArc(Canvas canvas, float outSideWidth, float startAngle, float sweepAngle, int color) {
        this.paint.setColor(color);
        this.paint.setStrokeWidth(dip2px(6));
        rectF.left = centerX - outSideWidth;
        rectF.right = centerX + outSideWidth;
        rectF.bottom = centerY + outSideWidth;
        rectF.top = centerY - outSideWidth;
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
    }

    private void drawShortLine(Canvas canvas, float outSideWidth, float outSideWidthLittle, int startNum, int count, int color) {
//        this.paint.setStrokeWidth(dip2px(3));
//        paint.setColor(color);
//        //this.paint.setColor(Color.parseColor("#12d0ff"));
//
//        for (int i = startNum; i < count; i++) {
//            float startX = (float) (centerX + outSideWidth * cos(15 * (i + 1)));
//            float endX = (float) (centerX + outSideWidthLittle * cos(15 * (i + 1)));
//            float startY = (float) (centerY - outSideWidth * sin(15 * (i + 1)));
//            float endY = (float) (centerY - outSideWidthLittle * sin(15 * (i + 1)));
//            canvas.drawLine(startX, startY, endX, endY, paint);
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        invalidate();
    }


    private void drawLittleCircle(Canvas canvas, float outSideWidth) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#2112d0ff"));
        float degree = 180 - rate * 180;
        float x = (float) (centerX + outSideWidth * cos(degree));
        float y = (float) (centerY - outSideWidth * sin(degree));
        canvas.drawCircle(x, y, dip2px(18), paint);
        canvas.drawCircle(x, y, dip2px(14), paint);
        paint.setColor(Color.parseColor("#d941cfff"));
        canvas.drawCircle(x, y, dip2px(10), paint);
    }

    private double sin(double value) {
        return Math.sin(Math.toRadians(value));
    }

    private double cos(double value) {
        return Math.cos(Math.toRadians(value));
    }

    private int dip2px(float dpValue) {
        return DimensionsKt.dip(context, dpValue);
    }
}
