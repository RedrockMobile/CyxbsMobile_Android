package com.mredrock.cyxbs.course.component

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.utils.createCornerBackground
import org.jetbrains.anko.dip
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
class ScheduleView : ViewGroup {

    companion object {
        const val CLICK_RESPONSE_DISTANCE = 10
    }

    var adapterChangeListener: ((Adapter) -> Unit)? = null

    private var touchClickListener: (() -> Unit)? = null

    /**
     * 接收一个继承了本类内部抽象Adapter的类对象，
     * 以此把获取数据和itemView的逻辑和itemView数据获取（仿造ViewPager或RecyclerView）
     */
    var adapter: Adapter? = null
        set(value) {
            field = value
            field?.let { adapterChangeListener?.invoke(it) }
            adapter?.let { notNullAdapter ->
                // Set the TouchView's OnClickListener
                initTouchView()
                touchClickListener = notNullAdapter.onTouchViewClick(mTouchView, (mTouchView.getTag(R.id.touchPosition) as TouchPositionData))
                mTouchView.setOnClickListener { touchClickListener?.invoke() }
                // When we add new courses, remove old values first.
                removeAllViews()
                addCourseView(notNullAdapter)
                showAnimationForTheFirstTime()
            }
        }

    private val mStartPoint: PointF by lazy(LazyThreadSafetyMode.NONE) { PointF() }
    private val mEndPoint: PointF by lazy(LazyThreadSafetyMode.NONE) { PointF() }

    // mTouchView is used to add affair
    private val mTouchView: ImageView by lazy(LazyThreadSafetyMode.NONE) { ImageView(context).apply { setTag(R.id.touchPosition, TouchPositionData(0)) } }

    // This paint is used to draw the place which will be highlighted.
    private lateinit var mPaint: Paint

    // This is used to represent whether the courses and affairs are null
    private var mIsEmpty: Boolean = true

    // This is the TextView to show when the courses and affairs are null
    private val mEmptyTextView: TextView by lazy(LazyThreadSafetyMode.NONE) { TextView(context) }

