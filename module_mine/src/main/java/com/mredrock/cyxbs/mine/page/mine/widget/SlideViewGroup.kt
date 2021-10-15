package com.mredrock.cyxbs.mine.page.mine.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.mine.R

class SlideViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent2 {

    /**
     * 嵌套滑动的速度
     */
    private var slideVelocity = 0f

    private var isExecuteAnimation = false
    private val mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var mTopView //头部view
            : View? = null
    private var mNavView //导航view
            : View? = null
    private var mViewPager //Viewpager
            : ViewPager2? = null
    private var mScrollChangeListener: ((moveRatio: Float) -> Unit)? = null

    /**
     * 父控件可以滚动的距离
     */
    private var mCanScrollDistance = 0f

    init {
        mViewPager = findViewById(R.id.vp2_mine)
    }

    private var childNestedScrollingChild:RecyclerView? = null

    /**
     * 父控件接受嵌套滑动，不管是手势滑动还是fling 父控件都接受
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        childNestedScrollingChild = target  as RecyclerView
        valueAnimator?.cancel()
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {

        valueAnimator?.cancel()
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        valueAnimator?.cancel()
        //如果子view欲向上滑动，则先交给父view滑动
        val hideTop = dy > 0 && scrollY < mCanScrollDistance
        //如果子view欲向下滑动，必须要子view不能向下滑动后，才能交给父view滑动
        val showTop = dy < 0 && scrollY >= 0 && !target.canScrollVertically(-1)
        if (hideTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy // consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离
        }
    }


    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
            if (dyConsumed==0){
                childNestedScrollingChild?.stopScroll()
            }
        if (dyUnconsumed < 0) { //表示已经向下滑动到头，这里不用区分手势还是fling
            scrollBy(0, dyUnconsumed)
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {

        if(scrollY!=mCanScrollDistance.toInt()&&scrollY!=0){
            if (slideVelocity==0f&&type==1){

                onStopAnimator()
            }
            else if (type==0&&slideVelocity==0f){

                onStopAnimator()
            }
        }
        slideVelocity=0f

    }

    /**
     * 嵌套滑动时，如果父View处理了fling,那子view就没有办法处理fling了，所以这里要返回为false
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        valueAnimator?.cancel()
        slideVelocity  = velocityY

        return false
    }
    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        valueAnimator?.cancel()
        return false
    }
    override fun onFinishInflate() {
        super.onFinishInflate()
        mTopView = findViewById(R.id.sl_top_view)
        mNavView = findViewById(R.id.mine_tablayout)
        mViewPager = findViewById(R.id.vp2_mine)

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //先测量一次
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //ViewPager修改后的高度= 总高度-TabLayout高度
        val lp = mViewPager!!.layoutParams
        lp.height = measuredHeight - mNavView!!.measuredHeight*3
        mViewPager!!.layoutParams = lp
        //因为ViewPager修改了高度，所以需要重新测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mCanScrollDistance =
            (mTopView!!.measuredHeight - context.dp2px(85f)).toFloat() //getResources().getDimension(R.dimen.normal_title_height);

    }
    var proportion = 0f
    override fun scrollTo(x: Int, y: Int) {
        var y = y

        if (y < 0&&!isExecuteAnimation) {
            childNestedScrollingChild?.stopScroll()
            y = 0
        }
        if (y > mCanScrollDistance&&!isExecuteAnimation) {

            y = mCanScrollDistance.toInt()

        }
        if (mScrollChangeListener != null) {

                if (y>=mCanScrollDistance){
                    proportion = 1f
                }else{
                    proportion = y / mCanScrollDistance
                }
            mScrollChangeListener!!.invoke( proportion)
        }
        if (scrollY != y) super.scrollTo(x, y)
    }
    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }
        /**
         * 移动监听
         *
         * @param moveRatio 移动比例
         */
    fun setScrollChangeListener(scrollChangeListener: (moveRatio: Float) -> Unit) {
        scrollChangeListener.also { mScrollChangeListener = it }
    }
    var valueAnimator:ValueAnimator?=null
    /**
     * 上滑825 下滑0
     */
    fun onStopAnimator(){
           valueAnimator?.cancel()
        if (scrollY>=mCanScrollDistance*2/5){   //完全上滑
           valueAnimator = ValueAnimator.ofInt(scrollY,mCanScrollDistance.toInt())
        }else{   //完全下滑
             valueAnimator = ValueAnimator.ofInt(scrollY,0)
        }
        valueAnimator?.duration = 650
        valueAnimator?.interpolator = OvershootInterpolator()
        valueAnimator?.addUpdateListener {
            scrollTo(0,it.animatedValue as Int)
        }
        valueAnimator?.addListener(
            onCancel = {
                isExecuteAnimation=false
            },
            onStart = {
                isExecuteAnimation = true
            },
            onEnd = {
                isExecuteAnimation = false
            }

        )
         valueAnimator?.start()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                valueAnimator?.cancel()
            }
            MotionEvent.ACTION_UP->{

            }
            MotionEvent.ACTION_MOVE->{

            }
            MotionEvent.ACTION_CANCEL->{

            }

        }

        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                valueAnimator?.cancel()
            }
            MotionEvent.ACTION_UP->{

            }
            MotionEvent.ACTION_MOVE->{

            }
            MotionEvent.ACTION_CANCEL->{

            }

        }
        return super.onInterceptTouchEvent(event)
    }








































}