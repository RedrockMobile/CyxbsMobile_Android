package com.mredrock.cyxbs.course.component

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.getScreenHeight
import com.mredrock.cyxbs.course.R
import org.jetbrains.anko.*

/**
 * [ScheduleView] is used to display the course. The use of it is very easy. you can only implement
 * the [ScheduleView.Adapter].
 *
 * @attr [R.styleable.ScheduleView_elementGap] represent the gap between two elements.
 * @attr [R.styleable.ScheduleView_touchViewColor] represent the touchView's color
 * @attr [R.styleable.ScheduleView_touchViewDrawable] represent the touchView's drawable.
 * @attr [R.styleable.ScheduleView_noCourseDrawable] represent the drawable to be display when there.
 * isn't courses.
 * @attr [R.styleable.ScheduleView_heightAtLowDpi] represent the ScheduleView's height when it work.
 * at low dpi device.
 * @attr [R.styleable.ScheduleView_highlightColor] represent the ScheduleView's today highlight color.
 * @attr [R.styleable.ScheduleView_emptyTextSize] represent no course the displayed text' size.
 * @attr [R.styleable.ScheduleView_emptyText] represent no course the displayed text.
 *
 * @attr [R.attr.ScheduleViewStyle] represent the style name of Schedule's View, so you can use a style
 * instead of write it directly in the XML.
 *
 * Created by anriku on 2018/8/14.
 */
class ScheduleView : FrameLayout {

    companion object {
        const val TAG = "ScheduleView"
        const val CLICK_RESPONSE_DISTANCE = 10
    }

    var adapter: Adapter? = null
        set(value) {
            field = value

            // The following code is to add a OnGlobalLayoutListener. When the layout state change
            // will add the ItemViews to the ScheduleView, and the listener will be removed after
            // the itemViews are all added.
            // Do like this, because the viewPager will add next fragment going to display for more
            // good performance. The Fragment preload but the ScheduleView of this Fragment isn't show,
            // so the measuredWidth and measureHeight will be 0. In the following way, the itemViews
            // will be added after the onMeasure function is called, and the size of ScheduleView is
            // correct now.
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        adapter?.let { notNullAdapter ->
                            // Set the TouchView's OnClickListener
                            notNullAdapter.setOnTouchViewClickListener()?.let {
                                initTouchView()
                                it.invoke(mTouchView)
                            }

                            // When we add new courses, remove old values first.
                            removeAllViews()

                            for (row in 0..5) {
                                for (column in 0..6) {
                                    val itemViewInfo = notNullAdapter.getItemViewInfo(row, column)
                                    //if this row and column don't have course or affair continue.
                                    itemViewInfo ?: continue

                                    val itemView = notNullAdapter.getItemView(row, column, this@ScheduleView)

                                    //compute the LayoutParams of the ItemView
                                    val params = FrameLayout.LayoutParams(mBasicElementWidth,
                                            mBasicElementHeight * (itemViewInfo.itemHeight / 2))
                                    params.leftMargin = mElementGap * (column + 1) + mBasicElementWidth * column
                                    params.topMargin = mElementGap * (row + 1) + mBasicElementHeight * row
                                    // If the itemView is in the left or bottom edge, add additional
                                    // element gap.
                                    if (column == 6) {
                                        params.rightMargin = mElementGap
                                    }
                                    if (row == 5) {
                                        params.bottomMargin = mElementGap
                                    }
                                    itemView.layoutParams = params
                                    //add the itemView to the ScheduleView
                                    addView(itemView)

                                    mIsEmpty = false
                                }
                            }
                        }

                        if (mIsEmpty && mNoCourseDrawableResId != 0) {
                            // Set mEmptyImageView
                            val emptyImageView = mEmptyImageView
                            val emptyTextView = mEmptyTextView

                            val imageParams = FrameLayout.LayoutParams(mScheduleViewWidth, mScheduleViewWidth)
                            emptyImageView.apply {
                                setImageDrawable(ContextCompat.getDrawable(context, mNoCourseDrawableResId))
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                layoutParams = imageParams
                            }
                            addView(emptyImageView)

                            // set mEmptyTextView
                            mEmptyText?.let {
                                val textParams = FrameLayout.LayoutParams(mScheduleViewWidth,
                                        FrameLayout.LayoutParams.WRAP_CONTENT)
                                textParams.topMargin = mScheduleViewWidth * 4 / 5

                                emptyTextView.apply {
                                    text = it
                                    textSize = px2sp(mEmptyTextSize)
                                    gravity = Gravity.CENTER
                                    layoutParams = textParams
                                }
                                addView(emptyTextView)
                            }
                        }

                        // Remove the OnGlobalLayoutListener after we add the ItemViews.
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            // This is used to guaranteed the layout change, and trigger to add views. If requestLayout
            // isn't called in there, the ScheduleView won't add views until you let the layout change
            // at the first time when you install the app, because the first time is getting data from
            // internet which is time-consuming. when you get the data from the internet, your layout
            // won't be changed until you let it change.
            requestLayout()
        }

