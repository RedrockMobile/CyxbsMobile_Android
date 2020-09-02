package com.mredrock.cyxbs.discover.map.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.mredrock.cyxbs.discover.map.R
import kotlin.math.atan

/**
 * @author Jovines
 * @create 2020-02-21 1:28 PM
 *
 *
 * 描述:课表头部可拉动提示，别问为什么要写这个动态的东西
 *      问的话那还要从一只蝙蝠说起（闲的）
 *      具体描述一下，这是一个会从平的提示小条变化到上箭头或者下箭头的自定义View
 *      本来是用在课表的头部中心的，但是发现在BottomSheet上会
 *      出现性能问题，所以就放弃使用了
 */
internal class RedRockTipsView : View {

    companion object {
        const val UP = 0
        const val CENTER = 1
        const val BOTTOM = 2
    }

    private val paint: Paint = Paint().apply {
        color = 0xff000000.toInt()
    }
    private val leftPath: Path = Path()
    private val rightPath: Path = Path()
    private var tipColor: Int = 0xff000000.toInt()
    var position = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 使用这个View只用设置相应的状态就好了
     */
    var state = CENTER
        set(value) {
            field = value
            when (value) {
                UP -> topWayAnimation.start()
                CENTER -> centerWayAnimation.start()
                BOTTOM -> bottomWayAnimation.start()
            }
        }
    // 因为这三个动画不一定会用到所以懒加载,
    // 还有这个这三个动画需要根据开启动画时position来展示动画，所以重写了start
    /**
     * 将该view的状态从向上指或者向下指的状态恢复到平的状态
     */
    private val centerWayAnimation: ValueAnimator by lazy(LazyThreadSafetyMode.NONE) {
        val animation = object : ValueAnimator() {
            override fun start() {
                this.setFloatValues(position, 0f)
                super.start()
            }
        }
        animSetting(animation)
    }

    /**
     * 将状态变化成向下指
     */
    private val bottomWayAnimation: ValueAnimator by lazy(LazyThreadSafetyMode.NONE) {
        val animation = object : ValueAnimator() {
            override fun start() {
                this.setFloatValues(position, -1f)
                super.start()
            }
        }
        animSetting(animation)
    }

    /**
     * 将状态变化成向上指
     */
    private val topWayAnimation: ValueAnimator by lazy(LazyThreadSafetyMode.NONE) {
        val animation = object : ValueAnimator() {
            override fun start() {
                this.setFloatValues(position, 1f)
                super.start()
            }
        }
        animSetting(animation)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
    ) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.RedRockTipsView,
                R.attr.RedRockTipsViewStyle, 0)
        tipColor = typeArray.getColor(R.styleable.RedRockTipsView_tipsColor, 0xff000000.toInt())
        typeArray.recycle()
        init()
    }

    private fun init() {
        paint.color = tipColor
    }

    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {

        init()
    }


    override fun onDraw(canvas: Canvas) {
        val heightSpan = height / 8f
        val add = ((height.toDouble() / width.toDouble()) * (heightSpan) * position).toFloat()
        val radian = atan((height.toFloat() / width.toFloat()) * position)
        val angle = -Math.toDegrees(radian.toDouble()).toFloat()
        val distance = (-(heightSpan * 2)) * position
        canvas.save()
        canvas.translate(0f, distance)
        canvas.rotate(angle, width / 2f, height / 2f)
        leftPath.moveTo(0f + 20, heightSpan * 4f)
        leftPath.quadTo(0f + 20, heightSpan * 3, 0f + 20 + heightSpan * 1f, heightSpan * 3)
        leftPath.lineTo((width.toFloat() / 2) + add, heightSpan * 3)
        leftPath.lineTo((width.toFloat() / 2) - add, heightSpan * 5)
        leftPath.lineTo(0f + 20 + heightSpan * 1f, heightSpan * 5)
        leftPath.quadTo(0f + 20, heightSpan * 5, 0f + 20, heightSpan * 4f)
        canvas.drawPath(leftPath, paint)
        canvas.restore()
        canvas.translate(0f, distance)
        canvas.rotate(-angle, width / 2f, height / 2f)
        rightPath.moveTo((width.toFloat() / 2) - add, heightSpan * 3)
        rightPath.lineTo(width - 20f - heightSpan * 1f, heightSpan * 3)
        rightPath.quadTo(width - 20f, heightSpan * 3, width - 20f, heightSpan * 4f)
        rightPath.lineTo(width - 20f, heightSpan * 4f)
        rightPath.quadTo(width - 20f, heightSpan * 5, width - 20f - heightSpan * 1f, heightSpan * 5)
        rightPath.lineTo((width.toFloat() / 2) + add, heightSpan * 5)
        canvas.drawPath(rightPath, paint)
    }


    private fun animSetting(animation: ValueAnimator): ValueAnimator {
        animation.interpolator = DecelerateInterpolator()
        animation.duration = 500
        animation.addUpdateListener {
            position = it.animatedValue as Float
        }
        return animation
    }

}