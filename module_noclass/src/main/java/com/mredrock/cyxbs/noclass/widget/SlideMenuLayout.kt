package com.mredrock.cyxbs.noclass.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction.Companion.SLIDE_MODE_LEFT
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction.Companion.SLIDE_MODE_LEFT_RIGHT
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction.Companion.SLIDE_MODE_NONE
import com.mredrock.cyxbs.noclass.callback.ISlideMenuAction.Companion.SLIDE_MODE_RIGHT
import com.mredrock.cyxbs.noclass.callback.OnSlideChangedListener
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.widget
 * @ClassName:      SlideMenuLayout
 * @Author:         Yan
 * @CreateDate:     2022年08月16日 20:02:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    左右滑动布局
 */

class SlideMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes), ISlideMenuAction {

    //view

    /**
     * 左侧视图
     */
    private var mLeftView : View? = null

    /**
     * 右侧视图
     */
    private var mRightView : View? = null

    /**
     * 主视图
     */
    private lateinit var mContentView : View

    //data
    private val mScreenWidth : Int = resources.displayMetrics.widthPixels
    private val mScreenHeight : Int = resources.displayMetrics.heightPixels

    /**
     * 左视图和右视图的宽度
     *
     * 在没有设置[mSlideLeftWidth]或[mSlideRightWidth]宽度时,左右视图参考[mSlideWidth]
     *
     * 默认为屏幕三分之一
     */
    private var mSlideWidth = mScreenWidth / 3

    /**
     * 左视图宽度
     *
     * @see [mSlideWidth]
     */
    private var mSlideLeftWidth = -1

    /**
     * 右视图宽度
     *
     * @see [mSlideWidth]
     */
    private var mSlideRightWidth = -1

    private var mContentWidth = 0
    private var mContentHeight = 0

    //attrs
    private var mSlideMode = SLIDE_MODE_NONE
    private var mSlideTime = 700
    private var mContentAlpha = 0.5f
    private var mContentShadowColor = Color.parseColor("#000000")
    private var mAllowDragging = true


    //slide
    private val mScroller: Scroller = Scroller(context)

    /**
     * 左划菜单是否打开
     */
    private var mTriggerSlideLeft = false

    /**
     * 右划菜单是否打开
     */
    private var mTriggerSlideRight = false

    //paint
    private lateinit var mContentShadowPaint: Paint

    //listener
    private var mSlideChangedListener: OnSlideChangedListener? = null

    //是否固定视图
    private var mIsFixedView : Boolean = false

    /**
     * 设置轻点击事件
     * 这个ViewGroup的点击事件是在UP中处理的，move事件可能会移动整个视图
     * 想要实现轻点击(还未移动视图时)才触发就不能写在onClickListener里面
     */
    private var mOnTapClickListener : ((SlideMenuLayout) -> Unit)? = null

    /**
     * 设置在Down事件触发的回调
     * performClick在UP中处理，需要在DOWN事件处理的回调就得另行设置
     */
    private var mOnDownClickListener : ((SlideMenuLayout) -> Unit)? = null

    init {
        initAttrs(attrs)
        initContentShadowPaint()
    }

    private fun initAttrs(attrs: AttributeSet?){
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout)
            mSlideMode =
                ta.getInteger(R.styleable.SlideMenuLayout_noclass_slideMode, mSlideMode)
            mSlideTime =
                ta.getInteger(R.styleable.SlideMenuLayout_noclass_slideTime, mSlideTime)
            mContentAlpha =
                ta.getFloat(R.styleable.SlideMenuLayout_noclass_contentAlpha, mContentAlpha)
            mContentShadowColor =
                ta.getColor(R.styleable.SlideMenuLayout_noclass_contentShadowColor, mContentShadowColor)
            mAllowDragging =
                ta.getBoolean(R.styleable.SlideMenuLayout_noclass_allowDragging, mAllowDragging)
            val slideWidth =
                ta.getDimension(R.styleable.SlideMenuLayout_noclass_slideWidth, mScreenWidth / 3f)
            mIsFixedView =
                ta.getBoolean(R.styleable.SlideMenuLayout_noclass_isFixedView,mIsFixedView)
            val slideLeftWidth =
                ta.getDimension(R.styleable.SlideMenuLayout_noclass_slideLeftWidth, -1f)
            val slideRightWidth =
                ta.getDimension(R.styleable.SlideMenuLayout_noclass_slideRightWidth, -1f)