    private val mStartPoint: PointF by lazy(LazyThreadSafetyMode.NONE) { PointF() }
    private val mEndPoint: PointF by lazy(LazyThreadSafetyMode.NONE) { PointF() }
    // mTouchView is used to add affair
    private val mTouchView: ImageView by lazy(LazyThreadSafetyMode.NONE) { ImageView(context) }
    // This paint is used to draw the place which will be highlighted.
    private lateinit var mPaint: Paint
    // This is used to represent whether the courses and affairs are null
    private var mIsEmpty: Boolean = true
    // This is the ImageView to show when the courses and affairs are null
    private val mEmptyImageView: ImageView by lazy(LazyThreadSafetyMode.NONE) { ImageView(context) }
    // This is the TextView to show when the courses and affairs are null
    private val mEmptyTextView: TextView by lazy(LazyThreadSafetyMode.NONE) { TextView(context) }

    private var mScheduleViewWidth: Int = 0
    private var mScheduleViewHeight: Int = 0
    private var mBasicElementWidth: Int = 0
    private var mBasicElementHeight: Int = 0
    private var mTouchViewWidth: Int = 0
    private var mTouchViewHeight: Int = 0

    // The following fields are the attrs
    private var mElementGap: Int = 0
    private var mTouchViewColor: Int = 0
    private var mHeightAtLowDpi: Int = 0
    private var mTouchViewDrawableResId: Int = 0
    private var mHighlightColor: Int = 0
    private var mNoCourseDrawableResId: Int = 0
    private var mEmptyText: String? = null
    private var mEmptyTextSize: Int = sp(12)


