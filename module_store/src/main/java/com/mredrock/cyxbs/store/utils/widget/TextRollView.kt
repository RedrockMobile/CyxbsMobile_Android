package com.mredrock.cyxbs.store.utils.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import com.mredrock.cyxbs.store.R
import java.util.*

/**
 * 邮票中心界面的邮票数字滚动动画
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/15
 */
class TextRollView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    /**
     * 有动画的设置数字
     * @param text 输入的数字只能为非负整数
     * @param isAttachToWindowStart 是否在该 View 添加到窗口时自动运行动画
     */
    fun setText(text: String, isAttachToWindowStart: Boolean) {
        if (text == mLastText) return
        if (text.matches(Regex("^\\d+$"))) { // 输入的文字匹配非负整数才有效
            mLastText = text
            mChangeCallBack = {
                mNewTextList.clear()
                mNewTextList.addAll(mLastText.chunked(1))
                mDiffZero = mNewTextList.size - mOldTextList.size
                repeat(mNewTextList.size - mOldTextList.size) { mOldTextList.addFirst("0") }
                repeat(mOldTextList.size - mNewTextList.size) { mNewTextList.addFirst("0") }
                if (mLastText.length > mMaxNumber) { // 数字大于当前显示长度, 重新测量
                    mMaxNumber += mLastText.length - mMaxNumber + 1
                    requestLayout()
                }
                slowlyAnimate(
                    0F, 1F,
                    onEnd = {
                        mNewTextList.clear()
                        mNewTextList.addAll(mLastText.chunked(1))
                        mOldTextList.clear()
                        mOldTextList.addAll(mNewTextList)
                    },
                    onCancel = {
                        mNewTextList.clear()
                        mNewTextList.addAll(mLastText.chunked(1))
                        mOldTextList.clear()
                        mOldTextList.addAll(mNewTextList)
                    },
                    onChange = {
                        mRadio = it
                        invalidate()
                    }
                )
            }
            if (!isAttachToWindowStart || isAttachedToWindow) {
                mChangeCallBack?.invoke()
                mChangeCallBack = null
            }
        }
    }

    /**
     * 没有动画的设置数字
     * @param text 输入的数字只能为非负整数
     */
    fun setTextNoAnimate(text: String) {
        if (text == mLastText) return
        if (text.matches(Regex("^\\d+$"))) { // 输入的文字匹配非负整数才有效
            mLastText = text
            mNewTextList.clear()
            mOldTextList.clear()
            mNewTextList.addAll(text.chunked(1))
            mOldTextList.addAll(mNewTextList)
            if (text.length > mMaxNumber) {
                mMaxNumber += text.length - mMaxNumber + 1
                requestLayout()
            }
            invalidate()
        }
    }

    /**
     * 透明度从 0 -> 1 的动画, 可用于初次加载
     * @param text 输入的数字只能为非负整数
     */
    fun setTextOnlyAlpha(text: String) {
        if (text == mLastText) return
        if (text.matches(Regex("^\\d+$"))) { // 输入的文字匹配非负整数才有效
            mLastText = text
            mNewTextList.clear()
            mOldTextList.clear()
            mNewTextList.addAll(text.chunked(1))
            mOldTextList.addAll(mNewTextList)
            if (text.length > mMaxNumber) {
                mMaxNumber += text.length - mMaxNumber + 1
                requestLayout()
            }
            invalidate()
            slowlyAnimate(0F, 1F,
                onCancel = { alpha = 1F },
                onChange = {
                    alpha = it
                }
            )
        }
    }

    fun hasText(): Boolean {
        return mLastText.isNotEmpty()
    }




    /*
    * ==================================================================================================================
    * 全局变量及 init()
    * */

    private var mLastText = "" // 之前的文字, 在使用 setText*() 方法后都会变成现在的文字
    private var mRadio = 1F // 0 -> 1 的动画进度
    private val mTextPaint = Paint()
    private val mOneTextWidth: Float // 一个文字的宽度
    private val mTextHeight: Float // 文字的整体高度
    private val mTextDrawHeight: Float // 文字基线的距离(加上你想绘制的中心线 y 值, 文字就会绘制在中心)
    private var mMaxNumber = 8 // 默认的文字个数, 超过就要重新测量

    init {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.TextRollView)
        val size = ty.getDimension(R.styleable.TextRollView_view_textSize, 60F)
        val font = ty.getString(R.styleable.TextRollView_view_textFontFromAssets)
        val color = ty.getColor(R.styleable.TextRollView_view_textColor, 0xFF000000.toInt())
        ty.recycle()
        mTextPaint.apply {
            isAntiAlias = true
            textSize = size
            this.color = color
            typeface = Typeface.createFromAsset(context.assets, font)
        }
        mOneTextWidth = mTextPaint.measureText("0")
        val fm = mTextPaint.fontMetrics
        mTextHeight = fm.descent - fm.ascent
        mTextDrawHeight = (fm.bottom - fm.top) / 2F - fm.bottom
    }




    /*
    * ==================================================================================================================
    * 实现数字滚动的核心代码
    * */

    private var mDiffZero = 0 // 之前与现在的数字位数差, 大于 0, 表示现在的位数大于之前的位数
    private val mOldTextList = LinkedList<String>()
    private val mNewTextList = LinkedList<String>()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        repeat(mOldTextList.size) { // 控制位数的循环
            val start = mOldTextList[it].toInt()
            val end = mNewTextList[it].toInt()
            if (start < end) { // 当前位上, 之前的数字小于现在的数字
                repeat(end - start + 1) { i -> // 控制当前位的滚动绘制
                    canvas.drawText(
                        (start + i).toString(),
                        getTextX(it),
                        getTextY(start, end, start + i),
                        mTextPaint
                    )
                }
            }else { // 当前位上, 之前的数字大于现在的数字
                repeat(start - end + 1) { i -> // 控制当前位的滚动绘制
                    canvas.drawText(
                        (start - i).toString(),
                        getTextX(it),
                        getTextY(start, end, start - i),
                        mTextPaint
                    )
                }
            }
        }
        if (mSlowlyAnimate == null) { // 动画为空表明动画结束, 就完整的重新绘制一边
            /*
            * 为什么仍以位数来绘制, 直接绘制 mLastText 不香吗?
            * 原因在于如果直接绘制整个文字, 会出现间隔不对应的问题
            * */
            repeat(mNewTextList.size) {
                canvas.drawText(
                    mNewTextList[it],
                    getTextX(it),
                    getTextY(0, 0, 0),
                    mTextPaint
                )
            }
        }
    }

    // 得到当前为的 X 坐标, 因为在 999 -> 99 的过程中少了一位, 所以需要该函数来判断
    private fun getTextX(position: Int): Float {
        return if (mDiffZero > 0) { // 位数减少, 往左移
            (position - mDiffZero * (1 - mRadio)) * mOneTextWidth
        }else if (mDiffZero < 0) { // 位数增加, 往右移
            (position + mDiffZero * mRadio) * mOneTextWidth
        }else {
            position * mOneTextWidth
        }
    }
    // 当当前位数字滚动时改变 Y 坐标
    private fun getTextY(start: Int, end: Int, now: Int): Float {
        val moveH = (end - start) * mTextHeight * mRadio
        val diffH = (now - start) * mTextHeight
        return height / 2F + moveH - diffH + mTextDrawHeight
    }




    /*
    * ==================================================================================================================
    * 一般不会修改的代码
    * */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wMS = widthMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            wMS = MeasureSpec.makeMeasureSpec(
                (mMaxNumber * mOneTextWidth).toInt(),
                MeasureSpec.EXACTLY
            )
        }
        var hMS = heightMeasureSpec
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            hMS = MeasureSpec.makeMeasureSpec(mTextHeight.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(wMS, hMS)
    }

    private var mSlowlyAnimate: ValueAnimator? = null
    private fun slowlyAnimate(
        old: Float,
        new: Float,
        time: Long = 400L,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onChange: (now: Float) -> Unit
    ) {
        mSlowlyAnimate?.run { if (isRunning) cancel() }
        mSlowlyAnimate = ValueAnimator.ofFloat(old, new)
        mSlowlyAnimate?.run {
            addUpdateListener {
                val now = animatedValue as Float
                onChange.invoke(now)
            }
            addListener(
                onEnd = {
                    onEnd?.invoke()
                    mSlowlyAnimate = null
                },
                onCancel = {
                    onCancel?.invoke()
                    mSlowlyAnimate = null
                }
            )
            interpolator = AccelerateDecelerateInterpolator()
            duration = time
            start()
        }
    }

    // 用于该 View 添加到窗口上时的回调
    private var mChangeCallBack: (() -> Unit)? = null
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mChangeCallBack?.invoke()
        mChangeCallBack = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mSlowlyAnimate?.run { if (isRunning) end() }
    }
}