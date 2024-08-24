package com.cyxbsmobile_single.module_todo.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import androidx.core.animation.doOnEnd
import com.cyxbsmobile_single.module_todo.R


/**
 * Author: RayleighZ
 * Time: 2021-08-02 14:34
 */
class CheckLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatCheckBox(context, attrs, defStyle) {
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }
    private var startAngle = 40f//左侧圆圈的起始角度
    private var curAnimeProcess = 0f//当前动画的进度，0~200，其中0~100表示圆圈的绘制，100~200表示线的绘制
    var checkedColor = 0
    var uncheckedColor = 0
    private var leftRadius = 0f
    private var isChecked = false
    private var sweepAngle = 0f
    private var selectAnime: ValueAnimator? = null
    private var unSelectAnime: ValueAnimator? = null
    private val leftCircleRectF by lazy { RectF() }
    private var firstEndOfAnime = true
    private var uncheckLineWidth = 0f
    private var checkedLineWidth = 0f
    private var withCheckLine = true

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
        uncheckLineWidth =
            typeArray.getDimension(R.styleable.CheckLineView_uncheck_line_width, 1.5f)
        checkedLineWidth =
            typeArray.getDimension(R.styleable.CheckLineView_checked_line_width, 1.2f)
        withCheckLine =
            typeArray.getBoolean(R.styleable.CheckLineView_with_check_line, true)
        startAngle =
            typeArray.getFloat(R.styleable.CheckLineView_start_angle, 40f)
        setStatusWithoutAnime(isChecked)
        typeArray.recycle()
        paint.style = Paint.Style.STROKE
        // 设置点击事件

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        when (curAnimeProcess) {
            in 0f..100f -> {
                drawLeftArc(curAnimeProcess, canvas)
            }
            in 101f..200f -> {
                if (isChecked) {
                    //如果已经check
                    drawLeftArc(100f, canvas)
                    //绘制左侧划线
                    if (withCheckLine){
                        canvas.drawLine(
                            leftRadius * 2 + paint.strokeWidth / 2f,
                            leftRadius + paint.strokeWidth / 2f,
                            (leftRadius * 2 + paint.strokeWidth / 2f) +
                                    (width.toFloat() - leftRadius * 2 + paint.strokeWidth / 2f)
                                    * (curAnimeProcess - 100) / 100,
                            leftRadius,
                            paint
                        )
                    }
                } else {
                    //如果尚未check（走到这个逻辑里多半是默认加载为没有check）
                    paint.color = uncheckedColor
                    paint.strokeWidth = uncheckLineWidth
                    drawLeftArc(100f, canvas)
                }
            }
        }
    }

    fun setStatusWithoutAnime(isChecked: Boolean) {
        curAnimeProcess = 200f
        this.isChecked = isChecked
    }

    fun setStatusWithAnime(isChecked: Boolean, onSuccess: (() -> Unit)? = null) {
        this.isChecked = isChecked
        if (isChecked) {
            selectAnime ?: let {
                selectAnime = ValueAnimator.ofFloat(0f, 200f).apply {
                    duration = 800
                    addUpdateListener {
                        curAnimeProcess = it.animatedValue as Float
                        invalidate()
                    }
                }
            }
            selectAnime?.doOnEnd {
                onSuccess?.invoke()
            }
            selectAnime?.start()
        } else {
            unSelectAnime ?: let {
                unSelectAnime = ValueAnimator.ofFloat(200f, 0f).apply {
                    duration = 800
                    addUpdateListener {
                        curAnimeProcess = it.animatedValue as Float
                        invalidate()
                    }
                }
            }
            unSelectAnime?.doOnEnd {
                onSuccess?.invoke()
            }
            unSelectAnime?.start()
            firstEndOfAnime = true
        }
    }

    private fun drawLeftArc(process: Float, canvas: Canvas) {
        //绘制左侧check圈圈
        paint.color = if (isChecked) checkedColor else uncheckedColor
        paint.strokeWidth =
            if (isChecked) checkedLineWidth else uncheckLineWidth
        //刷新右侧矩形
        leftCircleRectF.apply {
            top = 0f + paint.strokeWidth / 2f
            left = 0f + paint.strokeWidth / 2f
            right = leftRadius * 2 + paint.strokeWidth / 2f
            bottom = leftRadius * 2 + paint.strokeWidth / 2f
        }
        sweepAngle = if (isChecked) (360f - startAngle) * process / 100 else 360f
        canvas.drawArc(leftCircleRectF, startAngle, sweepAngle, false, paint)
    }
}