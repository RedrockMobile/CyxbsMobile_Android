package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import java.lang.annotation.Retention;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.EXTEND_ABSTRACT_INDICATORS;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.FLASH;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.MOVE;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.NO_SHOW;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.SELF_VIEW;
import static com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators.Style.ZOOM;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * .....
 *
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/29
 */
public class Indicators {

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            NO_SHOW,
            SELF_VIEW,
            EXTEND_ABSTRACT_INDICATORS,
            MOVE,
            ZOOM,
            //WATER_DROP,
            FLASH,
    })
    public @interface Style {

        /**
         * 不显示
         */
        int NO_SHOW = -1;

        /**
         * 接入 IIndicator 接口自己写的指示器，并使用 SlideShow#setYourIndicators 方法
         */
        int SELF_VIEW = 0;

        /**
         * 继承于 AbstractIndicatorsView 抽象类自己写的指示器，并使用 SlideShow#setYourIndicators 方法
         */
        int EXTEND_ABSTRACT_INDICATORS = 1;

        /**
         * 一个简单的平滑指示器
         */
        int MOVE = 2;

        /**
         * 一个带有放大动画指示器
         */
        int ZOOM = 3;

        /**
         * 一个带有水滴动画的指示器
         */
        int WATER_DROP = 4;

        /**
         * 一个无动画的指示器，直接闪现过去
         */
        int FLASH = 5;
    }

    /**
     * 整个横幅的 Gravity
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            OuterGravity.TOP,
            OuterGravity.BOTTOM,
            OuterGravity.LEFT,
            OuterGravity.RIGHT,
            OuterGravity.VERTICAL_CENTER,
            OuterGravity.HORIZONTAL_CENTER
    })
    public @interface OuterGravity {
        int TOP = 0x1;
        int BOTTOM = 0x2;
        int LEFT = 0x4;
        int RIGHT = 0x10;
        int VERTICAL_CENTER = 0x14;
        int HORIZONTAL_CENTER = 0x3;
    }

    /**
     * 横幅中圆点 Gravity
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef({
            InnerGravity.FRONT,
            InnerGravity.BACK,
            InnerGravity.CENTER,
    })
    public @interface InnerGravity {

        /**
         * 水平时靠左，竖直时靠上
         */
        int FRONT = 0x1;

        /**
         * 水平时靠右，竖直时靠下
         */
        int BACK = 0x2;

        int CENTER = 0x3;
    }
}
