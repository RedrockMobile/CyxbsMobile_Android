package com.mredrock.cyxbs.store.utils.widget.slideshow.indicators

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.Indicators
import com.mredrock.cyxbs.store.utils.widget.slideshow.myinterface.IIndicator
import com.mredrock.cyxbs.store.utils.widget.slideshow.indicators.utils.IndicatorsAttrs
import com.mredrock.cyxbs.store.utils.widget.slideshow.utils.SlideShowUtils

/**
 * 如果你想实现自己的指示器, 可以继承于该抽象类, 实现该抽象类你只需绘制一段路径的圆点移动动画就能实现所有圆点间的移动动画
 *
 * 继承后，你只需要实现 [onDrawMovePath] 方法即可，该方法可以实现一个区间的轨迹绘制而实现全部轨迹的绘制
 * @author 985892345
 * @email 2767465918@qq.com
 * @data 2021/5/28
 */
abstract class AbstractIndicatorsView(
    context: Context
) : View(context), IIndicator {

    /**
     * 设置为 wrap_content 时的厚度值
     */
    var wrapWidth = WRAP_WIDTH
        private set

    /**
     * 圆点颜色
     */
    var circleColor = CIRCLE_COLOR
        private set

    /**
     * 圆点的半径值
     */
    var circleRadius = CIRCLE_RADIUS
        private set

    /**
     * 两个圆点间的距离值
     */
    var intervalMargin = INTERVAL_MARGIN
        private set

    /**
     * 小圆点背景的颜色
     */
    var backgroundCircleColor = BACKGROUND_CIRCLE_COLOR
        private set

    /**
     * 设置指示器属性
     */
    internal fun setIndicatorsAttrs(attrs: IndicatorsAttrs) {
        indicatorsAttrs = attrs
        wrapWidth = attrs.indicatorWrapWidth
        circleColor = attrs.indicatorCircleColor
        circleRadius = attrs.indicatorCircleRadius
        intervalMargin = attrs.intervalMargin
        backgroundCircleColor = attrs.indicatorBackgroundCircleColor
    }

    companion object {

        /**
         * 指示器横幅最小边的宽度默认值
         */
        val WRAP_WIDTH = SlideShowUtils.dp2Px(30)

        /**
         * 指示器圆点颜色默认值
         */
        const val CIRCLE_COLOR = 0xFFFAFAFA.toInt()

        /**
         * 指示器圆点半径大小默认值
         */
        val CIRCLE_RADIUS = SlideShowUtils.dp2Px(6)

        /**
         * 指示器两个圆点间的距离默认值
         */
        val INTERVAL_MARGIN = SlideShowUtils.dp2Px(32)

        /**
         * 指示器横幅背景颜色默认值
         */
        const val BACKGROUND_COLOR = 0x00000000

        /**
         * 指示器小圆点背景颜色默认值
         */
        const val BACKGROUND_CIRCLE_COLOR = 0x8E8E8E8E.toInt()
    }

    private lateinit var indicatorsAttrs: IndicatorsAttrs

    private var amount = 0
    private var frontMargin = 0F // 指示器第一个小圆点距离左方/上方的距离

    /**
     * 圆点位置
     */
    private var position = 0

    /**
     * 圆点上一次停留时的位置
     */
    private var idlePosition = 0

    /**
     * 圆点的位置（带小数）
     */
    private var positionFloat = 0F

    /**
     * 用于 [onDrawMovePath] 中，是该点的像素偏离值，只会在 -intervalMargin 到 +intervalMargin 之间
     */
    private var offsetPixels = 0F

    private var outerGravity = Indicators.OuterGravity.BOTTOM

    private var innerGravity = Indicators.InnerGravity.CENTER

    override fun getIndicatorView(): View {
        return this
    }

    override fun setAmount(amount: Int) {
        this.amount = amount
        post { // 写在这里是为了在调用 changeAmount() 后能修改 frontMargin 值
            judgeStyle(
                horizontal = {
                    when (innerGravity) {
                        Indicators.InnerGravity.FRONT -> {
                            frontMargin = intervalMargin
                        }
                        Indicators.InnerGravity.BACK -> {
                            frontMargin = width - amount * intervalMargin
                        }
                        Indicators.InnerGravity.CENTER -> {
                            frontMargin = width / 2F - (amount - 1) / 2F * intervalMargin
                        }
                    }
                },
                vertical = {
                    when (innerGravity) {
                        Indicators.InnerGravity.FRONT -> {
                            frontMargin = intervalMargin
                        }
                        Indicators.InnerGravity.BACK -> {
                            frontMargin = height - amount * intervalMargin
                        }
                        Indicators.InnerGravity.CENTER -> {
                            frontMargin = height / 2F - (amount - 1) / 2F * intervalMargin
                        }
                    }
                }
            )
            invalidate()
        }
    }

    override fun changeAmount(amount: Int) {
        setAmount(amount)
    }

    override fun setIndicatorsOuterGravity(gravity: Int): FrameLayout.LayoutParams {
        outerGravity = gravity
        return super.setIndicatorsOuterGravity(gravity)
    }

    override fun setIndicatorsInnerGravity(gravity: Int) {
        innerGravity = gravity
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.position = position
        positionFloat = position + positionOffset
        val offset =
            if (position >= idlePosition || positionFloat == 0F) positionOffset
            else positionOffset - 1 // 当向左滑动时，positionOffset 是从 0.99999 -> 0 的，所以要减一改为 0 -> -0.99999
        offsetPixels = offset * intervalMargin
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> {
                idlePosition = position
                offsetPixels = 0F
                invalidate()
            }
            ViewPager2.SCROLL_STATE_DRAGGING -> {
            }
            ViewPager2.SCROLL_STATE_SETTLING -> {
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wrap = wrapWidth.toInt()
        val newWidthMS = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(wrap, MeasureSpec.EXACTLY)
        }
        val newHeightMS = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightMeasureSpec
            else -> MeasureSpec.makeMeasureSpec(wrap, MeasureSpec.EXACTLY)
        }
        super.onMeasure(newWidthMS, newHeightMS)
    }

    private val mPath1 = Path()
    private val mPath2 = Path()
    private val mPath3 = Path()
    private val mMatrix = Matrix()
    private val mBackgroundCirclePaint by lazy {
        val paint = Paint()
        paint.color = backgroundCircleColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint
    }
    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawBackgroundCircle(canvas)
        drawMovePath(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        onDrawBackground(canvas, width, height)
    }

    private fun drawBackgroundCircle(canvas: Canvas) {
        mPath1.reset()
        mPath2.reset()
        mPath3.reset()
        onDrawBackgroundCircle(mPath1, mPath2, mPath3, circleRadius)
        judgeStyle(
            horizontal = {
                mMatrix.setTranslate(frontMargin, wrapWidth / 2)
                mPath1.transform(mMatrix)
                mPath2.transform(mMatrix)
                mPath3.transform(mMatrix)
                canvas.drawPath(mPath1, mBackgroundCirclePaint)
                repeat(amount - 1) {
                    mMatrix.setTranslate(intervalMargin, 0F)
                    mPath1.transform(mMatrix)
                    mPath2.transform(mMatrix)
                    mPath3.transform(mMatrix)
                    canvas.drawPath(mPath1, mBackgroundCirclePaint)
                    canvas.drawPath(mPath2, mBackgroundCirclePaint)
                    canvas.drawPath(mPath3, mBackgroundCirclePaint)
                }
            },
            vertical = {
                mMatrix.setTranslate(wrapWidth / 2, frontMargin)
                mPath1.transform(mMatrix)
                mPath2.transform(mMatrix)
                mPath3.transform(mMatrix)
                canvas.drawPath(mPath1, mBackgroundCirclePaint)
                repeat(amount - 1) {
                    mMatrix.setTranslate(0F, intervalMargin)
                    mPath1.transform(mMatrix)
                    mPath2.transform(mMatrix)
                    mPath3.transform(mMatrix)
                    canvas.drawPath(mPath1, mBackgroundCirclePaint)
                    canvas.drawPath(mPath2, mBackgroundCirclePaint)
                    canvas.drawPath(mPath3, mBackgroundCirclePaint)
                }
            }
        )
    }

    private val mMovePathPaint by lazy {
        val paint = Paint()
        paint.color = circleColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint
    }
    private fun drawMovePath(canvas: Canvas) {
        mPath1.reset()
        mPath2.reset()
        mPath3.reset()
        onDrawMovePath(mPath1, mPath2, mPath3, circleRadius, offsetPixels, intervalMargin)
        val p = if (position >= idlePosition || positionFloat == 0F) position else position + 1
        judgeStyle(
            horizontal = {
                mMatrix.setTranslate(frontMargin + p * intervalMargin, wrapWidth / 2)
            },
            vertical = {
                mMatrix.setTranslate(wrapWidth / 2, frontMargin + p * intervalMargin)
                mMatrix.postRotate(90F, wrapWidth / 2, frontMargin + p * intervalMargin)
            }
        )
        mPath1.transform(mMatrix)
        mPath2.transform(mMatrix)
        mPath3.transform(mMatrix)
        canvas.drawPath(mPath1, mMovePathPaint)
        canvas.drawPath(mPath2, mMovePathPaint)
        canvas.drawPath(mPath3, mMovePathPaint)
    }

    private inline fun judgeStyle(horizontal: () -> Unit, vertical: () -> Unit) {
        when (outerGravity) {
            Indicators.OuterGravity.TOP,
            Indicators.OuterGravity.BOTTOM,
            Indicators.OuterGravity.HORIZONTAL_CENTER
            -> horizontal.invoke()
            Indicators.OuterGravity.LEFT,
            Indicators.OuterGravity.RIGHT,
            Indicators.OuterGravity.VERTICAL_CENTER
            -> vertical.invoke()
        }
    }

    /**
     * 用于绘制背景
     * @param width 自身 View 的宽
     * @param height 自身 View 的高
     */
    open fun onDrawBackground(canvas: Canvas, width: Int, height: Int) {
        // 对于 xml 中的 indicators_backgroundColor 属性, 实现方式是写在了 IIndicator 接口中
    }

    /**
     * 用于绘制背景的小圆点
     *
     * **NOTE：** 坐标会在内部转换, 只需修改 path, 每个圆的中心坐标为(0, 0), 且每个圆点都会以该 path
     * 来绘制(意思是你只需绘制一个小圆点即可). 有三个 path 只是用来绘制复杂图形使用
     */
    open fun onDrawBackgroundCircle(path1: Path, path2: Path, path3: Path, radius: Float) {
        path1.addCircle(0F, 0F, radius, Path.Direction.CCW)
    }

    /**
     * 用于在移动时绘制图形，请自己实现 path 的绘制，只需绘制一个区间的轨迹. 有三个 path 只是用来绘制复杂图形使用
     *
     * **NOTE：**
     *
     * 1、你只需要使用 offsetPixels 的值来绘制从 -intervalMargin 到 +intervalMargin 之间对应的 path 即可
     *
     * 2、参考系是水平的，坐标为 (-intervalMargin, 0) <---> (0, 0) <---> (+intervalMargin, 0)，
     *    在内部绘图时会自动对 path 进行旋转或移动来以一个路径而显示全部路径的圆点动画
     *
     * 3、看不懂? 那你去看我写的一些实现类 MoveIndicators
     *
     * @param offsetPixels 值只会在 -intervalMargin 到 +intervalMargin 之间
     * @param intervalMargin 两个圆点间的距离值
     */
    abstract fun onDrawMovePath(
        path1: Path,
        path2: Path,
        path3: Path,
        radius: Float,
        offsetPixels: Float,
        intervalMargin: Float
    )
}