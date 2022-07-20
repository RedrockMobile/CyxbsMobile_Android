package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker
import com.mredrock.cyxbs.course.R
import java.lang.reflect.Field


/**
 * [FlexibleNumberPicker] is a NumberPicker which you can custom the color of the text displayed
 * ,you can select show a middle selector or a selection Divider and you can custom their color.
 *
 * @attr [R.styleable.FlexibleNumberPicker_selectorBkColor] the middle selector color
 * @attr [R.styleable.FlexibleNumberPicker_selectorWheelPaintColor] the color painting the text
 * @attr [R.styleable.FlexibleNumberPicker_isSelectionDividerShow] true: show the selection divider.
 * false: show the middle selector.
 *
 * Created by anriku on 2018/9/11.
 */
class FlexibleNumberPicker : NumberPicker {

    private val mClazz = this::class.java.superclass
    //the middle selector color
    private var mSelectorBkColor: Int = Color.parseColor("#EDF6FD")
    //the color of the paint painting the EditText
    private var mSelectorWheelPaintColor: Int = Color.parseColor("#6D83EE")
    //the paint painting the middle selector
    private var mDrawSelectorBkPaint: Paint? = null
    //show the middle selector or selectionDivider
    private var mIsShowSelectionDivider: Boolean = false
    //hold the size of middle selector
    private lateinit var mRect: Rect

    constructor(context: Context) : super(context) {
        mSelectorBkColor = Color.parseColor("#EDF6FD")
        mSelectorWheelPaintColor = Color.parseColor("#6D83EE")
        mIsShowSelectionDivider = false

        initFlexibleNumberPicker()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.FlexibleNumberPicker, R.attr.FlexibleNumberPickerStyle, 0)
        mSelectorBkColor = typedArray.getColor(R.styleable.FlexibleNumberPicker_selectorBkColor
                , Color.parseColor("#EDF6FD"))
        mSelectorWheelPaintColor = typedArray.getColor(R.styleable.FlexibleNumberPicker_selectorWheelPaintColor,
                Color.parseColor("#6D83EE"))
        mIsShowSelectionDivider = typedArray.getBoolean(R.styleable.FlexibleNumberPicker_isSelectionDividerShow,
                false)
        typedArray.recycle()

        initFlexibleNumberPicker()
    }


    private fun initFlexibleNumberPicker() {
        initEditText()
        isSelectionDividerShow(mIsShowSelectionDivider)
        //this paint setting is to set the paint painting the EditText
        setSelectorWheelPaintColor()
        //TODO 需要去除反射
        // Avoid Warning about reflection.
//        avoidWarningUsingReflection()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        prepareDrawSelectorBk()
    }

    override fun onDraw(canvas: Canvas?) {
        //draw the SelectorBackground. It should be invoked before super, because the EditText is
        //drawn in the super function and this order can avoid EditText is covered by the background
        //color.
        mDrawSelectorBkPaint?.let {
            canvas?.drawRect(mRect, it)
        }
        super.onDraw(canvas)
    }

    /**
     * This function is used to init the EditText's color and make the EditText is unable to edit.
     * If you don't init at there the beginning of the EditText color will be black.
     * Want to get more understanding? go to the source code!!!
     */
    private fun initEditText() {

        repeat(childCount) {
            (getChildAt(it) as? EditText)?.apply {
                setTextColor(mSelectorWheelPaintColor)
                textSize = 16f
                isEnabled = false
            }
        }
    }

    /**
     * This function is used to get the size of the middle selector's rect drawn by onDraw later.
     * it should be invoked at onLayout. because the field mSelectorElementHeight in [NumberPicker]
     * is determined in the onLayout. And to get more perfect performance you shouldn't call this
     * in onDraw.
     */
    private fun prepareDrawSelectorBk() {
        var mSelectorElementHeight = measuredHeight / 3
        mClazz?.let {
            mSelectorElementHeight = it.getDeclaredField("mSelectorElementHeight").apply {
                isAccessible = true
            }.get(this) as Int
        }
        val top = (measuredHeight - mSelectorElementHeight) / 2
        val bottom = (measuredHeight + mSelectorElementHeight) / 2
        mRect = Rect(0, top, measuredWidth, bottom)
    }

    /**
     * This function is used to set the color of the paint painting the EditText
     */
    private fun setSelectorWheelPaintColor() {
        mClazz?.let {
            val paint = it.getDeclaredField("mSelectorWheelPaint").apply {
                isAccessible = true
            }.get(this) as Paint
            paint.color = mSelectorWheelPaintColor
        }
    }

    /**
     * This function is used to decide whether the selectionDivider will be shown.
     * @param isShow true: show the selectionDivider but the selectorBk won't be drawn.
     *               false: the selectionBk will be drawn but the selectionDivider won't be drawn.
     */
    private fun isSelectionDividerShow(isShow: Boolean) {
        var dividerField: Field? = null
        mClazz?.let {
            dividerField = it.getDeclaredField("mSelectionDivider")
            dividerField?.isAccessible = true
        }

        if (isShow) {
            dividerField?.set(this, ColorDrawable(mSelectorBkColor))
        } else {
            mDrawSelectorBkPaint = Paint()
            mDrawSelectorBkPaint?.color = mSelectorBkColor
            dividerField?.set(this, null)
        }
    }


    /**
     * This method is used to avoid warning, if the app uses the reflection. Because in Android Pie
     * if you use the reflection, you will get a warning. But this isn't a good method for a long time.
     * So we should find a good method.
     */
    /*@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun avoidWarningUsingReflection() {
        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/
}