    constructor(context: Context) : super(context) {
        mElementGap = dip(2f)
        mTouchViewColor = Color.parseColor("#bdc3c7")
        mHeightAtLowDpi = dip(600f)
        initScheduleView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView,
                R.attr.ScheduleViewStyle, 0)
        mElementGap = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_elementGap, dip(2f))
        mTouchViewColor = typeArray.getColor(R.styleable.ScheduleView_touchViewColor, Color.parseColor("#bdc3c7"))
        mHeightAtLowDpi = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_heightAtLowDpi, dip(600f))
        mTouchViewDrawableResId = typeArray.getResourceId(R.styleable.ScheduleView_touchViewDrawable, 0)
        mHighlightColor = typeArray.getColor(R.styleable.ScheduleView_highlightColor, Color.parseColor("#FFFBEB"))
        mNoCourseDrawableResId = typeArray.getResourceId(R.styleable.ScheduleView_noCourseDrawable, 0)
        mEmptyText = typeArray.getString(R.styleable.ScheduleView_emptyText)
        mEmptyTextSize = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_emptyTextSize, sp(12))
        typeArray.recycle()
        initScheduleView()
    }

    private fun initScheduleView() {
        setWillNotDraw(false)
        mPaint = Paint().apply {
            color = mHighlightColor
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    /**
     * Do some init work for the [mTouchView].
     */
    private fun initTouchView() {
        val touchView = mTouchView

        touchView.backgroundColor = mTouchViewColor
        if (mTouchViewDrawableResId != 0) {
            touchView.apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageDrawable(ContextCompat.getDrawable(context, mTouchViewDrawableResId))
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Get the ScheduleView's size
        mScheduleViewWidth = measuredWidth
        mScheduleViewHeight = if (px2dip(context.getScreenHeight()) > 700) {
            context.getScreenHeight()
        } else {
            mHeightAtLowDpi
        }
        // Compute the BasicElement's size.
        mBasicElementWidth = (mScheduleViewWidth - mElementGap * 8) / 7
        mBasicElementHeight = (mScheduleViewHeight - mElementGap * 7) / 6
        // Compute the TouchView's size.
        mTouchViewWidth = mBasicElementWidth
        mTouchViewHeight = (mBasicElementHeight - mElementGap) / 2
        setMeasuredDimension(mScheduleViewWidth, mScheduleViewHeight)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val startPoint = mStartPoint
        val endPoint = mEndPoint
        val touchView = mTouchView

        if (event.action == MotionEvent.ACTION_DOWN) {
            //record the start position
            startPoint.x = event.x
            startPoint.y = event.y
        }
        if (event.action == MotionEvent.ACTION_UP) {
            // 如果setOnTouchViewClickListener返回为null就不启用mTouchView
            adapter?.setOnTouchViewClickListener() ?: return true

            // 如果没有课程就不启用mTouchView
            if (mIsEmpty) {
                return true
            }

            performClick()
            //record the end position
            endPoint.x = event.x
            endPoint.y = event.y

            // If the move distance more than CLICK_RESPONSE_DISTANCE. This MotionEvent won't service
            // as a click event.
            if (Math.sqrt(Math.pow((endPoint.x - startPoint.x).toDouble(), 2.0) +
                            Math.pow((endPoint.y - startPoint.y).toDouble(), 2.0)) > CLICK_RESPONSE_DISTANCE) {
                return true
            }
            // Compute the click event at which range.
            var clickHashX: Int = (endPoint.x / mTouchViewWidth).toInt()
            var clickHashY: Int = (endPoint.y / mTouchViewHeight).toInt()
            if (clickHashX >= 7) {
                clickHashX = 6
            }
            if (clickHashY >= 12) {
                clickHashY = 11
            }

            // The mTouchView's tag is used to keep the position and indicate weather it is showing.
            val tag = touchView.tag
            if (tag != null) {
                removeView(touchView)
            }
            touchView.tag = (clickHashY / 2) * 7 + clickHashX
            // If the click position has course, it will be consumed.
            if (adapter!!.getItemViewInfo(clickHashY / 2, clickHashX) != null) {
                return true
            }
            // Compute the mTouchView's LayoutParams.
            val params = FrameLayout.LayoutParams(mTouchViewWidth,
                    mTouchViewHeight)
            params.leftMargin = mElementGap * (clickHashX + 1) + mTouchViewWidth * clickHashX
            params.topMargin = mElementGap * (clickHashY + 1) + mTouchViewHeight * clickHashY
            // If the mTouchView's position is at the right or bottom edge. It will add the additional
            // gap like the course.
            if (clickHashX == 6) {
                params.rightMargin = mElementGap
            }
            if (clickHashY == 11) {
                params.bottomMargin = mElementGap
            }
            touchView.layoutParams = params
            // Add the mTouchView to the ScheduleView.
            addView(touchView)
        }

        return true
    }


    override fun onDraw(canvas: Canvas) {
        val highlightPosition = adapter?.getHighLightPosition()
        highlightPosition ?: return

        val left = mBasicElementWidth * highlightPosition + mElementGap * (highlightPosition + 1)
        canvas.drawRect(left.toFloat(), 0f,
                (left + mBasicElementWidth).toFloat(), mScheduleViewHeight.toFloat(), mPaint)
    }

    /**
     * This abstract class is the adapter of [ScheduleView]. If you use the ScheduleView, you should
     * give the ScheduleView a implemented Adapter.
     */
    abstract class Adapter {

        /**
         * This method is used to get the ItemView which will be display in the [ScheduleView].
         *
         * @param row The ItemView's row.
         * @param column The ItemView's column.
         * @param container There return the [ScheduleView] as the ItemView's container.
         */
        abstract fun getItemView(row: Int, column: Int, container: ViewGroup): View

        /**
         * This method is used to get the ItemView's Info.
         * @param row The ItemView's row.
         * @param column The ItemView's column.
         * @return [ScheduleItem] is a class which you should map your data to it. And [ScheduleView]
         * use it to determine the ItemView's size.
         */
        abstract fun getItemViewInfo(row: Int, column: Int): ScheduleItem?

        /**
         * This method is used to implement the [ScheduleView.mTouchView]'s Click Listener.
         *
         * @return It is a Function Object.
         * 1.It is used to set the [mTouchView]'s onClickListener if the return is not null.
         * 2.If it return null, the mTouchView won't be displayed.
         */
        abstract fun setOnTouchViewClickListener(): ((ImageView) -> Unit)?

        /**
         * This method is used to get the position which will be highlighted.
         *
         * @return not null: the highlight position.
         * null: don't highlight.
         */
        abstract fun getHighLightPosition(): Int?
    }

    /**
     * This class represents the ItemSize going to display
     *
     * @param itemWidth represent the ScheduleItem's width.
     * @param itemHeight represent the ScheduleItem's height
     */
    data class ScheduleItem(val itemWidth: Int = 1, val itemHeight: Int = 2)

}