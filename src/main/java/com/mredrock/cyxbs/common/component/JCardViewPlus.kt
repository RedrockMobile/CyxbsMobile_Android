package com.mredrock.cyxbs.common.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.R

/**
 * Created By jay68 on 2018/8/27.
 */
open class JCardViewPlus(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {
    companion object {
        @JvmStatic
        val DEFAULT_CARD_BACKGROUND_COLOR = Color.parseColor("#ffffff")
        @JvmStatic
        val DEFAULT_SHADER_COLOR = Color.parseColor("#fefefe")

        const val LEFT_SHADOW_MASK = 0x1
        const val TOP_SHADOW_MASK = 0x2
        const val RIGHT_SHADOW_MASK = 0x4
        const val BOTTOM_SHADOW_MASK = 0x8

        private const val LEFT_SHADOW_SHIFT = 0
        private const val TOP_SHADOW_SHIFT = 1
        private const val RIGHT_SHADOW_SHIFT = 2
        private const val BOTTOM_SHADOW_SHIFT = 3
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var cardBackgroundShape: Path = Path()

    @ColorInt
    var shadowColor: Int = DEFAULT_SHADER_COLOR
        set(value) {
            field = value
            backgroundBuffer = null
            paint.apply {
                setShadowLayer(shadowRadius, 0f, 0f, value)
            }
        }
    var shadowRadius: Float = dip(10)
        set(value) {
            field = value
            backgroundBuffer = null
            paint.apply {
                setShadowLayer(value, 0f, 0f, shadowColor)
            }
        }
    private var shadowFlags: Int = 0xF

    private var cardCornerRadiusUpperLeft: Int = 0
    private var cardCornerRadiusUpperRight: Int = 0
    private var cardCornerRadiusLowerLeft: Int = 0
    private var cardCornerRadiusLowerRight: Int = 0

    private var contentPaddingLeft: Int = 0
    private var contentPaddingTop: Int = 0
    private var contentPaddingBottom: Int = 0
    private var contentPaddingRight: Int = 0

    private val paint: Paint
    private var backgroundBuffer: Bitmap? = null

    init {
        if (childCount > 1) {
            throw IllegalStateException("JCardViewPlus can only support one child")
        }
        setBackgroundColor(Color.TRANSPARENT)
        paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.JCardViewPlus)
        val cardBackgroundColor = typedArray.getColor(R.styleable.JCardViewPlus_cardBackgroundColor, DEFAULT_CARD_BACKGROUND_COLOR)
        setCardBackgroundColor(cardBackgroundColor)
        initShadow(typedArray)
        initCardRadius(typedArray)
        initContentPadding(typedArray)
        typedArray.recycle()
    }

    private fun initShadow(typedArray: TypedArray) {
        shadowRadius = typedArray.getDimension(R.styleable.JCardViewPlus_shadowRadius, shadowRadius)
        shadowFlags = typedArray.getInt(R.styleable.JCardViewPlus_shadowFlags, shadowFlags)
        shadowColor = typedArray.getColor(R.styleable.JCardViewPlus_shadowColor, DEFAULT_SHADER_COLOR)
        setShadow(shadowFlags)
    }

    private fun initCardRadius(typedArray: TypedArray) {
        val cardCornerRadius = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_cardCornerRadius, -1)
        if (cardCornerRadius != -1) {
            cardCornerRadiusUpperLeft = cardCornerRadius
            cardCornerRadiusUpperRight = cardCornerRadius
            cardCornerRadiusLowerLeft = cardCornerRadius
            cardCornerRadiusLowerRight = cardCornerRadius
        } else {
            cardCornerRadiusUpperLeft = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_cardCornerRadiusUpperLeft, 0)
            cardCornerRadiusUpperRight = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_cardCornerRadiusUpperRight, 0)
            cardCornerRadiusLowerLeft = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_cardCornerRadiusLowerLeft, 0)
            cardCornerRadiusLowerRight = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_cardCornerRadiusLowerRight, 0)
        }
    }

    private fun initContentPadding(typedArray: TypedArray) {
        val contentPadding = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_contentPadding, -1)
        if (contentPadding > 0) {
            contentPaddingLeft = contentPadding
            contentPaddingTop = contentPadding
            contentPaddingBottom = contentPadding
            contentPaddingRight = contentPadding
            return
        }

        val horizontalContentPadding = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_horizontalContentPadding, -1)
        val verticalContentPadding = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_verticalContentPadding, -1)
        contentPaddingLeft = horizontalContentPadding
        contentPaddingTop = verticalContentPadding
        contentPaddingBottom = verticalContentPadding
        contentPaddingRight = horizontalContentPadding
        if (contentPaddingLeft < 0) {
            contentPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_contentPaddingLeft, 0)
            contentPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_contentPaddingRight, 0)
        }
        if (contentPaddingTop < 0) {
            contentPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_contentPaddingTop, 0)
            contentPaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.JCardViewPlus_contentPaddingBottom, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount != 0) {
            measureChildWithMargins(getChildAt(0), widthMeasureSpec, contentPaddingLeft + contentPaddingRight,
                    heightMeasureSpec, contentPaddingTop + contentPaddingBottom)
            val child = getChildAt(0)
            super.setMeasuredDimension(getMeasureSpec(widthMeasureSpec, child.measuredWidth, paddingLeft + paddingRight + contentPaddingLeft + contentPaddingRight),
                    getMeasureSpec(heightMeasureSpec, child.measuredHeight, paddingTop + paddingBottom + contentPaddingTop + contentPaddingBottom))
        } else {
            super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun getMeasureSpec(measureSpec: Int, childSize: Int, padding: Int): Int {
        val size = MeasureSpec.getSize(measureSpec)
        val mode = MeasureSpec.getMode(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> measureSpec

            MeasureSpec.AT_MOST -> {
                MeasureSpec.makeMeasureSpec(Math.min(size, childSize + padding), MeasureSpec.EXACTLY)
            }

            MeasureSpec.UNSPECIFIED -> {
                MeasureSpec.makeMeasureSpec(childSize + padding, MeasureSpec.EXACTLY)
            }

            else -> measureSpec
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount <= 0) {
            return
        }
        val parentLeft = paddingLeft + contentPaddingLeft
        val parentTop = paddingTop + contentPaddingTop
        val child = getChildAt(0)
        child.layout(parentLeft, parentTop, parentLeft + child.measuredWidth, parentTop + child.measuredHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (backgroundBuffer == null) {
//            if (childCount != 0 && getChildAt(0) is ImageView) {
//                val drawable = (getChildAt(0) as ImageView).drawable
//                if (drawable is BitmapDrawable) {
//                    backgroundBuffer = drawable.bitmap.extractAlpha()
//                    paint.apply {
//                        clearShadowLayer()
//                        maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.SOLID)
//                    }
//                }
//            } else {
//                paint.apply {
//                    maskFilter = null
//                    setShadowLayer(shadowRadius, 0f, 0f, shadowColor)
//                }
//            }
            drawBuffer()
        }
        canvas.drawBitmap(backgroundBuffer, 0f, 0f, null)
        canvas.clipPath(cardBackgroundShape)
        super.dispatchDraw(canvas)
    }

    private fun drawBuffer() {
        backgroundBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(backgroundBuffer)
        calcBackgroundShape()
        canvas.drawPath(cardBackgroundShape, paint)
    }

    private fun calcBackgroundShape() {
        cardBackgroundShape.reset()
        //边界区域
        val cardRect = RectF(0f, 0f, width.toFloat(), height.toFloat()).apply {
            left += (shadowFlags and LEFT_SHADOW_MASK ushr LEFT_SHADOW_SHIFT) * shadowRadius
            top += (shadowFlags and TOP_SHADOW_MASK ushr TOP_SHADOW_SHIFT) * shadowRadius
            right -= (shadowFlags and RIGHT_SHADOW_MASK ushr RIGHT_SHADOW_SHIFT) * shadowRadius
            bottom -= (shadowFlags and BOTTOM_SHADOW_MASK ushr BOTTOM_SHADOW_SHIFT) * shadowRadius
        }
        //通过op运算剪裁出圆角
        cardBackgroundShape.addRect(cardRect, Path.Direction.CW)
        calcCorner(cardCornerRadiusUpperLeft, cardRect.left, cardRect.top, 1, 1)
        calcCorner(cardCornerRadiusUpperRight, cardRect.right, cardRect.top, -1, 1)
        calcCorner(cardCornerRadiusLowerRight, cardRect.right, cardRect.bottom, -1, -1)
        calcCorner(cardCornerRadiusLowerLeft, cardRect.left, cardRect.bottom, 1, -1)
    }

    private fun calcCorner(cornerRadius: Int, x: Float, y: Float, xSign: Int, ySign: Int) {
        val circle = Path()
        circle.addCircle(x, y, Math.abs(cornerRadius.toFloat()), Path.Direction.CW)
        if (cornerRadius != 0) {
            cardBackgroundShape -= circle
            if (cornerRadius > 0) {
                circle.offset(cornerRadius.toFloat() * xSign, cornerRadius.toFloat() * ySign)
                cardBackgroundShape += circle
            }
        }
    }

    private operator fun Path.minusAssign(path: Path) {
        op(path, Path.Op.DIFFERENCE)
    }

    private operator fun Path.plusAssign(path: Path) {
        op(path, Path.Op.UNION)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        backgroundBuffer?.recycle()
        backgroundBuffer = null
    }

    fun setCardBackgroundColor(@ColorInt color: Int) {
        backgroundBuffer = null
        paint.color = color
    }

    val cardBackgroundColor get() = paint.color

    fun setCardCornerRadius(upperLeft: Int, upperRight: Int, lowerRight: Int, lowerLeft: Int) {
        cardCornerRadiusUpperLeft = upperLeft
        cardCornerRadiusUpperRight = upperRight
        cardCornerRadiusLowerRight = lowerRight
        cardCornerRadiusLowerLeft = lowerLeft
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val changed = contentPaddingLeft != left || contentPaddingTop != top
                || contentPaddingRight != right || contentPaddingBottom != bottom
        if (changed) {
            contentPaddingLeft = left
            contentPaddingTop = top
            contentPaddingRight = right
            contentPaddingBottom = bottom
            requestLayout()
        }
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) = when (layoutDirection) {
        View.LAYOUT_DIRECTION_LTR -> setPadding(start, top, end, bottom)
        else -> setPadding(end, top, start, bottom)
    }

    /**
     * 设置阴影的显示，true为显示，否则不显示
     */
    fun setShadow(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean) {
        val flags = (bottom.toInt() shl BOTTOM_SHADOW_SHIFT) or
                (right.toInt() shl RIGHT_SHADOW_SHIFT) or
                (top.toInt() shl TOP_SHADOW_SHIFT) or
                (left.toInt() shl LEFT_SHADOW_SHIFT)
        if (shadowFlags != flags) {
            shadowFlags = flags
            backgroundBuffer = null
        }
        val shadowRadius = shadowRadius.toInt()
        super.setPadding(left.toInt() * shadowRadius, top.toInt() * shadowRadius,
                right.toInt() * shadowRadius, bottom.toInt() * shadowRadius)
    }

    private fun setShadow(shadowFlags: Int) {
        setShadow((shadowFlags and LEFT_SHADOW_MASK).toBoolean(),
                (shadowFlags and TOP_SHADOW_MASK).toBoolean(),
                (shadowFlags and RIGHT_SHADOW_MASK).toBoolean(),
                (shadowFlags and BOTTOM_SHADOW_MASK).toBoolean())
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun Int.toBoolean() = this != 0

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT)

    override fun generateLayoutParams(p: LayoutParams?) = MarginLayoutParams(p)

    override fun checkLayoutParams(p: LayoutParams?) = p is MarginLayoutParams

    private fun dip(dpValue: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), resources.displayMetrics)
}