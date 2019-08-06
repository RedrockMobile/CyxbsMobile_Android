package com.mredrock.cyxbs.common.component;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * 标题居中显示的toolbar
 * Created by Jay on 2017/9/20.
 */

public class JToolbar extends Toolbar {
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    public JToolbar(Context context) {
        super(context);
    }

    public JToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView = getTitleTv("mTitleTextView");
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        super.setSubtitle(subtitle);
        mSubtitleTextView = getTitleTv("mSubtitleTextView");
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    private TextView getTitleTv(String name) {
        try {
            Field field = getClass().getSuperclass().getDeclaredField(name);
            field.setAccessible(true);
            return (TextView) field.get(this);
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

    private void reLayoutTitle(TextView title) {
        if (title == null) return;
        //note: o for old ,t for temp, l for left...
        int ol = title.getLeft();
        int width = title.getMeasuredWidth();
        int tl = (getMeasuredWidth() - width) / 2;

        if (tl > ol) {
            title.layout(tl, title.getTop(), tl + width, title.getBottom());
        }
    }
}
