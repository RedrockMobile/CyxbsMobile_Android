package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.utils.extensions.dip
import com.mredrock.cyxbs.course.R
import kotlin.math.max
import kotlin.math.sqrt


/**
 * @author Jovines
 * @create 2020-01-26 2:36 PM
 *
 *
 * 描述:课表中事务的背景View，
 * 别问为什么不用图片，问就是图片太麻烦，而且效果还不好
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
        paint.color = SkinManager.getColor("common_transaction_background_stripe_color",
        R.color.common_transaction_background_stripe_color)
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

    override fun onDraw(canvas: Canvas) {
        wholeRectF.set(-(width / 2f), (height / 2f), width / 2f, -(height / 2f))
        mPath.addRoundRect(wholeRectF, 16f, 16f, Path.Direction.CCW)
        val drawEdge = max(width, height) * sqrt(2.0)
        val space = context.dip(8)
        val num = drawEdge / (space * 2)
        canvas.translate(width / 2f, height / 2f)
        canvas.clipPath(mPath)
        canvas.rotate(45f)
        rectF.set(-(drawEdge / 2).toFloat(), (drawEdge / 2).toFloat(), ((-drawEdge / 2) + space).toFloat(), (-(drawEdge / 2f)).toFloat())
        for (i in 0 until num.toInt()) {
            canvas.drawRect(rectF, paint)
            rectF.set(rectF.left + (space * 2), rectF.top, rectF.right + (space * 2), rectF.bottom)
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