package com.mredrock.cyxbs.api.crash.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.TextView
import kotlin.math.max
import kotlin.math.min

/**
 * 一个用于带有左侧行号的文本显示 View，并且支持双指放大缩小
 *
 * @author 985892345
 * @date 2022/9/23 19:19
 */
class ScaleScrollTextView(
  context: Context,
  attrs: AttributeSet?
) : ViewGroup(context, attrs) {
  
  /**
   * 设置文本
   *
   * 内部会自动根据 \n 分行
   */
  fun setText(text: CharSequence) {
    mTvContent.text = text
    val builder = StringBuilder()
    builder.append(1).appendLine()
    repeat(text.count { it == '\n' }) {
      builder.append((it + 2).toString())
        .appendLine()
    }
    mTvLineNum.text = builder.toString()
  }
  
  /**
   * 设置字体大小，默认大小为 12F，单位 SP
   */
  fun setTextSize(sp: Float) {
    mTvLineNum.textSize = sp
    mTvContent.textSize = sp
  }
  
  /**
   * 设置侧边显示行数的字体颜色
   */
  fun setSideTextColor(color: Int) {
    mTvLineNum.setTextColor(color)
  }
  
  /**
   * 设置侧边显示行数的背景颜色
   */
  fun setSideBackgroundColor(color: Int) {
    mTvLineNum.setBackgroundColor(color)
  }
  
  private val mScaleGestureDetector = ScaleGestureDetector(
    context,
    object : ScaleGestureDetector.OnScaleGestureListener {
      
      private var mLastFocusX = 0F
      private var mLastFocusY = 0F
      
      override fun onScale(detector: ScaleGestureDetector): Boolean {
        mTvLineNum.setTextSize(
          TypedValue.COMPLEX_UNIT_PX,
          mTvLineNum.textSize * detector.scaleFactor
        )
        mTvContent.setTextSize(
          TypedValue.COMPLEX_UNIT_PX,
          mTvContent.textSize * detector.scaleFactor
        )
        move(detector.focusX - mLastFocusX, detector.focusY - mLastFocusY)
        mLastFocusX = detector.focusX
        mLastFocusY = detector.focusY
        return true
      }
      
      override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        mLastFocusX = detector.focusX
        mLastFocusY = detector.focusY
        return true
      }
      
      override fun onScaleEnd(detector: ScaleGestureDetector) {
      }
    }
  )
  
  private val mTvLineNum = TextView(context).apply {
    textSize = 12F
    setTextColor(if (isDaytimeMode()) 0xFF595959.toInt() else 0xFFDFDFDF.toInt())
    setBackgroundColor(if (isDaytimeMode()) 0xFFE6E6E6.toInt() else 0xFF434343.toInt())
    setPadding(12.dp2px, paddingTop, 10.dp2px, paddingBottom)
    gravity = Gravity.END
  }
  
  private val mTvContent = TextView(context).apply {
    textSize = 12F
  }
  
  init {
    addView(
      mTvContent,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    )
    addView(
      mTvLineNum,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    )
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val wSize = MeasureSpec.getSize(widthMeasureSpec)
    val wMode = MeasureSpec.getMode(widthMeasureSpec)
    val hSize = MeasureSpec.getSize(heightMeasureSpec)
    val hMode = MeasureSpec.getMode(heightMeasureSpec)
    mTvLineNum.measure(
      MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED)
    )
    mTvContent.measure(
      MeasureSpec.makeMeasureSpec(wSize - mTvLineNum.measuredWidth, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED)
    )
    val wSpec = when (wMode) {
      MeasureSpec.EXACTLY -> widthMeasureSpec
      MeasureSpec.AT_MOST ->
        MeasureSpec.makeMeasureSpec(
          min(mTvLineNum.measuredWidth + mTvContent.measuredWidth, wSize),
          MeasureSpec.EXACTLY
        )
      MeasureSpec.UNSPECIFIED ->
        MeasureSpec.makeMeasureSpec(
          max(mTvLineNum.measuredWidth + mTvContent.measuredWidth, wSize),
          MeasureSpec.EXACTLY
        )
      else -> error("")
    }
    val hSpec = when (hMode) {
      MeasureSpec.EXACTLY -> heightMeasureSpec
      MeasureSpec.AT_MOST ->
        MeasureSpec.makeMeasureSpec(
          min(max(mTvLineNum.measuredHeight, mTvContent.measuredHeight), hSize),
          MeasureSpec.EXACTLY
        )
      MeasureSpec.UNSPECIFIED ->
        MeasureSpec.makeMeasureSpec(
          maxOf(mTvLineNum.measuredHeight, mTvContent.measuredHeight, hSize),
          MeasureSpec.EXACTLY
        )
      else -> error("")
    }
    super.onMeasure(wSpec, hSpec)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    mTvLineNum.layout(0, 0, mTvLineNum.measuredWidth, mTvLineNum.measuredHeight)
    val left = mTvLineNum.measuredWidth + 6.dp2px
    mTvContent.layout(left, 0, left + mTvContent.measuredWidth, mTvContent.measuredHeight)
  }
  
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    return true
  }
  
  private var mInitialX = 0F
  private var mInitialY = 0F
  private var mLastMoveX = 0F
  private var mLastMoveY = 0F
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        mInitialX = x
        mInitialY = y
        mLastMoveX = x
        mLastMoveY = y
      }
      MotionEvent.ACTION_MOVE -> {
        // 多根手指时交给 mScaleGestureDetector 处理
        if (event.pointerCount == 1) {
          move(x - mLastMoveX, y - mLastMoveY)
        }
        mLastMoveX = x
        mLastMoveY = y
      }
    }
    mScaleGestureDetector.onTouchEvent(event)
    return true
  }
  
  private fun move(dx: Float, dy: Float) {
    mTvContent.translationX += dx
    if (dx > 0) {
      // 手指向右移动
      if (mTvContent.translationX > 0) {
        mTvContent.translationX = 0F
      }
    } else {
      // 手指向左移动
      if (mTvContent.translationX + mTvContent.width < width - 200) {
        mTvContent.translationX = (width - 200 - mTvContent.width).toFloat()
      }
    }
    if (mTvLineNum.height > height) {
      mTvContent.translationY += dy
      mTvLineNum.translationY += dy
      if (mTvLineNum.y + mTvLineNum.height < height) {
        mTvContent.translationY = (height - mTvContent.height).toFloat()
        mTvLineNum.translationY = (height - mTvLineNum.height).toFloat()
      }
    }
    if (mTvContent.translationY > 0) {
      mTvContent.translationY = 0F
      mTvLineNum.translationY = 0F
    }
  }
  
  private val Int.dp2pxF: Float
    get() = context.resources.displayMetrics.density * this
  
  private val Int.dp2px: Int
    get() = dp2pxF.toInt()
  
  private fun isDaytimeMode(): Boolean {
    val uiMode = context.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
  }
}