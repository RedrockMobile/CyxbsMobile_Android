package com.mredrock.cyxbs.discover.electricity.ui.widget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import java.lang.Math.cos
import java.lang.Math.min
import kotlin.math.PI
import kotlin.math.sin

/**
 * Create By Hosigus at 2019/3/26
 */
class ElectricityMeterView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var pointNum = 21
    private var lightPointNum = 0

    private val centerRate = 0.2412f
    private val outerORate = 0.3926f
    private val outerIRate = 0.3728f
    private val outerLinkRate = 0.0110f
    private val smallPRate = 0.0123f
    private val bigPRate = 0.0185f
    private val outerAngle = 65f
    private val pointAngle = 18 - outerAngle / (pointNum - 1)

    private var maxSize = -1
    private var center = -1f
    private var centerR = -1f
    private var outerLinkR = -1f
    private var outerLinkY = -1f
    private var outerLinkXMove = -1f
    private var smallPR = -1f
    private var smallPD = -1f
    private var bigPRAdd = -1f

    private var bigPointProgress = -1f

    private val surroundORect = RectF()
    private val surroundIRect = RectF()

    private val ringColor = Color.parseColor("#F0F6FF")
    private val pointLevelColor =
            arrayOf(
                    Color.parseColor("#f5f5f5"),
                    Color.parseColor("#bdddff"),
                    Color.parseColor("#859AF6"),
                    Color.parseColor("#7974e3"),
                    Color.parseColor("#7142b6"))
    private var darkPointColor = pointLevelColor[0]
    private var lightPointColor = pointLevelColor[1]
    private var bigPointColor = pointLevelColor[2]

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }
    private val path = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)

        maxSize = min(w, h)
        initSize()

        setMeasuredDimension(maxSize, maxSize)
    }

    private fun initSize() {
        center = maxSize / 2f

        centerR = maxSize * centerRate
        outerLinkR = maxSize * outerLinkRate
        smallPR = maxSize * smallPRate
        bigPRAdd = maxSize * bigPRate - smallPR

        val surroundOR = maxSize * outerORate
        val topAndStartO = center - surroundOR
        val bottomAndEndO = center + surroundOR
        surroundORect.set(topAndStartO, topAndStartO, bottomAndEndO, bottomAndEndO)

        val surroundIR = maxSize * outerIRate
        val topAndStartI = center - surroundIR
        val bottomAndEndI = center + surroundIR
        surroundIRect.set(topAndStartI, topAndStartI, bottomAndEndI, bottomAndEndI)

        val outerLinkD = (surroundOR + surroundIR) / 2
        outerLinkY = center + outerLinkD * cos(rad(outerAngle / 2)).toFloat()
        outerLinkXMove = outerLinkD * sin(rad(outerAngle / 2)).toFloat()
        smallPD = (centerR + outerLinkD) / 2
    }

    private fun rad(angle: Float) = angle / 180 * PI

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        paint.color = ringColor
        canvas.drawCircle(center, center, centerR, paint)

        path.reset()
        path.arcTo(surroundORect, 90 - outerAngle / 2, outerAngle - 360)
        path.arcTo(surroundIRect, 90 + outerAngle / 2, 360 - outerAngle)
        canvas.drawCircle(center - outerLinkXMove, outerLinkY, outerLinkR, paint)
        canvas.drawCircle(center + outerLinkXMove, outerLinkY, outerLinkR, paint)
        canvas.drawPath(path, paint)

        repeat(pointNum) {
            paint.color = when {
                it < lightPointNum -> lightPointColor
                else -> darkPointColor
            }
            val angle = pointAngle * it + outerAngle / 2
            canvas.drawCircle(center - smallPD * sin(rad(angle)).toFloat(), center + smallPD * cos(rad(angle)).toFloat(), smallPR, paint)
        }

        if (bigPointProgress != -1f) {
            paint.color = bigPointColor
            val angle = pointAngle * lightPointNum + outerAngle / 2
            canvas.drawCircle(center - smallPD * sin(rad(angle)).toFloat(), center + smallPD * cos(rad(angle)).toFloat(), smallPR + bigPointProgress * bigPRAdd, paint)
        }

    }

    fun refresh(used: Int, fullCircle: Int = 100) {
        val eachLevel = fullCircle / (pointNum - 1)
        val levelCount = when {
            used < eachLevel * 2 -> 2
            used > fullCircle * 3 -> pointNum * 3 - 1
            else -> used / eachLevel
        }

        var level = 0
        darkPointColor = pointLevelColor[level]
        lightPointColor = pointLevelColor[level + 1]
        bigPointColor = pointLevelColor[level + 2]

        AnimatorSet().apply {
            play(
                    ValueAnimator.ofInt(1, levelCount).apply {
                        duration = levelCount / pointNum * 3000L + levelCount % pointNum * 150L
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            lightPointNum = it.animatedValue as Int
                            if (lightPointNum > pointNum) {
                                if (lightPointNum / pointNum != level) {
                                    level++
                                    darkPointColor = pointLevelColor[level]
                                    lightPointColor = pointLevelColor[level + 1]
                                    bigPointColor = pointLevelColor[level + 2]
                                }
                                lightPointNum %= pointNum
                            }
                            postInvalidate()
                        }
                    }
            ).before(
                    ValueAnimator.ofFloat(0f, 1f).apply {
                        duration = 300L
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            bigPointProgress = it.animatedValue as Float
                            postInvalidate()
                        }
                    }
            )
        }.start()
    }

}