    private var courseItemCount = 0

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
        mHeightAtLowDpi = dip(620f)
        initScheduleView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView,
                R.attr.ScheduleViewStyle, 0)
        mElementGap = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_elementGap, dip(2f))
        noCourseImageHeight = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_noGourseImageHeight, dip(0f))
        noCourseImageWidth = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_noGourseImageWidth, dip(0f))
        mTouchViewColor = typeArray.getColor(R.styleable.ScheduleView_touchViewColor, Color.parseColor("#bdc3c7"))
        mHeightAtLowDpi = typeArray.getDimensionPixelSize(R.styleable.ScheduleView_heightAtLowDpi, dip(620f))
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
        layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.course_item_show_anim))
        mPaint = Paint().apply {
            color = mHighlightColor
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    private fun layoutTransition(): LayoutTransition {
        val mLayoutTransition = LayoutTransition()
        //课表显示动画item，切记这里不是第一次显示的动画
        mLayoutTransition.setStagger(LayoutTransition.APPEARING, 500)
        val appearingScaleX: PropertyValuesHolder = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f)
        val appearingScaleY: PropertyValuesHolder = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f)
        val mAnimatorAppearing: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, appearingScaleX, appearingScaleY)
        mLayoutTransition.setAnimator(LayoutTransition.CHANGING, mAnimatorAppearing)
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
        if (mIsEmpty && mNoCourseDrawableResId != 0) {
            // Set mEmptyTextView
            mEmptyTextView.apply {
                val drawable = ContextCompat.getDrawable(context, mNoCourseDrawableResId)
                drawable?.setBounds(0, 0,
                        if (noCourseImageWidth == 0) dip(100) else noCourseImageWidth,
                        if (noCourseImageHeight == 0) dip(100) else noCourseImageHeight)
                setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
                compoundDrawablePadding = dip(25)
                val textParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT)
                text = mEmptyText
                textSize = px2sp(mEmptyTextSize)
                gravity = Gravity.CENTER
                layoutParams = textParams
            }
            if (mEmptyTextView.parent == null) {
                addView(mEmptyTextView)
            }
        } else if (mEmptyTextView.parent == null) {
            addView(mEmptyTextView)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childStart = childView.getTag(R.id.childLeft) as Int
            val childTop = childView.getTag(R.id.childTop) as Int
            val childEnd = childStart + childView.measuredWidth
            val childBottom = childTop + childView.measuredHeight
            childView.layout(childStart, childTop, childEnd, childBottom)
        }
    }


    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        when {
            mEmptyTextView == child -> {
                mEmptyTextView.setTag(R.id.childTop, mScheduleViewHeight / 5)
                mEmptyTextView.setTag(R.id.childLeft, (mScheduleViewWidth - if (noCourseImageWidth == 0) mScheduleViewWidth else noCourseImageWidth) / 2)
                super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec)
            }
            mIsDisplayCourse -> {
                actionItemInfoStatus(child, infoFound = { itemViewInfo, row, column ->
                    val childWidth = mBasicElementWidth
                    val childHeight = (mBasicElementHeight * itemViewInfo.itemHeight + (itemViewInfo.itemHeight - 1) * mElementGap)
                    child.setTag(R.id.childTop, mElementGap * (row + 1) + (mBasicElementHeight * 2 + mElementGap) * row)
                    child.setTag(R.id.childLeft, mElementGap * (column + 1) + mBasicElementWidth * column)
                    val widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
                    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
                    child.measure(widthMeasureSpec, heightMeasureSpec)
                }, infoNotFound = { super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec) })
            }
            !mIsDisplayCourse -> {
                actionItemInfoStatus(child, infoFound = { itemViewInfo, row, column ->
                    val childWidth = mBasicElementWidth
                    val childHeight = (mBasicElementHeight * itemViewInfo.itemHeight + mElementGap * (itemViewInfo.itemHeight - 1))
                    child.setTag(R.id.childTop, mElementGap * (row + 1) + mBasicElementHeight * row)
                    child.setTag(R.id.childLeft, mElementGap * (column + 1) + mBasicElementWidth * column)
                    child.measure(
                            MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY))
                }, infoNotFound = { super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec) })
            }
        }
    }

    /**
     * 动态更新
     */
    fun notifyDataChange() {
        adapter?.let {
            addCourseView(it, true)
        }
    }

    /**
     *
     */
    private fun addCourseView(notNullAdapter: Adapter, isCheckChange: Boolean = false) {
        courseItemCount = 0
        if (isCheckChange) notNullAdapter.notifyDataChange()
        //为了不影响检查变化
        clearTouchView()
        // 如果是课表，每节课的开始都是偶数，0、2、4...这样开始的。因此这里就使用6 * 7的矩阵。
        // 但是如果是没课约，有可能是奇数开始的，因此这里需要使用12 * 7的矩阵运算。
        if (mIsDisplayCourse) {
            for (row in 0..5) {
                Column@ for (column in 0..6) {
                    val itemViewInfo = notNullAdapter.getItemViewInfo(row, column)
                    if (isCheckChange) {
                        var removeChildren: View? = null
                        for (view in children) {
                            if (row == view.getTag(R.id.item_position_row) && column == view.getTag(R.id.item_position_column)) {
                                notNullAdapter.initItemView(view, row, column)
                                courseItemCount++
                                if (itemViewInfo?.uniqueSign?.equals((view.getTag(R.id.item_info) as? ScheduleItem)?.uniqueSign) == true) {
                                    continue@Column
                                } else {
                                    removeChildren = view
                                }
                            }
                        }
                        removeChildren?.let { removeView(it) }
                    }
                    //if this row and column don't have course or affair continue.
                    itemViewInfo ?: continue
                    val itemView = notNullAdapter.getItemView(row, column, this@ScheduleView)
                    notNullAdapter.initItemView(itemView, row, column)
                    //这里设置tag，将真正用来测量和布局的代码放到标准的view绘制流程的回调方法里
                    //如果直接在这进行测量或者布局，在activity异常重启而导致的view异常重启之后
                    //无法异常重启的view第一次显示时，能够获取准确的测绘高度和宽度
                    itemView.setTag(R.id.item_position_row, row)
                    itemView.setTag(R.id.item_position_column, column)
                    itemView.setTag(R.id.item_info, itemViewInfo)
                    addView(itemView)
                    courseItemCount++
                    mIsEmpty = false
                }
            }
        } else {
            for (column in 0..6) {
                var row = 0
                Row@ while (row < 12) {
                    val itemViewInfo = notNullAdapter.getItemViewInfo(row, column)
                    if (isCheckChange) {
                        var removeChildren: View? = null
                        for (view in children) {
                            if (row == view.getTag(R.id.item_position_row) && column == view.getTag(R.id.item_position_column)) {
                                notNullAdapter.initItemView(view, row, column)
                                courseItemCount++
                                if (itemViewInfo?.uniqueSign?.equals((view.getTag(R.id.item_info) as? ScheduleItem)?.uniqueSign) == true) {
                                    continue@Row
                                } else {
                                    removeChildren = view
                                }
                            }
                        }
                        removeChildren?.let { removeView(it) }
                    }
                    if (itemViewInfo == null) {
                        row++
                        continue
                    }
                    val itemView = notNullAdapter.getItemView(row, column, this@ScheduleView)
                    notNullAdapter.initItemView(itemView, row, column)
                    itemView.setTag(R.id.item_position_row, row)
                    itemView.setTag(R.id.item_position_column, column)
                    itemView.setTag(R.id.item_info, itemViewInfo)
                    addView(itemView)
                    courseItemCount++
                    row += itemViewInfo.itemHeight
                    mIsEmpty = false
                }
            }
        }
        checkIsNoCourse()
    }

    /**
     * 检查是否需要添加没课提示图片
     */
    private fun checkIsNoCourse() {
        if (courseItemCount == 0) {
            addNoCourseView()
        } else {
            removeView(mEmptyTextView)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val startPoint = mStartPoint
        val endPoint = mEndPoint
        if (event.action == MotionEvent.ACTION_DOWN) {
            //record the start position
            startPoint.x = event.x
            startPoint.y = event.y
        } else if (event.action == MotionEvent.ACTION_UP) {
            endPoint.x = event.x
            endPoint.y = event.y
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        val startPoint = mStartPoint
        val endPoint = mEndPoint
        val touchView = mTouchView
        // If the move distance more than CLICK_RESPONSE_DISTANCE. This MotionEvent won't service
        // as a click event.
        val isClick = sqrt((endPoint.x - startPoint.x).toDouble().pow(2.0) + (endPoint.y - startPoint.y).toDouble().pow(2.0)) > CLICK_RESPONSE_DISTANCE

        //虽然按照常理来说这里必定不可能大于6，
        //但是不知道是不是全面屏手势的影响，交界处的计算的值是大于6的，下同
        val clickHashX: Int = (endPoint.x / (mBasicElementWidth + mElementGap)).toInt().run {
            if (this >= 7) {
                6
            } else this
        }
        val clickHashY: Int = (endPoint.y / (mBasicElementHeight * 2 + mElementGap)).toInt().run {
            if (this >= 6) {
                5
            } else this
        }
        return if (touchClickListener != null && !mIsEmpty && !isClick && adapter?.getItemViewInfo(clickHashY, clickHashX) == null) {
            clearTouchView()
            (touchView.getTag(R.id.touchPosition) as TouchPositionData).position = (clickHashY) * 7 + clickHashX
            // Compute the mTouchView's LayoutParams.
            touchView.layoutParams = LayoutParams(mBasicElementWidth, mBasicElementHeight * 2 + mElementGap)
            touchView.setTag(R.id.item_position_row, clickHashY)
            touchView.setTag(R.id.item_position_column, clickHashX)
            touchView.setTag(R.id.item_info, ScheduleItem())
            // Add the mTouchView to the ScheduleView.
            addView(touchView)
            true
        } else super.performClick()
    }

    fun clearTouchView() {
        if (mTouchView.parent != null) {
            removeView(mTouchView)
        }
    }

    private fun showAnimationForTheFirstTime() {
        startLayoutAnimation()
        //保证出场动画只显示一次
        layoutAnimation?.animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                layoutAnimation.animation.cancel()
            }
        })
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
        open fun onTouchViewClick(view: View, position: TouchPositionData): (() -> Unit)? = null

        /**
         * This method is used to get the position which will be highlighted.
         *
         * @return not null: the highlight position.
         * null: don't highlight.
         */
        abstract fun getHighLightPosition(): Int?

        //当该view调用notifyDataChange方法时会回调这个方法
        open fun notifyDataChange() {}

        open fun initItemView(view: View, row: Int, column: Int) {}
    }

    /**
     * This class represents the ItemSize going to display
     *
     * @param itemWidth 表示一个课占多宽
     * @param itemHeight 表示有多少节课的高度。每天的课有12节，这个就表示多少个12分之一。
     * @param uniqueSign 会根据这个标志来确定更新前后是否为同一个信息，若不设置，则不会对item更改生效，只会对删除添加生效
     */
    data class ScheduleItem(val itemWidth: Int = 1, val itemHeight: Int = 2, val uniqueSign: Any = Any())

    /**
     * 为了记录touchView的位置
     * @param
     */
    data class TouchPositionData(var position: Int)
}