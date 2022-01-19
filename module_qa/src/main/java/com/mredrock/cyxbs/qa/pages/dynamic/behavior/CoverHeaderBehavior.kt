package com.mredrock.cyxbs.qa.pages.dynamic.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.mredrock.cyxbs.qa.R

/**
 * @class
 * @author YYQF
 * @data 2021/9/13
 * @description
 **/

//手指向下滑动dy<0,手指向上滑动dy>0
class CoverHeaderBehavior(context: Context, attr: AttributeSet) : CoordinatorLayout.Behavior<View>(context,attr) {
    //child的translationY可移动距离，即为上方圈子RecyclerView的高度
    private var headerHeight = 0

    //child的最小translationY，上方圈子RecyclerView的top
    private var minTransY = 0f

    //child的最大translationY，上方圈子RecyclerView的bottom
    private var maxTransY = 0f

    //child的当前translationY
    private var transY = 0f

    /**
     * 多次嵌套滑动会导致多次重新layout，而我们重写了onLayoutChild，每次重新布局都会
     * 导致translationY被重置为maxTransY（初始值），所以我们加一个标记位，只允许初始化
     * 一次TranslationY
     */
    private var isLayout = false

    private var scroller: OverScroller? = null

    private lateinit var contentView: View

    // 滑动监听
    private val scrollRunnable = object : Runnable{
        override fun run() {
            scroller?.let {
                if (it.computeScrollOffset()){ // 如果已经计算过滑动偏移值
                    contentView.translationY = it.currY.toFloat() 
                    // 让View在下一次绘制时执行定义好的Runnable
                    ViewCompat.postOnAnimation(contentView, this)
                }

            }
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        contentView = child
        val circlesRv = parent.findViewById<View>(R.id.qa_rv_circles_List)

        headerHeight = circlesRv.bottom - circlesRv.top
        minTransY = circlesRv.top.toFloat()
        maxTransY = circlesRv.bottom.toFloat()

        val lp = contentView.layoutParams as CoordinatorLayout.LayoutParams
        lp.bottomMargin = headerHeight
        contentView.layoutParams = lp

        /**
         * 页面会被多次布局，本方法会被多次调用，而第一次调用时变量并未被初始化，故加入maxTransY > 0判断
         * 变量是否已被初始化
         */
        if (!isLayout && maxTransY > 0) {
            child.translationY = maxTransY
            isLayout = true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        //判断滑动方向是否垂直
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        stopAutoScroll()
        //滑动之后的translationY
        transY = child.translationY - dy
        if(transY == maxTransY){
        }
        when{
            //手指向上滑动
            dy > 0 -> {
                if (transY > minTransY){
                    //如果向上滑动后还未滑动到minTranslation，则dy由父布局全部消费
                    consumed[1] = dy
                    child.translationY = transY
                }else {
                    /**
                     * 如果滑动后translationY小于minTranslation,则父布局消费的距离为改变前的translationY和
                     * minTranslation的距离
                     */
                    consumed[1] = (child.translationY - minTransY).toInt()
                    child.translationY = minTransY
                }
            }
            //手指向下滑动
            dy < 0 -> {
                if (transY<maxTransY){
                    //如果向下滑动后还未超过到maxTranslation，则dy由父布局全部消费
                    consumed[1] = dy
                    child.translationY = transY
                }else {
                    /**
                     * 如果滑动后TranslationY大于maxTranslation,则父布局消费的距离为改变前的minTranslation和
                     * translationY的距离
                     */
                    consumed[1] = (maxTransY - child.translationY).toInt()
                    child.translationY = maxTransY
                }
            }
        }
//        child.apply {
//            layout(left,top,right,bottom)
//        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        stopAutoScroll()

        //结束滑动后，再滑动动距离最近的边界
        if(child.translationY < minTransY + headerHeight * 0.5){
            startAutoScroll(child.translationY.toInt(),minTransY.toInt(),200)
        }else {
            startAutoScroll(child.translationY.toInt(),maxTransY.toInt(),200)
        }
    }

    private fun startAutoScroll(current: Int, target: Int, duration: Int){
        if (scroller == null){
            scroller = OverScroller(contentView.context,OvershootInterpolator())
        }
        scroller?.let {
            if (it.isFinished){
                contentView.removeCallbacks(scrollRunnable)
                it.startScroll(0, current, 0, target - current, duration)
                ViewCompat.postOnAnimation(contentView, scrollRunnable)
            }
        }
    }

    private fun stopAutoScroll(){
        scroller?.let {
            if (!it.isFinished){
                it.abortAnimation()
                contentView.removeCallbacks(scrollRunnable)
            }
        }
    }
    
}