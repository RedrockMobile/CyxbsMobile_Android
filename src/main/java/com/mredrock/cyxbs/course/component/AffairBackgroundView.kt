package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.dip
import kotlin.math.max
import kotlin.math.sqrt


/**
 * @author jon
 * @create 2020-01-26 2:36 PM
 *
 *
 * 描述:
 */
internal class AffairBackgroundView : View {

    private var screenWidth: Int? = null
    private var screenHeight: Int? = null
    private val paint = Paint()
    private val rectF = RectF()
    private val wholeRectF = RectF()
    private val mPath = Path()


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
            context,
            attrs
    ) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.AffairBackgroundView,
                R.attr.ScheduleViewStyle, 0)
        paint.color = typeArray.getColor(R.styleable.AffairBackgroundView_backgroundStripeColor, 0xFFE4E7EC.toInt())
        typeArray.recycle()
        init()
    }

    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        screenHeight = getScreenHeight(context)
        screenWidth = getScreenWidth(context)
    }

    override fun onDraw(canvas: Canvas?) {
        wholeRectF.set(-(width / 2f), (height / 2f), width / 2f, -(height / 2f))
        mPath.addRoundRect(wholeRectF, 16f, 16f, Path.Direction.CCW)
        val drawEdge = max(width, height) * sqrt(2.0)
        val space = dip(8)
        val num = drawEdge / (space * 2)
        canvas?.let { canvas1 ->
            canvas1.translate(width / 2f, height / 2f)
            canvas1.clipPath(mPath)
            canvas1.rotate(45f)
            rectF.set(-(drawEdge / 2).toFloat(), (drawEdge / 2).toFloat(), ((-drawEdge / 2) + space).toFloat(), (-(drawEdge / 2f)).toFloat())
            for (i in 0 until num.toInt()) {
                canvas1.drawRect(rectF, paint)
                rectF.set(rectF.left + (space * 2), rectF.top, rectF.right + (space * 2), rectF.bottom)
            }
        }
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    /**
     * Get Screen Width
     */
    private fun getScreenWidth(context: Context): Int {
        return getDisplayMetrics(context).widthPixels
    }

    /**
     * Get Screen Height
     */
    private fun getScreenHeight(context: Context): Int {
        return getDisplayMetrics(context).heightPixels
    }
}