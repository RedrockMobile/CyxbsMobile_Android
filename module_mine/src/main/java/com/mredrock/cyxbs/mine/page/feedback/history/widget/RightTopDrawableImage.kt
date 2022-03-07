/*
package com.mredrock.cyxbs.mine.page.feedback.history.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.mredrock.cyxbs.mine.R

*/
/**
 *@author ZhiQiang Tu
 *@time 2021/8/24  18:18
 *@signature 我们不明前路，却已在路上
 *//*

class RightTopDrawableImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    var drawable1:Drawable? = null

    init {
        val attrSets = context.obtainStyledAttributes(attrs, R.styleable.RightTopDrawableImage)

//        drawable1 = attrSets.getDrawable(R.styleable.RightTopDrawableImage_rightTopDrawable)
        drawable1 = ResourcesCompat.getDrawable(resources,R.drawable.mine_feedback_ic_cancel,null)

        attrSets.recycle()
    }

    val path = Path()

    override fun onDraw(canvas: Canvas?) {
        setPadding(0, dpToFloat(3f).toInt(), dpToFloat(3f).toInt(),0)
        drawable1?.setBounds((width-dpToFloat(10f)).toInt(),0,width, dpToFloat(10f).toInt())
        drawable1?.draw(canvas)
       */
/* path.quadTo()*//*

        super.onDraw(canvas)
    }

    private fun dpToFloat(float: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, float,
        resources.displayMetrics
    )

}*/
