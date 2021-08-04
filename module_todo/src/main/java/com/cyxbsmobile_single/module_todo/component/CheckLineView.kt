package com.cyxbsmobile_single.module_todo.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.common.utils.extensions.dip


/**
 * Author: RayleighZ
 * Time: 2021-08-02 14:34
 */
class CheckLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val paint by lazy { Paint().apply { isAntiAlias = true } }
    private val startAngle = 40f//左侧圆圈的起始角度
    private var curAnimeProcess = 0f//当前动画的进度，0~200，其中0~100表示圆圈的绘制，100~200表示线的绘制
    private var checkedColor = 0
    private var uncheckedColor = 0
    private var leftRadius = 0f
    private var isChecked = false
    private var sweepAngle = 0f
    private var selectAnime: ValueAnimator? = null
    private var unSelectAnime: ValueAnimator? = null
    private val leftCircleRectF by lazy { RectF() }

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CheckLineView)
        checkedColor = typeArray.getColor(
            R.styleable.CheckLineView_checked_line_color,
            Color.parseColor("#15315B")
        )
        uncheckedColor = typeArray.getColor(
            R.styleable.CheckLineView_unchecked_line_color,
            Color.parseColor("#5215315B")
        )
        isChecked = typeArray.getBoolean(R.styleable.CheckLineView_is_checked, false)
        leftRadius = typeArray.getDimension(R.styleable.CheckLineView_left_radius, 0f)
        typeArray.recycle()
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        when (curAnimeProcess) {
            in 0f..100f -> {
                drawLeftArc(curAnimeProcess, canvas)
            }
            in 100f..200f -> {
                if (isChecked) {
                    //如果已经check
                    drawLeftArc(100f, canvas)
                    //绘制左侧划线
                    canvas.drawLine(
                        leftRadius * 2 + paint.strokeWidth / 2f,
                        leftRadius + paint.strokeWidth / 2f,
                        width.toFloat() * (curAnimeProcess - 100) / 100,
                        leftRadius,
                        paint
                    )
                } else {
                    //如果尚未check（走到这个逻辑里多半是默认加载为没有check）
                    paint.color = uncheckedColor
                    paint.strokeWidth = context.dip(1.5f).toFloat()
                    drawLeftArc(100f, canvas)
                }
            }
        }
    }

    private fun setStatusWithoutAnime(isChecked: Boolean) {
        curAnimeProcess = 200f
        this.isChecked = isChecked
    }

    private fun setStatusWithAnime(isChecked: Boolean){
        this.isChecked = isChecked
        if (isChecked){
            selectAnime ?: let {
                selectAnime = ValueAnimator.ofFloat(0f, 200f).apply {
                    duration = 1000
                    interpolator = OvershootInterpolator()
                    addUpdateListener {
                        curAnimeProcess = it.animatedValue as Float
                    }
                }
            }
            selectAnime?.start()
        } else {
            unSelectAnime ?: let {
                unSelectAnime = ValueAnimator.ofFloat(200f, 0f).apply {
                    duration = 1000
                    interpolator = OvershootInterpolator()
                    addUpdateListener {
                        curAnimeProcess = it.animatedValue as Float
                    }
                }
            }
            unSelectAnime?.start()
        }
    }

    private fun drawLeftArc(process: Float, canvas: Canvas) {
        //绘制左侧check圈圈
        paint.color = checkedColor
        paint.strokeWidth = context.dip(1).toFloat()
        //刷新右侧矩形
        leftCircleRectF.apply {
            top = 0f + paint.strokeWidth
            left = 0f + paint.strokeWidth
            right = leftRadius * 2 + paint.strokeWidth
            bottom = leftRadius * 2 + paint.strokeWidth
        }
        sweepAngle = (360f - startAngle) * process / 100
        canvas.drawArc(leftCircleRectF, startAngle, 360f - startAngle, false, paint)
    }
}