            mSlideWidth = slideWidth.toInt()
            mSlideLeftWidth = slideLeftWidth.toInt()
            mSlideRightWidth = slideRightWidth.toInt()
            ta.recycle()
        }
    }

    private fun initContentShadowPaint(){
        mContentShadowPaint = Paint().apply {
            color = mContentShadowColor
            style = Paint.Style.FILL
        }
    }


    override fun addView(child: View?) {
        check(childCount <= 3) { "SlideMenuLayout 最多只能支持3个子view 无法将子view数量设置为${childCount}个" }
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        check(childCount <= 3) { "SlideMenuLayout 最多只能支持3个子view 无法将子view数量设置为${childCount}个" }
        super.addView(child, index)
    }

    override fun addView(child: View?, params: LayoutParams?) {
        check(childCount <= 3) { "SlideMenuLayout 最多只能支持3个子view 无法将子view数量设置为${childCount}个" }
        super.addView(child, params)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        check(childCount <= 3) { "SlideMenuLayout 最多只能支持3个子view 无法将子view数量设置为${childCount}个" }
        super.addView(child, index, params)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //设置宽度,默认全屏
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthResult: Int = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            mScreenWidth
        }
        //设置高度,默认全屏
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightResult : Int = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mScreenHeight
        }
        //初始化侧滑菜单
        initSlideView(widthResult, heightResult,widthMeasureSpec,heightMeasureSpec)
        measureSlideChild(mContentView, widthMeasureSpec, heightMeasureSpec)
        measureSlideChild(mLeftView, widthMeasureSpec, heightMeasureSpec)
        measureSlideChild(mRightView, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mContentWidth, mContentHeight)
    }

    private fun initSlideView(widthResult : Int,heightResult : Int, widthMeasureSpec : Int, heightMeasureSpec: Int){
        check(childCount != 0){ "SlideMenuLayout 至少需要拥有一个子View来显示内容"}
        mContentHeight = heightResult
        mContentWidth = widthResult
        when(childCount){
            1 -> {
                mSlideMode = SLIDE_MODE_NONE
                mContentView = getChildAt(0)
            }
            2 -> {
                when (mSlideMode) {
                    SLIDE_MODE_LEFT -> {
                        mLeftView = getChildAt(0)
                        mContentView = getChildAt(1)
                    }
                    SLIDE_MODE_RIGHT -> {
                        mRightView = getChildAt(0)
                        mContentView = getChildAt(1)
                    }
                    else -> {
//                        实际需求发现需要中间有两个view暂时设置为不可滑动
//                        throw IllegalStateException("SlideMenuLayout 拥有两个子View时只支持左划或右划 然而现在的滑动模式为: $mSlideMode")
                    }
                }
            }
            3 -> {
                mLeftView = getChildAt(0)
                mRightView = getChildAt(1)
                mContentView = getChildAt(2)
            }
        }
        //设置视图
        mLeftView?.layoutParams?.width = if(mSlideLeftWidth == -1) mSlideWidth else mSlideLeftWidth
        mLeftView?.layoutParams?.height = heightResult

        if (mLeftView != null){
            if (mIsFixedView){
                mLeftView!!.layoutParams!!.width = if(mSlideLeftWidth == -1) mSlideWidth else mSlideLeftWidth
                mLeftView!!.layoutParams!!.height = heightResult
            }else{
                measureChild(mLeftView,widthMeasureSpec,heightMeasureSpec)
                mLeftView!!.layoutParams!!.width = mLeftView!!.measuredWidth
                mLeftView!!.layoutParams!!.height = mLeftView!!.measuredHeight
            }
        }

        if (mRightView != null){
            if (mIsFixedView){
                mRightView!!.layoutParams!!.width = if(mSlideRightWidth == -1) mSlideWidth else mSlideRightWidth
                mRightView!!.layoutParams!!.height = heightResult
            }else{
                measureChild(mRightView,widthMeasureSpec,heightMeasureSpec)
                mRightView!!.layoutParams!!.width = mRightView!!.measuredWidth
                mRightView!!.layoutParams!!.height = mRightView!!.measuredHeight
            }
        }

        val contentParams = mContentView.layoutParams
        contentParams.width = widthResult
        contentParams.height = heightResult
    }

    private fun measureSlideChild(childView : View?, widthMeasureSpec : Int, heightMeasureSpec: Int){
        if (childView == null) return
        val lp: LayoutParams = childView.layoutParams
        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
            paddingLeft + paddingRight, lp.width)
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
            paddingTop + paddingBottom, lp.height)
        childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mLeftView != null) {
            mLeftView!!.layout(-mLeftView!!.measuredWidth, 0, 0, mContentHeight)
        }

        if (mRightView != null) {
            mRightView!!.layout(mContentWidth, 0, mContentWidth + mRightView!!.measuredWidth, mContentHeight)
        }

        mContentView.layout(0, 0, mContentWidth, mContentHeight)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
            changeContentViewAlpha()
        }
    }

    //滑动距离
    private var mDx = 0
    private var mDy = 0
    private var mLastX = 0
    private var mLastY = 0
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var hasInter = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mAllowDragging) return false
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                mOnDownClickListener?.invoke(this)
                mLastX = event.x.toInt()
                mLastY = event.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
                setTag(R.id.noclass_tap_click_time_tag,System.currentTimeMillis())
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x.toInt()
                val currentY = event.y.toInt()
                //拿到x方向的偏移量
                val dx = currentX - mLastX
                val dy = currentY - mLastY

                if (dx < 0) { //向左滑动
                    scrollLeft(dx)
                } else { //向右滑动
                    scrollRight(dx)
                }
                mLastX = currentX
                mLastY = currentY
                mDx = dx
                mDy = dy

                if(abs(dx)  > abs(dy) * 0.5 || hasInter){
                    hasInter = true
                    parent.requestDisallowInterceptTouchEvent(true)
                }else{
                    parent.requestDisallowInterceptTouchEvent(false)
                }

            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                performClick()
                val dTime = System.currentTimeMillis() - getTag(R.id.noclass_tap_click_time_tag) as Long
                if (dTime < 125 && mDx.absoluteValue <= touchSlop && mDy.absoluteValue <= touchSlop && scrollX.toFloat() / getCurrentRightWidth() < 0.001 && -scrollX.toFloat() / getCurrentLeftWidth() < 0.001 ){
                    mOnTapClickListener?.invoke(this)
                }
                if (mDx > 0) { //右滑
                    inertiaScrollRight()
                } else {
                    inertiaScrollLeft()
                }
                mDx = 0
                hasInter = false
            }
        }
        return true
    }

    /**
     * 获得左视图的实际宽度
     */
    private fun getCurrentLeftWidth() : Int{
        return if (!mIsFixedView){
            mLeftView?.measuredWidth ?: if(mSlideWidth == -1) mSlideLeftWidth else mSlideWidth
        }else if(mSlideLeftWidth == -1){
            mSlideWidth
        }else{
            mSlideLeftWidth
        }
    }

    /**
     * 获得右视图的实际宽度
     */
    private fun getCurrentRightWidth() : Int{
        return if (!mIsFixedView){
            mRightView?.measuredWidth ?: if(mSlideWidth == -1) mSlideRightWidth else mSlideWidth
        }else if(mSlideRightWidth == -1){
            mSlideWidth
        }else{
            mSlideRightWidth
        }
    }

    /**
     * 惯性向左
     */
    private fun inertiaScrollLeft(){
        if (mSlideMode == SLIDE_MODE_RIGHT) {
            if (scrollX >= getCurrentRightWidth() / 2) {
                openRightSlide()
            } else {
                closeRightSlide()
            }
        } else if (mSlideMode == SLIDE_MODE_LEFT) {
            if (-scrollX <= getCurrentLeftWidth() / 2) {
                closeLeftSlide()
            } else {
                openLeftSlide()
            }
        } else if (mSlideMode == SLIDE_MODE_LEFT_RIGHT) {
            if(scrollX >= getCurrentRightWidth() / 2){
                openRightSlide()
            }else if(scrollX > 0 && scrollX < getCurrentRightWidth() / 2){
                closeRightSlide()
            }else if (scrollX < 0 && -scrollX < getCurrentLeftWidth() / 2){
                closeLeftSlide()
            }else if(-scrollX >= getCurrentLeftWidth() / 2 ){
                openLeftSlide()
            }
        }
    }

    /**
     * 惯性向右
     */
    private fun inertiaScrollRight(){
        if (mSlideMode == SLIDE_MODE_LEFT) {
            if (-scrollX >= getCurrentLeftWidth() / 2) {
                openLeftSlide()
            } else {
                closeLeftSlide()
            }
        } else if (mSlideMode == SLIDE_MODE_RIGHT) {
            if (scrollX <= getCurrentRightWidth() / 2) {
                closeRightSlide()
            } else {
                openRightSlide()
            }
        } else if (mSlideMode == SLIDE_MODE_LEFT_RIGHT) {
            if(scrollX >= getCurrentRightWidth() / 2){
                openRightSlide()
            }else if(scrollX > 0 && scrollX < getCurrentRightWidth() / 2){
                closeRightSlide()
            }else if (scrollX < 0 && -scrollX < getCurrentLeftWidth() / 2){
                closeLeftSlide()
            }else if(-scrollX >= getCurrentLeftWidth() / 2 ){
                openLeftSlide()
            }

        }
    }

    /**
     * 向左滑动
     *
     * dx<0
     */
    private fun scrollLeft(dx : Int){
        when (mSlideMode) {
            SLIDE_MODE_RIGHT -> {
                //右滑菜单已经打开，不做操作
                if (scrollX > getCurrentRightWidth()) {
                    return
                }else{
                    //在要划出去的时候特判，使得能够刚好将视图移动完成
                    if(scrollX - dx > getCurrentRightWidth()){
                        scrollBy(getCurrentRightWidth() - scrollX,0)
                    }else{
                        scrollBy(-dx,0)
                    }
                }
            }
            SLIDE_MODE_LEFT -> {
                //左滑菜单未打开，不做操作
                if (scrollX>= 0) {
                    return
                }else{
                    //在要划出去的时候特判，使得能够刚好将视图移动完成
                    if(scrollX - dx >= 0){
                        scrollBy(-scrollX,0)
                    }else{
                        scrollBy(-dx, 0)
                    }
                }
            }
            SLIDE_MODE_LEFT_RIGHT -> {
                //右滑菜单已经打开，不做操作
                if (scrollX > getCurrentRightWidth()) {
                    return
                }else{
                    //在要划出去的时候特判，使得能够刚好将视图移动完成
                    if(scrollX - dx > getCurrentRightWidth()){
                        scrollBy(getCurrentRightWidth() - scrollX,0)
                    }else{
                        scrollBy(-dx,0)
                    }
                }
            }
        }
        changeContentViewAlpha()
    }


    /**
     * 向右滑动
     *
     * dx>0
     */
    private fun scrollRight(dx : Int){
        when (mSlideMode) {
            SLIDE_MODE_LEFT -> {
                if (scrollX <= -getCurrentLeftWidth()) {
                    return
                }else{
                    //在要划出去的时候特判，防止快速移动将整个控件移除边界
                    if(scrollX - dx <= -getCurrentLeftWidth()){
                        scrollBy(-getCurrentLeftWidth() - scrollX,0)
                    }else{
                        scrollBy(-dx, 0)
                    }
                }
            }
            SLIDE_MODE_RIGHT -> {
                if (scrollX < 0) {
                    return
                }else{
                    if(scrollX -dx < 0){
                        scrollBy(-scrollX,0)
                    }else{
                        scrollBy(-dx, 0)
                    }
                }
            }
            SLIDE_MODE_LEFT_RIGHT -> {
                if (scrollX <= -getCurrentLeftWidth()) {
                    return
                }else{
                    //在要划出去的时候特判，防止快速移动将整个控件移除边界
                    if(scrollX - dx <= -getCurrentLeftWidth()){
                        scrollBy(-getCurrentLeftWidth() - scrollX,0)
                    }else{
                        scrollBy(-dx, 0)
                    }
                }
            }
        }
        changeContentViewAlpha()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //设置滑动中的监听操作
        if (scrollX < 0 && (mSlideMode == SLIDE_MODE_LEFT || mSlideMode == SLIDE_MODE_LEFT_RIGHT)){
            //左侧打开
            mSlideChangedListener?.onSlideLeftChanged(-scrollX.toFloat() / getCurrentLeftWidth())
        }else{
            mSlideChangedListener?.onSlideLeftChanged(0f)
        }

        if(scrollX > 0 && (mSlideMode == SLIDE_MODE_RIGHT || mSlideMode == SLIDE_MODE_LEFT_RIGHT)){
            //右侧打开
            mSlideChangedListener?.onSlideRightChanged(scrollX.toFloat() / getCurrentRightWidth())
        }else{
            mSlideChangedListener?.onSlideRightChanged(0f)
        }
    }

    private fun changeContentViewAlpha(){
        val slideWidth : Int = if(scrollX < 0){
            getCurrentLeftWidth()
        }else if(scrollX > 0){
            getCurrentRightWidth()
        }else{
            mSlideWidth
        }

        var alpha: Float = when (val rX = abs(scrollX)) {
            0 -> {
                1.0f
            }
            slideWidth -> {
                mContentAlpha
            }
            else -> {
                abs(1.0f - ((1.0f - mContentAlpha) / slideWidth) * rX)
            }
        }
        alpha = if(alpha < 0.1f) 0.1f else alpha
        alpha = if(alpha > 1.0f) 1.0f else alpha
        mContentView.alpha = alpha
        postInvalidate()
    }

    override fun setSlideMode(@ISlideMenuAction.SlideMode slideMode: Int) {
        when (slideMode) {
            SLIDE_MODE_LEFT -> closeRightSlide()
            SLIDE_MODE_RIGHT -> closeLeftSlide()
            SLIDE_MODE_NONE -> {
                closeLeftSlide()
                closeRightSlide()
            }
            SLIDE_MODE_LEFT_RIGHT -> {
            }
        }
        mSlideMode = slideMode
    }

    /**
     * 缓慢滑动
     */
    private fun smoothScrollTo(destX: Int, destY: Int) {
        val scrollX = scrollX
        val deltaX = destX - scrollX
        var time = if (scrollX > 0){
            (deltaX * 1.0f / getCurrentRightWidth() * 1.0f) * mSlideTime
        }else{
            (deltaX * 1.0f / getCurrentLeftWidth() * 1.0f) * mSlideTime
        }
        time = abs(time)
        mScroller.startScroll(scrollX, 0, deltaX, destY, time.toInt())
        invalidate()
        if (mSlideChangedListener != null) {
            when (destX) {
                -getCurrentLeftWidth() -> { //左滑打开
                    mSlideChangedListener!!.onSlideStateChanged(this,
                        isLeftSlideOpen = true,
                        isRightSlideOpen = false)
                }
                0 -> { //关闭侧滑
                    mSlideChangedListener!!.onSlideStateChanged(this,
                        isLeftSlideOpen = false,
                        isRightSlideOpen = false)
                }
                getCurrentRightWidth() -> { //右滑打开
                    mSlideChangedListener!!.onSlideStateChanged(this,
                        isLeftSlideOpen = false,
                        isRightSlideOpen = true)
                }
            }
        }
    }

    override fun setSlideWidth(slideWidth: Int) {
        mSlideWidth = slideWidth
    }

    override fun setSlideLeftWidth(slideLeftWidth: Int) {
        mSlideLeftWidth = slideLeftWidth
    }

    override fun setSlideRightWidth(slideRightWidth: Int) {
        mSlideRightWidth = slideRightWidth
    }

    override fun setSlideTime(slideTime: Int) {
        mSlideTime = slideTime
    }


    override fun setContentAlpha(@FloatRange(from = 0.0, to = 1.0)contentAlpha: Float) {
        mContentAlpha = contentAlpha
    }

    override fun setContentShadowColor(@ColorInt color: Int) {
        mContentShadowColor = color
        if(this::mContentShadowPaint.isInitialized){
            mContentShadowPaint.color = mContentShadowColor
        }
    }


    override fun setAllowTogging(allowTogging: Boolean) {
        mAllowDragging = allowTogging
    }

    //获得视图操作

    override fun getSlideLeftView(): View? {
        return mLeftView
    }

    override fun getSlideRightView(): View? {
        return mRightView
    }

    override fun getSlideContentView(): View {
        return mContentView
    }

    //左滑相关操作

    override fun toggleLeftSlide() {
        if (mTriggerSlideLeft) {
            closeLeftSlide()
        } else {
            openLeftSlide()
        }
    }

    override fun openLeftSlide() {
        if (mSlideMode == SLIDE_MODE_RIGHT || mSlideMode == SLIDE_MODE_NONE) return
        mTriggerSlideLeft = true
        smoothScrollTo(-getCurrentLeftWidth(), 0)
    }

    override fun closeLeftSlide() {
        if (mSlideMode == SLIDE_MODE_RIGHT || mSlideMode == SLIDE_MODE_NONE) return
        mTriggerSlideLeft = false
        smoothScrollTo(0, 0)
    }

    override fun isLeftSlideOpen(): Boolean {
        return mTriggerSlideLeft
    }

    //右滑相关操作
    override fun toggleRightSlide() {
        if (mTriggerSlideLeft) {
            closeRightSlide()
        } else {
            openRightSlide()
        }
    }

    override fun openRightSlide() {
        if (mSlideMode == SLIDE_MODE_LEFT || mSlideMode == SLIDE_MODE_NONE) return
        mTriggerSlideRight = true
        smoothScrollTo(getCurrentRightWidth(), 0)
    }

    override fun closeRightSlide() {
        if (mSlideMode == SLIDE_MODE_LEFT || mSlideMode == SLIDE_MODE_NONE) return
        mTriggerSlideRight = false
        smoothScrollTo(0, 0)
    }

    override fun isRightSlideOpen(): Boolean {
        return mTriggerSlideRight
    }

    /**
     * 设置滚动监听
     */
    override fun setOnSlideChangedListener(listener: OnSlideChangedListener) {
        mSlideChangedListener = listener
    }

    fun setOnTapTouchListener(listener : (SlideMenuLayout) -> Unit){
        mOnTapClickListener = listener
    }

    fun setOnDownListener(listener : (SlideMenuLayout) -> Unit){
        mOnDownClickListener = listener
    }

}