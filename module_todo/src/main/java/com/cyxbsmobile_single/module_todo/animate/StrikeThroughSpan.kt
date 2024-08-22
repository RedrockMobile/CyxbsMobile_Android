//import android.animation.ObjectAnimator
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.text.SpannableString
//import android.text.TextPaint
//import android.text.style.ReplacementSpan
//import android.util.Log
//import android.widget.TextView
//
///**
// * @Project: CyxbsMobile_Android
// * @File: StrikeThroughSpan
// * @Author: 86199
// * @Date: 2024/8/18
// * @Description: 实现勾选动画
// */
//
//
//class StrikeThroughSpan : ReplacementSpan() {
//    private var strikeThroughWidth: Float = 0f
//
//    fun setStrikeThroughWidth(width: Float) {
//        this.strikeThroughWidth = width
//    }
//
//    override fun getSize(
//        paint: Paint,
//        text: CharSequence?,
//        start: Int,
//        end: Int,
//        fm: Paint.FontMetricsInt?
//    ): Int {
//        return paint.measureText(text, start, end).toInt()
//    }
//
//    override fun draw(
//        canvas: Canvas,
//        text: CharSequence?,
//        start: Int,
//        end: Int,
//        x: Float,
//        top: Int,
//        y: Int,
//        bottom: Int,
//        paint: Paint
//    ) {
//        if (text == null) return
//
//        // 绘制文本
//        canvas.drawText(text, start, end, x, y.toFloat(), paint)
//
//        // 设置绘制删除线的画笔样式
//        paint.strokeWidth = 2f // 线条宽度，可以调整
//        paint.color = 0xFFFF0000.toInt() // 删除线颜色（红色），可以调整
//
//        // 绘制删除线
//        canvas.drawLine(
//            x, y.toFloat(), x + strikeThroughWidth, y.toFloat(), paint
//        )
//    }
//}
//fun animateStrikeThrough(textView: TextView) {
//    val text = textView.text
//    val spannableString = SpannableString(text)
//    val strikeThroughSpan = StrikeThroughSpan()
//    spannableString.setSpan(strikeThroughSpan, 0, text.length, 0)
//    textView.text = spannableString
//
//    val widthAnimator = ObjectAnimator.ofFloat(
//        strikeThroughSpan, "strikeThroughWidth", 0f, textView.paint.measureText(text.toString())
//    )
//
//    widthAnimator.duration = 1000
//    widthAnimator.addUpdateListener {
//        val currentValue = it.animatedValue as Float
//        Log.d("StrikeThroughSpan", "Current width: $currentValue")
//        strikeThroughSpan.setStrikeThroughWidth(currentValue)
//        textView.invalidate() // 触发视图重绘
//    }
//    widthAnimator.start()
//}
