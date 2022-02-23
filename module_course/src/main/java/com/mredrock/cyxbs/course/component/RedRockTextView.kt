package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.course.R

/**
 * This is a TextView which display the text like cy's course column or week column. There are
 * three custom attrs. It doesn't adaptive multiline text, because I think it is not necessary
 *
 * @attr [R.styleable.RedRockTextView_orientation] this attr is used to set the orientation of the RedRockTextView
 * @attr [R.styleable.RedRockTextView_offsetBetweenText] this attr is used to set the gap between the texts
 *
 * isOthers attrs are the same with TextView.you can set the displayedStrings attr and the offset_between_text
 * attr in the code.
 *
 * Created by anriku on 2018/8/14.
 */
class RedRockTextView : AppCompatTextView {

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    private val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
    }
    private val selectTextPaint: Paint by lazy {
        Paint()
    }
    private var mElementWidth: Float = 0f
    private var mElementHeight: Float = 0f

    //record a CharSequence bounds
    private lateinit var mTextBounds: ArrayList<Rect>

    // The following fields are attrs.
    var displayedStrings: Array<CharSequence> = arrayOf()
        set(value) {
            field = value
            requestLayout()
        }

    private var mOrientation: Int = HORIZONTAL
    private var offsetBetweenText = 0
        set(value) {
            field = value
            invalidate()
        }


    var position: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    var selectColor = 0xffffff

    constructor(context: Context) : super(context) {
        initRedRockTextView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RedRockTextView, R.attr.RedRockTextViewStyle, 0)

        typedArray.getTextArray(R.styleable.RedRockTextView_displayedStrings).let {
            displayedStrings = if (it == null) {
                val strings = arrayOf<CharSequence>("")
                strings
            } else {
                it
            }
        }
        mOrientation = typedArray.getInt(R.styleable.RedRockTextView_orientation, HORIZONTAL)
        offsetBetweenText = typedArray.getDimensionPixelSize(R.styleable.RedRockTextView_offsetBetweenText,
                0)
        selectColor = typedArray.getColor(R.styleable.RedRockTextView_selectedColor, 0xffffff)
        typedArray.recycle()

        initRedRockTextView()
    }


    private fun initRedRockTextView() {
        //set Paint's color
        val drawableState = drawableState
        mPaint.color = textColors.getColorForState(drawableState, 0)

        //getTextSize
        mPaint.textSize = textSize
        selectTextPaint.set(mPaint)
        selectTextPaint.color = selectColor
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        computeBounds()

        if (layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT &&
                layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {

            if (mOrientation == HORIZONTAL) {
                setMeasuredDimension(measuredWidth + offsetBetweenText * (displayedStrings.size - 1),
                        measuredHeight)
            } else {
                setMeasuredDimension(measuredWidth, measuredHeight + offsetBetweenText *
                        (displayedStrings.size - 1))
            }
            return
        }

        dealWithWrapContent()
    }

    /**
     * This function is used to compute the displayedStrings bounds.
     */
    private fun computeBounds() {
        mTextBounds = ArrayList()

        for (str in displayedStrings) {
            val rect = Rect()
            mPaint.getTextBounds(str.toString(), 0, str.length, rect)
            mTextBounds.add(rect)
        }
    }

    /**
     * This function is used to deal wrap_content problem
     */
    private fun dealWithWrapContent() {
        var viewWidth = measuredWidth
        var viewHeight = measuredHeight
        if (mOrientation == HORIZONTAL) {
            if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                var max = 0
                for (bound in mTextBounds) {
                    if (bound.width() > max) {
                        max = bound.width()
                    }
                }
                viewWidth = max * displayedStrings.size + paddingLeft + paddingRight +
                        offsetBetweenText * (displayedStrings.size - 1)
            }
            if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                viewHeight = mTextBounds[0].height()
                viewHeight += paddingTop + paddingBottom
            }
        } else {
            if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                viewWidth = 0
                for (bound in mTextBounds) {
                    val width = bound.width()
                    if (width > viewWidth) {
                        viewWidth = width
                    }
                }
                viewWidth += paddingLeft + paddingRight
            }
            if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                viewHeight = mTextBounds.size * mTextBounds[0].height()
                viewHeight += paddingTop + paddingBottom + offsetBetweenText * (displayedStrings.size - 1)
            }
        }
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mOrientation == HORIZONTAL) {
            mElementHeight = measuredHeight.toFloat() - paddingTop - paddingBottom
            mElementWidth = (measuredWidth.toFloat() - paddingLeft - paddingRight -
                    offsetBetweenText * (displayedStrings.size - 1)) / displayedStrings.size

            for (i in displayedStrings.indices) {

                val drawX = (mElementWidth - mTextBounds[i].width()) / 2 + mElementWidth * i +
                        paddingLeft + offsetBetweenText * i
                val drawY = (mElementHeight - (-mPaint.ascent() + mPaint.descent())) / 2 -
                        mPaint.ascent() + paddingTop
                canvas?.drawText(displayedStrings[i].toString(), drawX, drawY, if (position != null && position == i) selectTextPaint else mPaint)
            }
        } else {
            mElementWidth = measuredWidth.toFloat() - paddingLeft - paddingRight
            mElementHeight = (measuredHeight.toFloat() - paddingTop - paddingBottom -
                    offsetBetweenText * (displayedStrings.size - 1)) / displayedStrings.size

            for (i in displayedStrings.indices) {
                val drawX = (mElementWidth - mTextBounds[i].width()) / 2 + paddingLeft
                val drawY = (mElementHeight - (-mPaint.ascent() + mPaint.descent())) / 2 -
                        mPaint.ascent() + mElementHeight * i + paddingTop + offsetBetweenText * i

                canvas?.drawText(displayedStrings[i].toString(), drawX, drawY, if (position != null && position == i) selectTextPaint else mPaint)
            }
        }
    }

    /**
     * This function is to set the displayedStrings which will be displayed.
     * @param strings displayedStrings going to be displayed.
     */
    fun setStrings(strings: Array<CharSequence>) {
        this.displayedStrings = strings
        invalidate()
    }
}