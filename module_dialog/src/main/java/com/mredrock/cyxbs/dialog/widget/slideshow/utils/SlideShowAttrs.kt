package com.mredrock.cyxbs.dialog.widget.slideshow.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.dialog.R
import com.mredrock.cyxbs.dialog.widget.slideshow.SlideShow
import com.mredrock.cyxbs.dialog.widget.slideshow.indicators.utils.IndicatorsAttrs

/**
 * .....
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/5/27
 */
class SlideShowAttrs private constructor(){

    companion object {
        internal val Lib_name = SlideShow::class.java.simpleName
    }

    var viewWidth = ViewGroup.LayoutParams.MATCH_PARENT
        internal set
    var viewHeight = ViewGroup.LayoutParams.MATCH_PARENT
        internal set
    var imgDefaultColor = 0x00000000
        internal set
    var backgroundColor = 0x00000000
        internal set

    private var viewMargin = 0
    var viewMarginHorizontal = 0
        internal set
    var viewMarginVertical = 0
        internal set

    /**
     * ViewPager2 内部相邻页面的边距
     */
    var adjacentPageInterval = -1
        internal set

    /**
     * ViewPager2 内部页面与外部页面的边距
     */
    var outPageInterval = -1
        internal set

    private var imgRadius = 0F
    var imgLeftTopRadius = 0F
        internal set
    var imgRightTopRadius = 0F
        internal set
    var imgLeftBottomRadius = 0F
        internal set
    var imgRightBottomRadius = 0F
        internal set

    @ViewPager2.Orientation
    var orientation = ViewPager2.ORIENTATION_HORIZONTAL
        internal set

    /**
     * 该值还需进行一次转换才是相邻页面边距值
     */
    internal var pageInterval = 0

    lateinit var mIndicatorsAttrs: IndicatorsAttrs
        internal set

    internal fun initialize(context: Context, attrs: AttributeSet) {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.SlideShow)

        viewWidth = ty.getLayoutDimension(R.styleable.SlideShow_slide_viewWidth, viewWidth)
        viewHeight = ty.getLayoutDimension(R.styleable.SlideShow_slide_viewHeight, viewHeight)

        imgDefaultColor = ty.getColor(R.styleable.SlideShow_slide_imgDefaultColor, imgDefaultColor)

        viewMargin = ty.getDimensionPixelSize(R.styleable.SlideShow_slide_viewMargin, viewMargin)
        viewMarginHorizontal =
            ty.getDimensionPixelSize(R.styleable.SlideShow_slide_viewMarginHorizontal, viewMarginHorizontal)
        viewMarginVertical =
            ty.getDimensionPixelSize(R.styleable.SlideShow_slide_viewMarginVertical, viewMarginVertical)

        orientation = ty.getInt(R.styleable.SlideShow_slide_orientation, orientation)

        adjacentPageInterval =
            ty.getDimensionPixelSize(R.styleable.SlideShow_slide_adjacentPageInterval, adjacentPageInterval)
        outPageInterval =
            ty.getDimensionPixelSize(R.styleable.SlideShow_slide_outPageInterval, outPageInterval)

        imgRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRadius, imgRadius)
        imgLeftTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftTopRadius, imgLeftTopRadius)
        imgRightTopRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightTopRadius, imgRightTopRadius)
        imgLeftBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgLeftBottomRadius, imgLeftBottomRadius)
        imgRightBottomRadius = ty.getDimension(R.styleable.SlideShow_slide_imgRightBottomRadius, imgRightBottomRadius)

        // 以下为指示器的属性
        mIndicatorsAttrs = IndicatorsAttrs.Builder().build()
        mIndicatorsAttrs.initialize(ty)

        ty.recycle()
        setAttrs()
    }

    internal fun setAttrs() {
        if (viewMargin != 0) {
            viewMarginHorizontal = viewMargin
            viewMarginVertical = viewMargin
        }
        if (adjacentPageInterval == -1 && outPageInterval != -1) { // 这种情况下只是单独设置内部页面到外部页面的值，与设置 viewMarginHorizontal 效果一样
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                viewMarginHorizontal = outPageInterval
            }else {
                viewMarginVertical = outPageInterval
            }
        }else if (adjacentPageInterval != -1 && outPageInterval == -1) { // 这种情况下只有当宽度不为 match_parent 时才允许设置相邻边距
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (viewWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                    throw RuntimeException(
                            "Your $Lib_name: " +
                                    "When slide_imgWidth is match_parent, " +
                                    "you must set slide_outPageInterval after setting the slide_adjacentPageInterval!")
                }
            }else {
                if (viewHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                    throw RuntimeException(
                            "Your $Lib_name: " +
                                    "When slide_imgHeight is match_parent, " +
                                    "you must set slide_outPageInterval after setting the slide_adjacentPageInterval!")
                }
            }
            pageInterval = adjacentPageInterval
        }else if (adjacentPageInterval != -1 && outPageInterval != -1) {
            if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (viewWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                    viewMarginHorizontal = adjacentPageInterval / 2
                    pageInterval = outPageInterval - viewMarginHorizontal
                    if (pageInterval < 0) {
                        throw RuntimeException(
                                "Your $Lib_name: " +
                                        "slide_outPageInterval must > slide_adjacentPageInterval / 2 !")
                    }
                }else {
                    pageInterval = adjacentPageInterval
                }
            }else {
                if (viewHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                    viewMarginVertical = adjacentPageInterval / 2
                    pageInterval = outPageInterval - viewMarginVertical
                    if (pageInterval < 0) {
                        throw RuntimeException(
                                "Your $Lib_name: " +
                                        "slide_outPageInterval must > slide_adjacentPageInterval / 2 !")
                    }
                }else {
                    pageInterval = adjacentPageInterval
                }
            }
        }
        if (imgRadius != 0F) {
            imgLeftTopRadius = imgRadius
            imgRightTopRadius = imgRadius
            imgLeftBottomRadius = imgRadius
            imgRightBottomRadius = imgRadius
        }
    }

    class Builder {

        private val mAttrs = SlideShowAttrs()

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
         */
        fun setImgRadius(@Px radius: Float): Builder {
            mAttrs.imgLeftTopRadius = radius
            mAttrs.imgRightTopRadius = radius
            mAttrs.imgLeftBottomRadius = radius
            mAttrs.imgRightBottomRadius = radius
            return this
        }

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的圆角
         */
        fun setImgRadius(@Px leftTop: Float, @Px rightTop: Float, @Px leftBottom: Float, @Px rightBottom: Float): Builder {
            mAttrs.imgLeftTopRadius = leftTop
            mAttrs.imgRightTopRadius = rightTop
            mAttrs.imgLeftBottomRadius = leftBottom
            mAttrs.imgRightBottomRadius = rightBottom
            return this
        }

        /**
         * 使用自带的图片加载的 setAdapter 方法后可以调用，用于设置内部 ImageView 的默认颜色
         */
        fun setImgDefaultColor(@ColorInt color: Int): Builder {
            mAttrs.imgDefaultColor = color
            return this
        }

        /**
         * -1 为 match_parent，-2 为 wrap_content
         */
        fun setViewWidth(@Px pixel: Int): Builder {
            if (pixel < -2) {
                throw RuntimeException(
                    "Your $Lib_name#setViewWidth(): The pixel is < -2")
            }
            mAttrs.viewWidth = pixel
            return this
        }

        /**
         * -1 为 match_parent，-2 为 wrap_content
         */
        fun setViewHeight(@Px pixel: Int): Builder {
            if (pixel < -2) {
                throw RuntimeException(
                    "Your $Lib_name#setViewHeight(): The pixel is < -2")
            }
            mAttrs.viewHeight = pixel
            return this
        }

        fun setViewMargin(@Px viewMargin: Int): Builder {
            mAttrs.viewMargin = viewMargin
            return this
        }

        /**
         * **WARNING：** 如果在水平滑动时 viewWidth 不为 match_parent 或者设置了 slide_outPageInterval，设置 viewMarginHorizontal 将无效
         */
        fun setViewMarginHorizontal(@Px viewMarginHorizontal: Int): Builder {
            mAttrs.viewMarginHorizontal = viewMarginHorizontal
            return this
        }

        /**
         * **WARNING：** 如果在垂直滑动时 viewHeight 不为 match_parent 或者设置了 slide_outPageInterval，设置 viewMarginVertical 将无效
         */
        fun setViewMarginVertical(@Px viewMarginVertical: Int): Builder {
            mAttrs.viewMarginVertical = viewMarginVertical
            return this
        }

        /**
         * 设置内部 ViewPager2 的 orientation
         * @param orientation 数据来自 [ViewPager2.ORIENTATION_HORIZONTAL]、[ViewPager2.ORIENTATION_VERTICAL]
         */
        fun setOrientation(@ViewPager2.Orientation orientation: Int): Builder {
            mAttrs.orientation = orientation
            return this
        }

        /**
         * 设置相邻内部页面边距和内部页面与外部页面
         *
         * **WARNING：** 如果在水平滑动时 imgWidth 或在垂直滑动时 imgHeight 不为 match_parent，设置 [outPageInterval] 将无效
         *
         * **WARNING：** 只有在未加载视图时设置才有效
         *
         * @param adjacentPageInterval 相邻页面间距
         * @param outPageInterval 内部页面于外部页面的边距
         */
        fun setPageInterval(@Px adjacentPageInterval: Int, @Px outPageInterval: Int): Builder {
            mAttrs.adjacentPageInterval = adjacentPageInterval
            mAttrs.outPageInterval = outPageInterval
            mAttrs.pageInterval = outPageInterval - adjacentPageInterval / 2
            if (mAttrs.pageInterval < 0) {
                throw RuntimeException(
                    "Your $Lib_name#setPageInterval(): " +
                            "outPageInterval must > adjacentPageInterval / 2 !")
            }
            return this
        }

        /**
         * 设置指示器
         */
        fun setIndicators(indicatorsAttrs: IndicatorsAttrs): Builder {
            mAttrs.mIndicatorsAttrs = indicatorsAttrs
            return this
        }

        fun build(): SlideShowAttrs {
            mAttrs.setAttrs()
            return mAttrs
        }
    }
}