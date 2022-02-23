package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.course.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Jovines
 * @date 2019/11/28 12:08
 * description：标记一天的时间
 */
class TimeIdentificationView : View {
    //时间参数，固定“时”以上
    private val pattern = "yyyy-MM-dd HH:mm"
    private val specificDate = "2019-11-28 "//这个日期没啥意义，我就是选的创建这个类的时间
    private var simpleDateFormat = SimpleDateFormat(pattern, Locale("chi (B)"))

    //上午上课时间
    private val amStartTimeStamp = simpleDateFormat.parse("${specificDate}8:00").time
    private val amEndTimeStamp = simpleDateFormat.parse("${specificDate}11:55").time

    //下午上课时间
    private val pmStartTimeStamp = simpleDateFormat.parse("${specificDate}14:00").time
    private val pmEndTimeStamp = simpleDateFormat.parse("${specificDate}17:55").time


    //晚上上课时间
    private val nightStartTimeStamp = simpleDateFormat.parse("${specificDate}19:00").time
    private val nightEndTimeStamp = simpleDateFormat.parse("${specificDate}22:30").time


    //当前时间，占今天上课时间总体的百分比
    private var percentage: Float = 0f

    //标识的颜色
    private var color = 0x2A4E84

    //单位是dp
    private val horizontalLineLength = 25

    //这个量完全是为了配合以前文学姐的逻辑
    var position: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    private lateinit var paint: Paint
    private lateinit var rectF: RectF


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(
            context,
            attrs
    ) {

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.TimeIdentificationView,
                R.attr.TimeIdentificationViewStyle, 0)
        color = typeArray.getColor(R.styleable.TimeIdentificationView_color, 0x2A4E84)
        typeArray.recycle()
        paint = Paint().apply {
            color = this@TimeIdentificationView.color
        }
        rectF = RectF()

    }

    constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        position ?: return
        update()
        val cx = measuredWidth - context.dip(horizontalLineLength).toFloat()
        val radius = 3f
        val cy = ((measuredHeight - 2f * context.dip(radius)) / measuredHeight) * (measuredHeight * percentage) + context.dip(radius)
        paint.isAntiAlias = true
        canvas.drawCircle(cx, cy, context.dip(radius).toFloat(), paint)
        rectF.set(cx, cy - context.dip(0.5f), cx +context.dip(horizontalLineLength), cy + context.dip(0.5f))
        canvas.drawRect(rectF, paint)
    }

    /**
     * 获取当前时间戳
     */
    private fun getNowTime(): Long = simpleDateFormat.parse(
            "${specificDate}${simpleDateFormat.format(Date()).substringAfter(" ")}"
    ).time

    /**
     * 更新百分比
     */
    private fun update() {
        val now = getNowTime()
        percentage = if (now > this.amStartTimeStamp && now < this.amEndTimeStamp) {
            ((now - amStartTimeStamp).toFloat() / (amEndTimeStamp - amStartTimeStamp).toFloat()) * (1f / 3f)
        } else if (now > this.amEndTimeStamp && now < this.pmStartTimeStamp) {
            1f / 3
        } else if (now > this.pmStartTimeStamp && now < this.pmEndTimeStamp) {
            ((now - pmStartTimeStamp).toFloat() / (pmEndTimeStamp - pmStartTimeStamp).toFloat()) * (1f / 3f) + (1f / 3f)
        } else if (now > this.pmEndTimeStamp && now < this.nightStartTimeStamp) {
            2f / 3
        } else if (now > this.nightStartTimeStamp && now < this.nightEndTimeStamp) {
            ((now - nightStartTimeStamp).toFloat() / (nightEndTimeStamp - nightStartTimeStamp).toFloat()) * (1f / 3f) + (2f / 3f)
        } else {
            1f
        }
    }
}