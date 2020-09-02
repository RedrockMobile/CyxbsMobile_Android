package com.mredrock.cyxbs.mine.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

import java.lang.reflect.Field;

/**
 * Created by roger on 2019/12/12
 */
//通过反射来更改Switch的宽度
public class SwitchPlus extends Switch {
    public SwitchPlus(Context context) {
        super(context);
    }

    public SwitchPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //通过反射来更改switch的宽度
        try {
            Class clazz = Class.forName(Switch.class.getName());
            final Field switchWidth = clazz.getDeclaredField("mSwitchWidth");
            switchWidth.setAccessible(true);
            int width = dp2px(50f);
            switchWidth.set(this, width);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public int dp2px(Float dpValue) {
        return (int)(dpValue * getResources().getDisplayMetrics().density + 0.5f);
    }
}
