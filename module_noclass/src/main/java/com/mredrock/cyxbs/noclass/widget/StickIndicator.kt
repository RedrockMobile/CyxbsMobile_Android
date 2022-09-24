package com.mredrock.cyxbs.noclass.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.noclass.R
import java.lang.Integer.min




/*
 *

 */
/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.widget
 * @ClassName:      StickIndicator
 * @Author:         Yan
 * @CreateDate:     2022年08月26日 03:29:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    可滚动的粘性指示器
 */
class StickIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 动画时长
     */
    private var animTime : Long = 500

    /**
     * 最大展示数量（当前总数超过最大展示数量时，自动滑动整组小圆点）
     */
    private var showCount: Int = 5

    /**
     * 小圆点之间的间距
     */
    private var dotPadding = 5f.dp2pxF

    /**
     * 当前选中的小圆点的宽度
     */
    private var bigDotWidth = 6f.dp2pxF

    /**
     * 默认小圆点的宽度
     */
    private var smallDotWidth = 5f.dp2pxF

    /**
     * 当前选中的小圆点的颜色
     */
    private var selectColor = Color.parseColor("#4A44E4")

    /**
     * 默认小圆点的颜色
     */
    private var defaultColor = Color.parseColor("#ABBCD8")

    /**
     * 是否开启粘性动画
     */
    private var isStickEnable = true

    private var curIndex = 0
    private var totalCount = 0
    private var lastX = 0f
    private var curX = 0f

    private lateinit var scrollAnimator: ValueAnimator
    private lateinit var stickAnimator: ValueAnimator

    private var paint: Paint = Paint()
    private var selectRectF: RectF = RectF()

    private var stickPath: Path = Path()
    private var stickAnimX = 0f
    private var isSwitchFinish = true

    init {
        initAttrs(attrs)
        initView()
    }

    private fun initAttrs(attrs: AttributeSet?){
        if (attrs != null){
            val ta = context.obtainStyledAttributes(attrs, R.styleable.StickIndicator)
            animTime = ta.getInteger(R.styleable.StickIndicator_noclass_indicator_anim_time, animTime.toInt()).toLong()
            dotPadding = ta.getDimension(R.styleable.StickIndicator_noclass_indicator_dot_padding, dotPadding)
            smallDotWidth = ta.getDimension(R.styleable.StickIndicator_noclass_indicator_small_dot_width, smallDotWidth)
            bigDotWidth = ta.getDimension(R.styleable.StickIndicator_noclass_indicator_big_dot_width, bigDotWidth)
            selectColor = ta.getColor(R.styleable.StickIndicator_noclass_indicator_select_color, selectColor)
            defaultColor = ta.getColor(R.styleable.StickIndicator_noclass_indicator_default_color, defaultColor)
            showCount = ta.getInteger(R.styleable.StickIndicator_noclass_indicator_show_count, showCount)
            isStickEnable = ta.getBoolean(R.styleable.StickIndicator_noclass_indicator_stick_enable, isStickEnable)
            ta.recycle()
        }
    }

    private fun initView(){
        scrollAnimator = ValueAnimator().apply {
            duration = animTime
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation: ValueAnimator ->
                curX = animation.animatedValue as Float
                invalidate()
            }
        }

        stickAnimator = ValueAnimator().apply {
            duration = animTime
            interpolator = OvershootInterpolator(0.675F)
            addUpdateListener { animation: ValueAnimator ->
                stickAnimX = animation.animatedValue as Float
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = min(totalCount,showCount)
        val width = ((count - 1) * smallDotWidth + (count - 1) * dotPadding + bigDotWidth).toInt()
        val height = bigDotWidth.toInt()
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getMode(heightMeasureSpec)

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height)
        } else {
            setMeasuredDimension(width, height)
        }
        requestLayout()
    }

    fun setCurIndex(index: Int) {
//        如果添加一下判断就会导致滑动过快会滑不回来
//        if (index == curIndex) {
//            return
//        }
        //当前滑动到的新index 处于左右两边2个之外的区域
        if (totalCount > showCount) {
            if (index > curIndex) {
                //往左边滑动
                //判断是否需要先滚动
                val start = if (showCount % 2 == 0) showCount / 2 - 1 else showCount / 2
                val end = totalCount - showCount / 2
                if (index in (start + 1) until end) {
                    startScrollAnim(Direction.LEFT) { invalidateIndex(index) }
                } else {
                    invalidateIndex(index)
                }
            } else {
                //往右边滑动
                val start = showCount / 2
                val end =
                    if (showCount % 2 == 0) totalCount - showCount / 2 + 1 else totalCount - showCount / 2
                if (index > start - 1 && index < end - 1) {
                    startScrollAnim(Direction.RIGHT) { invalidateIndex(index) }
                } else {
                    invalidateIndex(index)
                }
            }
        } else {
            invalidateIndex(index)
        }
    }

    fun setTotalCount(count: Int) {
        if (totalCount in showCount until count && curIndex >= totalCount - 2) {
            startScrollAnim(Direction.LEFT) {
                invalidateTotal(count)
            }
            return
        }
        if (totalCount > showCount && curX < 0 && curIndex >= totalCount - 2) {
            //超过最大展示数量，且当前有移动距离
            curX += dotPadding + smallDotWidth
            lastX = curX
            invalidateTotal(count)
            return
        }
        invalidateTotal(count)
    }

    fun reset() {
        totalCount = 0
        curIndex = 0
        curX = 0f
        lastX = 0f
    }

    private fun invalidateTotal(count: Int) {
        totalCount = count
        requestLayout()
        invalidate()
    }

    private fun invalidateIndex(index: Int) {
        if (!isStickEnable) {
            curIndex = index
            invalidate()
            return
        }
        if (stickAnimator.isRunning) {
            stickAnimator.end()
        }
        val startValues: Float = getCurIndexX() + bigDotWidth / 2
        if (index > curIndex) {
            stickAnimator.setFloatValues(startValues, startValues + dotPadding + smallDotWidth)
        } else {
            stickAnimator.setFloatValues(startValues, startValues - dotPadding - smallDotWidth)
        }
        isSwitchFinish = false
        stickAnimator.removeAllListeners()
        stickAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                isSwitchFinish = true
                curIndex = index
                invalidate()
            }
        })
        stickAnimator.start()
    }


    private fun startScrollAnim(direction: Direction, onScrollEndListener : () -> Unit) {
        if (scrollAnimator.isRunning) {
            scrollAnimator.end()
        }
        scrollAnimator.duration = animTime
        val startValues = curX
        val endValues =
            if (direction === Direction.LEFT) curX - dotPadding - smallDotWidth else curX + dotPadding + smallDotWidth
        scrollAnimator.setFloatValues(startValues, endValues)
        scrollAnimator.removeAllListeners()
        scrollAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                lastX = curX
                invalidate()
            }

            override fun onAnimationEnd(animation: Animator?) {
                onScrollEndListener.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
                curX = lastX
                invalidate()
            }
        })
        scrollAnimator.start()
    }

    /**
     * 获取当前选中下标的X坐标
     */
    private fun getCurIndexX(): Float {
        if (curX < 0) {
            val translateCount = (-curX / (smallDotWidth + dotPadding)).toInt()
            return (curIndex - translateCount) * (smallDotWidth + dotPadding)
        }
        return curIndex * (smallDotWidth + dotPadding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var startX = curX
        var selectX = 0f
        for (i in 0 until totalCount) {
            if (curIndex == i) {
                paint.color = selectColor
                paint.style = Paint.Style.FILL
                paint.strokeWidth = 2f
                selectRectF.left = startX
                selectRectF.top = height / 2f - bigDotWidth / 2
                selectRectF.right = startX + bigDotWidth
                selectRectF.bottom = height / 2f + bigDotWidth / 2
                canvas.drawCircle(startX + bigDotWidth / 2, bigDotWidth / 2, bigDotWidth / 2, paint)
                selectX = startX + bigDotWidth / 2
                startX += bigDotWidth + dotPadding
            } else {
                paint.color = defaultColor
                paint.style = Paint.Style.FILL
                paint.strokeWidth = 2f
                startX += smallDotWidth / 2
                canvas.drawCircle(startX, bigDotWidth / 2, smallDotWidth / 2, paint)
                startX += smallDotWidth / 2 + dotPadding
            }
        }
        if (isStickEnable) {
            if (isSwitchFinish) {
                stickPath.reset()
            } else {
                paint.color = selectColor
                val quadStartX = selectX
                val quadStartY = height / 2f - bigDotWidth / 2
                stickPath.reset()
                stickPath.moveTo(quadStartX, quadStartY)
                stickPath.quadTo(
                    quadStartX + (stickAnimX - quadStartX) / 2,
                    bigDotWidth / 2,
                    stickAnimX,
                    quadStartY
                )
                stickPath.lineTo(stickAnimX, quadStartY + bigDotWidth)
                stickPath.quadTo(
                    quadStartX + (stickAnimX - quadStartX) / 2,
                    bigDotWidth / 2,
                    quadStartX,
                    quadStartY + bigDotWidth
                )
                stickPath.close()
                canvas.drawCircle(stickAnimX, bigDotWidth / 2, bigDotWidth / 2, paint)
                canvas.drawPath(stickPath, paint)
            }
        }
    }

    /**
     * 滑动方向
     */
    private enum class Direction {
        LEFT, RIGHT
    }

}