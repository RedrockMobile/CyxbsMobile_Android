package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils

import android.content.res.TypedArray
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.AbstractIndicatorsView

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/6/17
 */
class IndicatorsAttrs private constructor() {

    var isShowIndicators = true
        internal set
    @Indicators.Style
    var indicatorStyle = Indicators.Style.NO_SHOW
        internal set
    @Indicators.OuterGravity
    var indicatorOuterGravity = Indicators.OuterGravity.BOTTOM
        internal set
    @Indicators.InnerGravity
    var indicatorInnerGravity = Indicators.InnerGravity.CENTER
        internal set

    var indicatorCircleRadius = AbstractIndicatorsView.CIRCLE_RADIUS
        internal set
    var indicatorCircleColor = AbstractIndicatorsView.CIRCLE_COLOR
        internal set
    var indicatorBackgroundCircleColor = AbstractIndicatorsView.BACKGROUND_CIRCLE_COLOR
        internal set
    var indicatorBackgroundColor = AbstractIndicatorsView.BACKGROUND_COLOR
        internal set
    var indicatorWrapWidth = AbstractIndicatorsView.WRAP_WIDTH
        internal set
    var intervalMargin = AbstractIndicatorsView.INTERVAL_MARGIN
        internal set

    internal fun initialize(ty: TypedArray) {
        isShowIndicators = ty.getBoolean(R.styleable.SlideShow_Indicators_isShow, isShowIndicators)
        indicatorStyle = ty.getInt(R.styleable.SlideShow_indicators_style, indicatorStyle)
        indicatorOuterGravity = ty.getInt(R.styleable.SlideShow_indicators_outerGravity, indicatorOuterGravity)
        indicatorInnerGravity = ty.getInt(R.styleable.SlideShow_indicators_innerGravity, indicatorInnerGravity)
        indicatorCircleRadius = ty.getDimension(R.styleable.SlideShow_indicators_circleRadius, indicatorCircleRadius)
        indicatorCircleColor = ty.getColor(R.styleable.SlideShow_indicators_circleColor, indicatorCircleColor)
        indicatorBackgroundCircleColor = ty.getColor(R.styleable.SlideShow_indicators_backgroundCircleColor, indicatorBackgroundCircleColor)
        indicatorBackgroundColor = ty.getColor(R.styleable.SlideShow_indicators_backgroundColor, indicatorBackgroundColor)
        indicatorWrapWidth = ty.getDimension(R.styleable.SlideShow_indicators_wrapWidth, indicatorWrapWidth)
        intervalMargin = ty.getDimension(R.styleable.SlideShow_indicators_intervalMargin, intervalMargin)
        setAttrs()
    }

    private fun setAttrs() {
    }

    class Builder {

        private val mAttrs = IndicatorsAttrs()

        /**
         * 设置指示器的样式
         *
         * @see style 数据来自 [Indicators.Style]
         */
        fun setIndicatorsStyle(@Indicators.Style style: Int): Builder {
            mAttrs.indicatorStyle = style
            return this
        }

        /**
         * 设置指示器外部的位置（整个横幅）
         *
         * @param gravity 数据来自 [Indicators.OuterGravity]
         */
        fun setIndicatorsOuterGravity(@Indicators.OuterGravity gravity: Int): Builder {
            mAttrs.indicatorOuterGravity = gravity
            return this
        }

        /**
         * 设置指示器内部的位置（小圆点）
         *
         * @param gravity 数据来自 [Indicators.InnerGravity]
         */
        fun setIndicatorsInnerGravity(@Indicators.InnerGravity gravity: Int): Builder {
            mAttrs.indicatorInnerGravity = gravity
            return this
        }

        /**
         * 设置指示器的圆点半径
         */
        fun setIndicatorsCircleRadius(@Px radius: Float): Builder {
            mAttrs.indicatorCircleRadius = radius
            return this
        }

        /**
         * 设置指示器的圆点颜色
         */
        fun setIndicatorsCircleColor(@ColorInt color: Int): Builder {
            mAttrs.indicatorCircleColor = color
            return this
        }

        /**
         * 设置指示器的圆点的背景颜色
         */
        fun setIndicatorsBackgroundCircleColor(@ColorInt color: Int): Builder {
            mAttrs.indicatorBackgroundCircleColor = color
            return this
        }

        /**
         * 设置指示器的横幅背景颜色
         */
        fun setIndicatorsBackgroundColor(@ColorInt color: Int): Builder {
            mAttrs.indicatorBackgroundColor = color
            return this
        }

        /**
         * 设置指示器横幅最小边的宽度
         */
        fun setIndicatorWrapWidth(@Px indicatorWrapWidth: Float): Builder {
            mAttrs.indicatorWrapWidth = indicatorWrapWidth
            return this
        }

        /**
         * 设置指示器两个圆点间的距离值
         */
        fun setIntervalMargin(@Px interval: Float): Builder {
            mAttrs.intervalMargin = interval
            return this
        }

        fun build(): IndicatorsAttrs {
            mAttrs.setAttrs()
            return mAttrs
        }
    }
}