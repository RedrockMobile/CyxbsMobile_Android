package com.mredrock.cyxbs.common.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.mredrock.cyxbs.common.R;
import com.mredrock.cyxbs.common.utils.extensions.ContextKt;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 标题居中显示的toolbar
 * Created by Jay on 2017/9/20.
 */

public class JToolbar extends Toolbar {
    private boolean isTitleAtLeft = true;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private boolean withSplitLine = true;
    private Paint paint = new Paint();

    @SuppressLint("ResourceAsColor")
    public JToolbar(Context context) {
        super(context);
        paint.setColor(R.color.commonDefaultDivideLineColor);
        paint.setAlpha(25);
        paint.setStrokeWidth(ContextKt.dp2px(context, 1));
    }

    @SuppressLint("ResourceAsColor")
    public JToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(R.color.commonDefaultDivideLineColor);
        paint.setAlpha(25);
        paint.setStrokeWidth(ContextKt.dp2px(context, 1));
    }

    @SuppressLint("ResourceAsColor")
    public JToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(R.color.commonDefaultDivideLineColor);
        paint.setAlpha(25);
        paint.setStrokeWidth(ContextKt.dp2px(context, 1));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView = getTitleTv("mTitleTextView");
        if (mTitleTextView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTitleTextView.setTextColor(getContext().getColor(R.color.levelTwoFontColor));
            } else {
                mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.levelTwoFontColor));
            }
        }
    }

    @Override
    public void setTitleTextColor(int color) {
        super.setTitleTextColor(color);
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        super.setSubtitle(subtitle);
        mSubtitleTextView = getTitleTv("mSubtitleTextView");
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    @SuppressLint("ResourceAsColor")
    private TextView getTitleTv(String name) {
        try {
            Field field = Objects.requireNonNull(getClass().getSuperclass()).getDeclaredField(name);
            field.setAccessible(true);
            TextView view = (TextView) field.get(this);
            view.getPaint().setFakeBoldText(true);
            view.setTextColor(R.color.levelTwoFontColor);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        reLayoutTitle(mTitleTextView);
        reLayoutTitle(mSubtitleTextView);
    }

    //设置title为左对齐或是居中
    public void setTitleLocationAtLeft(boolean isLeft) {
        isTitleAtLeft = isLeft;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //为什么这个screenWidth绘制不满一个屏幕的宽度？？？

        if (withSplitLine)
            canvas.drawLine(0, getMeasuredHeight() - ContextKt.dp2px(getContext(), 0.5f), 2 * ContextKt.getScreenWidth(getContext()), getMeasuredHeight() - ContextKt.dp2px(getContext(), 0.5f), paint);
    }

    private void reLayoutTitleToLeft(TextView title) {
        if (title == null || !isTitleAtLeft) return;
        int ir = getChildAt(0).getRight();
        title.layout(ir, title.getTop(), ir + title.getMeasuredWidth(), title.getBottom());
    }

    private void reLayoutTitleToCenter(TextView title) {
        //note: o for old ,t for temp, l for left...
        int ol = title.getLeft();
        int width = title.getMeasuredWidth();
        int tl = (getMeasuredWidth() - width) / 2;

        if (tl > ol) {
            title.layout(tl, title.getTop(), tl + width, title.getBottom());
        }
    }

    private void reLayoutTitle(TextView title) {
        if (title == null) return;
        if (isTitleAtLeft) {
            reLayoutTitleToLeft(title);
        } else {
            reLayoutTitleToCenter(title);
        }
    }

    public void withSplitLine(boolean withSplitLine) {
        this.withSplitLine = withSplitLine;
    }

    public void initPaint(int color) {
        paint.setColor(color);
    }
}
