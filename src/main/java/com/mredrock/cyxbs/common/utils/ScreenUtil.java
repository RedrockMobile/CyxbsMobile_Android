package com.mredrock.cyxbs.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by zxzhu on 2017/8/4.
 */

public class ScreenUtil {
    private static int dpWide,dpHeight,pxWide,pxHiget,dpi;
    private static float density;


    public ScreenUtil(Context mContext){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(dm);
        pxWide = dm.widthPixels;         // 屏幕宽度（像素）
        pxHiget = dm.heightPixels;       // 屏幕高度（像素）
        density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        dpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        dpWide = (int) (pxWide / density);  // 屏幕宽度(dp)
        dpHeight = (int) (pxHiget / density);// 屏幕高度(dp)

    }
    public float getDensity(){
        return density;
    }

    public int getDpWide(){
        return dpWide;
    }
    public int getDpHeight(){
        return dpHeight;
    }
    public int getPxWide(){
        return pxWide;
    }
    public int getPxHiget(){
        return pxHiget;
    }
    public int getDpi(){
        return dpi;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}