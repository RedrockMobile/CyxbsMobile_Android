package com.mredrock.cyxbs.noclass.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.noclass.R

class MyVpIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var allDotNumber: Int = 0
    private var activeDotNumber: Int = 0
    private var activeDotColor: Int = Color.BLACK
    private var inActiveDotColor: Int = Color.WHITE
    private var dotSpace: Int = 10   //点和点之间的空间
    private var dotRadius: Float = 7F  //点的半径
    private var id: Int = 0

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MyVpIndicatorView)
        id = typeArray.getInteger(R.styleable.MyVpIndicatorView_android_id, 0)
        activeDotColor = typeArray.getColor(R.styleable.MyVpIndicatorView_active_color, Color.BLACK)
        inActiveDotColor =
            typeArray.getColor(R.styleable.MyVpIndicatorView_inactive_color, Color.WHITE)
        typeArray.recycle()
    }

    //设置当前的点数
    fun setCurrentDot(number: Int) {
        activeDotNumber = number
        invalidate()  //重新绘画
    }

    // 设置总共的点数
    fun setAllDot(number: Int) {
        allDotNumber = number
        invalidate()  //重新绘画
    }

    fun getAllDot(): Int {
        return if (allDotNumber > 0 ) allDotNumber else 1
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        //定义点所需要的空间
        val activePaint = Paint().apply {
            color = activeDotColor
        }
        val inActivePaint = Paint().apply {
            color = inActiveDotColor
        }
        val contextWidth = allDotNumber * (2 * dotRadius) + (allDotNumber - 1) * dotSpace
        var startX: Float = ((width.toFloat() - contextWidth) / 2)   //第一个圆点左边
        var startY = (height.toFloat() - dotRadius * 2) / 2   //圆点的上边边
        for (i in 0 until allDotNumber) {
            startX += dotRadius  //第一个圆点在x轴的中心位置
            val dy = startY + dotRadius  //第一个圆点在y轴的中心位置
            if (i != activeDotNumber) {  //开始画图
                canvas.drawCircle(startX, dy, dotRadius, inActivePaint)    //画⚪
            } else {
                canvas.drawCircle(startX, dy, dotRadius, activePaint)
            }
            startX += dotRadius + dotSpace  //画完之后到第二个圆点的最左边
        }
    }
}