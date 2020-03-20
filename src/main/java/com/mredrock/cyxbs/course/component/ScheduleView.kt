package com.mredrock.cyxbs.course.component

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getScreenHeight
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.utils.createCornerBackground
import org.jetbrains.anko.dip
import org.jetbrains.anko.px2dip
import org.jetbrains.anko.px2sp
import org.jetbrains.anko.sp
import kotlin.math.pow
import kotlin.math.sqrt

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
 * @attr [R.styleable.ScheduleView_isDisplayCourse] true: display course; false: display no course invite.
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

    var adapterChangeListener: ((Adapter) -> Unit)? = null

    /**
     * 接收一个继承了本类内部抽象Adapter的类对象，
     * 以此把获取数据和itemView的逻辑和itemView数据获取（仿造ViewPager或RecyclerView）
     */
    var adapter: Adapter? = null
        set(value) {
            field = value
            field?.let { adapterChangeListener?.invoke(it) }
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
                            addCourseView(notNullAdapter)
                        }
                        addNoCourseView()
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

    private var linearLayout: LinearLayout? = null


    private var mScheduleViewWidth: Int = 0
    private var mScheduleViewHeight: Int = 0
    private var mBasicElementWidth: Int = 0
    private var mBasicElementHeight: Int = 0

    //是否是显示课表
    private var mIsDisplayCourse: Boolean = true

    // The following fields are the attrs
    private var mElementGap: Int = 0
    private var mTouchViewColor: Int = 0
    private var mHeightAtLowDpi: Int = 0
    private var mTouchViewDrawableResId: Int = 0
    private var mHighlightColor: Int = 0
    private var mNoCourseDrawableResId: Int = 0
    private var mEmptyText: String? = null
    private var mEmptyTextSize: Int = sp(12)
    private var noCourseImageHeight: Int = 0
    private var noCourseImageWidth: Int = 0


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
        noCourseImageHeight = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_noGourseImageHeight, dip(0f))
        noCourseImageWidth = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_noGourseImageWidth, dip(0f))
        mTouchViewColor = typeArray.getColor(R.styleable.ScheduleView_touchViewColor, Color.parseColor("#bdc3c7"))
        mHeightAtLowDpi = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_heightAtLowDpi, dip(600f))
        mTouchViewDrawableResId = typeArray.getResourceId(R.styleable.ScheduleView_touchViewDrawable, 0)
        mHighlightColor = typeArray.getColor(R.styleable.ScheduleView_highlightColor, Color.parseColor("#FFFBEB"))
        mNoCourseDrawableResId = typeArray.getResourceId(R.styleable.ScheduleView_noCourseDrawable, 0)
        mEmptyText = typeArray.getString(R.styleable.ScheduleView_emptyText)
        mEmptyTextSize = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_emptyTextSize, sp(12))
        mIsDisplayCourse = typeArray.getBoolean(R.styleable.ScheduleView_isDisplayCourse, true)
        typeArray.recycle()
        initScheduleView()
    }

    private fun initScheduleView() {
        setWillNotDraw(false)
        layoutTransition = layoutTransition()
        mPaint = Paint().apply {
            color = mHighlightColor
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    private fun layoutTransition(): LayoutTransition {
        val mLayoutTransition = LayoutTransition()
        mLayoutTransition.setStagger(LayoutTransition.APPEARING, 500)
        val appearingScaleX: PropertyValuesHolder = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f)
        val appearingScaleY: PropertyValuesHolder = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1.0f)
        val appearingAlpha: PropertyValuesHolder = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        val mAnimatorAppearing: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, appearingAlpha, appearingScaleX, appearingScaleY)
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mAnimatorAppearing)
        return mLayoutTransition
    }

    /**
     * Do some init work for the [mTouchView].
     */
    private fun initTouchView() {
        val touchView = mTouchView
        touchView.background = createCornerBackground(mTouchViewColor, context.resources.getDimension(R.dimen.course_course_item_radius))
        if (mTouchViewDrawableResId != 0) {
            touchView.apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageDrawable(ContextCompat.getDrawable(context, mTouchViewDrawableResId))
            }
        }
    }

    private fun addNoCourseView() {
        if (linearLayout == null && mIsEmpty && mNoCourseDrawableResId != 0) {
            linearLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }

            // Set mEmptyImageView
            val emptyImageView = mEmptyImageView
            val emptyTextView = mEmptyTextView

            val imageParams = LayoutParams(
                    if (noCourseImageWidth == 0) mScheduleViewWidth else noCourseImageWidth,
                    if (noCourseImageHeight == 0) mScheduleViewHeight else noCourseImageHeight)
            emptyImageView.apply {
                setImageDrawable(ContextCompat.getDrawable(context, mNoCourseDrawableResId))
                scaleType = ImageView.ScaleType.FIT_XY
                layoutParams = imageParams
            }
            linearLayout?.addView(emptyImageView)

            // set mEmptyTextView
            mEmptyText?.let {
                val textParams = LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT)
                textParams.topMargin = mScheduleViewWidth * 1 / 10
                textParams.bottomMargin = mScheduleViewWidth * 4 / 5
                emptyTextView.apply {
                    text = it
                    textSize = px2sp(mEmptyTextSize)
                    gravity = Gravity.CENTER
                    layoutParams = textParams
                }
                linearLayout?.addView(emptyTextView)
                addView(linearLayout)
            }
        } else if (linearLayout != null) {
            addView(linearLayout)
        }
    }

    private fun addCourseView(notNullAdapter: Adapter) {
        // 如果是课表，每节课的开始都是偶数，0、2、4...这样开始的。因此这里就使用6 * 7的矩阵。
        // 但是如果是没课约，有可能是奇数开始的，因此这里需要使用12 * 7的矩阵运算。
        if (mIsDisplayCourse) {
            for (row in 0..5) {
                for (column in 0..6) {
                    val itemViewInfo = notNullAdapter.getItemViewInfo(row, column)
                    //if this row and column don't have course or affair continue.
                    itemViewInfo ?: continue
                    val itemView = notNullAdapter.getItemView(row, column, this@ScheduleView)
                    //这里设置tag，将真正用来测量和布局的代码放到标准的view绘制流程的回调方法里
                    //如果直接在这进行测量或者布局，在activity异常重启而导致的view异常重启之后
                    //无法异常重启的view第一次显示时，能够获取准确的测绘高度和宽度
                    itemView.setTag(R.id.item_position_row, row)
                    itemView.setTag(R.id.item_position_column, column)
                    itemView.setTag(R.id.item_info, itemViewInfo)
                    addView(itemView)
                    mIsEmpty = false
                }
            }
        } else {
            for (column in 0..6) {
                var row = 0
                while (row < 12) {
                    val itemViewInfo = notNullAdapter.getItemViewInfo(row, column)
                    if (itemViewInfo == null) {
                        row++
                        continue
                    }
                    val itemView = notNullAdapter.getItemView(row, column, this@ScheduleView)
                    itemView.setTag(R.id.item_position_row, row)
                    itemView.setTag(R.id.item_position_column, column)
                    itemView.setTag(R.id.item_info, itemViewInfo)
                    addView(itemView)
                    row += itemViewInfo.itemHeight
                    mIsEmpty = false
                }
            }
        }
    }

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        if (mIsDisplayCourse) {
            actionItemInfoStatus(child, infoFound = { itemViewInfo, row, column ->
                val layoutParams = child.layoutParams as LayoutParams
                val childWidth = mBasicElementWidth
                val childHeight = (mBasicElementHeight * itemViewInfo.itemHeight + (itemViewInfo.itemHeight - 1) * mElementGap)
                layoutParams.leftMargin = mElementGap * (column + 1) + mBasicElementWidth * column
                layoutParams.topMargin = mElementGap * (row + 1) + (mBasicElementHeight * 2 + mElementGap) * row
                if (column == 6) {
                    layoutParams.marginEnd -= mElementGap
                }
                if (row == 5) {
                    layoutParams.bottomMargin -= mElementGap
                }
                val widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
                val heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
                child.measure(widthMeasureSpec, heightMeasureSpec)
            }, infoNotFound = { super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec) })
        } else {
            actionItemInfoStatus(child, infoFound = { itemViewInfo, row, column ->
                val layoutParams = child.layoutParams as LayoutParams
                val childWidth = mBasicElementWidth
                val childHeight = (mBasicElementHeight * itemViewInfo.itemHeight + mElementGap * (itemViewInfo.itemHeight - 1))
                layoutParams.leftMargin = (mElementGap * (column + 1) + mBasicElementWidth * column)
                layoutParams.topMargin = (mElementGap * (row + 1) + mBasicElementHeight * row)
                if (column == 6) {
                    layoutParams.marginEnd -= mElementGap
                }
                if (row == 11) {
                    layoutParams.bottomMargin -= mElementGap
                }
                child.measure(
                        MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
            }, infoNotFound = { super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec) })
        }
    }


    private fun actionItemInfoStatus(child: View, infoFound: ((itemViewInfo: ScheduleItem, row: Int, column: Int) -> Unit)? = null, infoNotFound: (() -> Unit)? = null) {
        val itemViewInfo = child.getTag(R.id.item_info) as ScheduleItem?
        val row = child.getTag(R.id.item_position_row) as Int?
        val column = child.getTag(R.id.item_position_column) as Int?
        if (itemViewInfo != null && row != null && column != null) {
            infoFound?.invoke(itemViewInfo, row, column)
        } else {
            infoNotFound?.invoke()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        // Get the ScheduleView's size
        mScheduleViewWidth = measuredWidth
        //防止由于分屏或者横屏造成的显示异常
        mScheduleViewHeight = if (measuredHeight < mHeightAtLowDpi) mHeightAtLowDpi else measuredHeight

        // Compute the BasicElement's size.
        mBasicElementWidth = (mScheduleViewWidth - mElementGap * 8) / 7
        mBasicElementHeight = (mScheduleViewHeight - mElementGap * 13) / 12
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mScheduleViewWidth, mScheduleViewHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val startPoint = mStartPoint
        val endPoint = mEndPoint
        val touchView = mTouchView

        if (event.action == MotionEvent.ACTION_DOWN) {
            //record the start position
            startPoint.x = event.x
            startPoint.y = event.y
        } else if (event.action == MotionEvent.ACTION_UP) {
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
            if (sqrt((endPoint.x - startPoint.x).toDouble().pow(2.0) +
                            (endPoint.y - startPoint.y).toDouble().pow(2.0)) > CLICK_RESPONSE_DISTANCE) {
                return true
            }
            // Compute the click event at which range.
            var clickHashX: Int = (endPoint.x / mBasicElementWidth).toInt()
            var clickHashY: Int = (endPoint.y / mBasicElementHeight).toInt()
            if (clickHashX >= 7) {
                clickHashX = 6
            }
            if (clickHashY >= 12) {
                clickHashY = 11
            }
            clickHashY = if (clickHashY % 2 == 0) clickHashY else clickHashY - 1
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
            val params = LayoutParams(mBasicElementWidth,
                    mBasicElementHeight * 2)
            params.leftMargin = mElementGap * (clickHashX + 1) + mBasicElementWidth * clickHashX
            params.topMargin = mElementGap * (clickHashY + 1) + mBasicElementHeight * clickHashY
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

    fun clearTouchView() {
        if (mTouchView.tag != null) {
            mTouchView.tag = null
            removeView(mTouchView)
        }
    }

    override fun onDraw(canvas: Canvas) {
        LogUtils.d("${this::class.java.simpleName}GGG${this.hashCode()}", "onDraw")

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
     * @param itemWidth 表示一个课占多宽
     * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
     */
    data class ScheduleItem(val itemWidth: Int = 1, val itemHeight: Int = 2)
}