package com.mredrock.cyxbs.ufield.helper


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import android.util.TypedValue

/**
 *  description : 自定义的一个imageview 上面圆角 下面没有圆角
 *  author : lytMoon
 *  date : 2023/8/30 11:57
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */


class RoundedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val path = Path()
    private val rect = RectF()
    private val cornerRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10f, // 这里修改度数
        context.resources.displayMetrics
    )

    override fun onDraw(canvas: Canvas) {
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        path.reset()
        path.addRoundRect(rect, floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f), Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}