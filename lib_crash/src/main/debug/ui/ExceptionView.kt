package ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.Button

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/8/8
 * @Description:
 */
@SuppressLint("AppCompatCustomView")
class ExceptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyleAttr: Int = 0
) : Button(context, attrs, defaultStyleAttr) {
    var openException = false
    init {
        setOnClickListener {
            openException = true
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("RQ", "onDraw: view异常测试")
        if (openException) throw RuntimeException("view OnDraw绘制异常")
    }

}