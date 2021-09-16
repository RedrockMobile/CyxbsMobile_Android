package com.mredrock.cyxbs.mine.page.mine.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.mine.R

class SlideViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent2 {


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

    /**
     * 父控件接受嵌套滑动，不管是手势滑动还是fling 父控件都接受
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
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
        if (dyUnconsumed < 0) { //表示已经向下滑动到头，这里不用区分手势还是fling
            scrollBy(0, dyUnconsumed)
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type)
    }

    /**
     * 嵌套滑动时，如果父View处理了fling,那子view就没有办法处理fling了，所以这里要返回为false
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
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
       Log.e("wxtag","(SlideViewGroup.kt:111)->> 高度前${lp.height}")
        lp.height = measuredHeight - mNavView!!.measuredHeight*3
        Log.e("wxtag","(SlideViewGroup.kt:111)->> 高度后${lp.height}")
        mViewPager!!.layoutParams = lp
        //因为ViewPager修改了高度，所以需要重新测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("wxtag","(SlideViewGroup.kt:111)->>  super.onMeasure后${lp.height}")
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mCanScrollDistance =
            (mTopView!!.measuredHeight - 300).toFloat() //getResources().getDimension(R.dimen.normal_title_height);

    }

    override fun scrollTo(x: Int, y: Int) {
        var y = y
        if (y < 0) {
            y = 0
        }
        Log.d("view", "移动的位置" + getY())
        if (y > mCanScrollDistance) {
            y = mCanScrollDistance.toInt()
        }
        if (mScrollChangeListener != null) {
            //  Log.d("测试","移动的位置"+mCanScrollDistance);
            mScrollChangeListener!!.invoke(y / mCanScrollDistance)
        }
        if (scrollY != y) super.scrollTo(x, y)
    }


    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }



//        /**
//         * 移动监听
//         *
//         * @param moveRatio 移动比例
//         */
//        fun onScroll(moveRatio: Float)


    fun setScrollChangeListener(scrollChangeListener: (moveRatio: Float) -> Unit) {
        scrollChangeListener.also { mScrollChangeListener = it }
    }













































}