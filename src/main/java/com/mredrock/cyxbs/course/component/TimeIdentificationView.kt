package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.dip
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Jon
 * @date 2019/11/28 12:08
 * description：标记一天的时间
 */
class TimeIdentificationView : View {
    //时间参数，固定“时”以上
    private val pattern = "yyyy-MM-dd HH:mm"
    private val specificDate = "2019-11-28 "//这个日期没啥意义，我就是选的创建这个类的时间
    private val startingTime = "${specificDate}8:00"
    private val endTime = "${specificDate}22:30"
    private var simpleDateFormat = SimpleDateFormat(pattern)

    //一天的起始时间戳，和结束时间戳
    private val startTimeStamp = simpleDateFormat.parse(startingTime).time
    private val endTimeStamp = simpleDateFormat.parse(endTime).time

    //中午休息时间戳
    private val noonStartTimeStamp = simpleDateFormat.parse("${specificDate}11:55").time
    private val noonEndTimestamp = simpleDateFormat.parse("${specificDate}14:00").time

    //下午休息时间戳
    private val pmStartTimeStamp = simpleDateFormat.parse("${specificDate}14:00").time
    private val pmEndTimeStamp = simpleDateFormat.parse("${specificDate}14:00").time


    //当前时间，占今天上课时间总体的百分比
    private var percentage: Float = 0f

    //标识的颜色
    private var color = 0x2A4E84

    //单位是dp
    private val horizontalLineLength = 25

    var position: Int? = null

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
        val cx = measuredWidth - dip(horizontalLineLength).toFloat()
        val radius = 3f
        val cy = ((measuredHeight - 2f * dip(radius)) / measuredHeight) * (measuredHeight * percentage) + dip(radius)
        paint.isAntiAlias = true
        canvas.drawCircle(cx, cy, dip(radius).toFloat(), paint)
        rectF.set(cx, cy - dip(0.5f), cx + dip(horizontalLineLength), cy + dip(0.5f))
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
        percentage = if (now > this.startTimeStamp && now < this.noonStartTimeStamp) {
            ((now - startTimeStamp).toFloat() / (noonStartTimeStamp - startTimeStamp).toFloat()) * (1f / 3)
        } else if (now > this.noonStartTimeStamp && now < this.noonEndTimestamp) {
            1f / 3
        } else if (now > this.noonEndTimestamp && now < this.pmStartTimeStamp) {
            ((now - noonEndTimestamp).toFloat() / (pmStartTimeStamp - noonEndTimestamp).toFloat()) * (2f / 3)
        } else if (now > this.pmStartTimeStamp && now < this.pmEndTimeStamp) {
            2f / 3
        } else if (now > this.pmEndTimeStamp && now < this.endTimeStamp) {
            ((now - startTimeStamp).toFloat() / (endTimeStamp - startTimeStamp).toFloat())
        } else {
            0f
        }
    }
}