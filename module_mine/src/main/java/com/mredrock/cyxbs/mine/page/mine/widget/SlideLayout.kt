package com.mredrock.cyxbs.mine.page.mine.widget

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.dp2px

class SlideLayout @JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
):FrameLayout(context,attrs,defStyleAttr) {

    /**
     * 正常显示的view
     */
    private var contentView: View? = null

    /**
     * 侧滑需要展示的view
     */
    private var menuView: View? = null

    /**
     * 滚动者
     */
    private var scroller: Scroller? = null

    /**
     * Content的宽
     */
    private var contentWidth = 0

    /**
     * 侧滑view的宽
     */
    private var menuWidth = 0

    private var viewHeight //他们的高都是相同的
            = 0
    private var percentage=0f

    /**
     * 这是一个滑动过程中根据滑动的距离来计算的 整个过程的百分比 好对透明度等做出改变
     */
    private var x=MutableLiveData<Float>()

    private var isOpen = false
    init {
        scroller = Scroller(context)
        x.observeForever {
          percentage =it/ menuWidth
            menuView?.alpha = percentage

        }
    }

    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView = getChildAt(1)
        menuView = getChildAt(0)
    }


    /**
     * 在测量方法里，得到各个控件的高和宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        contentWidth = contentView!!.getMeasuredWidth()
        menuWidth = menuView!!.measuredWidth-context.dp2px(16f)*2
        viewHeight = measuredHeight
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //指定菜单的位置
        menuView!!.layout(contentWidth, 0, contentWidth + menuWidth+context.dp2px(16f)*2, viewHeight)
    }

    private var startX = 0f
    private var startY = 0f
    private var downX //只赋值一次
            = 0f
    private var downY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                //1.按下记录坐标
                //1.按下记录坐标
                downX = event.x.also { startX = it }
                downY = event.y.also { startY = it }
               // Log.e(SlideLayout.TAG, "SlideLayout-onTouchEvent-ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE->{
             //   Log.e(SlideLayout.TAG, "SlideLayout-onTouchEvent-ACTION_MOVE")
                //2.记录结束值
                //2.记录结束值
                val endX = event.x
                val endY = event.y
                //3.计算偏移量
                //3.计算偏移量
                val distanceX = endX - startX


                var toScrollX = (scrollX - distanceX).toInt()
                if (toScrollX < 0) {
                    toScrollX = 0
                    if (!isOpen){
                        return false
                    }

                } else if (toScrollX > menuWidth) {
                    toScrollX = menuWidth
                }

                scrollTo(toScrollX, scrollY)
                if (scrollX!=0){
                   Log.e("wxtag","(SlideLayout.kt:123)->>观察了 ")
                    x.value =scrollX.toFloat()
                }

                startX = event.x
                startY = event.y
                //在X轴和Y轴滑动的距离
                //在X轴和Y轴滑动的距离
                val DX = Math.abs(endX - downX)
                val DY = Math.abs(endY - downY)
                if (DX > DY && DX > 8) {
                    //水平方向滑动
                    //响应侧滑
                    //反拦截-事件给SlideLayout
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP->{
                val totalScrollX = scrollX //偏移量

                if (totalScrollX < menuWidth / 2) {
                    //关闭Menu
                    closeMenu()
                } else {
                    //打开Menu
                    openMenu()
                }
            }
            MotionEvent.ACTION_CANCEL->{
               // Log.e(SlideLayout.TAG, "SlideLayout-onTouchEvent-ACTION_UP")
                val totalScrollX = scrollX //偏移量

                if (totalScrollX < menuWidth / 2) {
                    //关闭Menu
                    closeMenu()
                } else {
                    //打开Menu
                    openMenu()
                }
            }
        }
      return  true
    }


    /**
     * 打开menu
     */
    fun openMenu() {
        isOpen = true
        //--->menuWidth
        val distanceX = menuWidth - scrollX
        scroller!!.startScroll(scrollX, scrollY, distanceX, scrollY)
        invalidate() //强制刷新
        if (onStateChangeListenter != null) {
            onStateChangeListenter?.onOpen(this)
        }
    }

    /**
     * 关闭menu
     */
    fun closeMenu() {
        isOpen=false
        //--->0
        val distanceX = 0 - scrollX

        scroller!!.startScroll(scrollX, scrollY, distanceX, scrollY)
        invalidate() //强制刷新
        if (onStateChangeListenter != null) {
            onStateChangeListenter?.onClose(this)
        }
    }


    override fun computeScroll() {
        super.computeScroll()
        if (scroller!!.computeScrollOffset()) {
            scrollTo(scroller!!.currX, scroller!!.currY)
            /**
             * 看似怪异的写法 实际上是为了提升性能 当Rv列表上下滑动的时候 也会走这个方法 在scrollX为零的时候造成不必要的观察
             */
            if (scrollX!=0){
                x.value =scrollX.toFloat()
                if (scrollX<=2){  //防止最后观察的值里面缺少零  造成透明度不完全的bug
                    x.value=0f
                }
            }

            invalidate()
        }
    }

    /**
     * true:拦截孩子的事件，但会执行当前控件的onTouchEvent()方法
     * false:不拦截孩子的事件，事件继续传递
     * @param event
     * @return
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var intercept = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    startX = event.x
                    downX = startX
                }

                if (onStateChangeListenter != null) {
                    onStateChangeListenter?.onDown(this)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //2.记录结束值
                val endX = event.x
                val endY = event.y
                //3.计算偏移量
                val distanceX: Float = endX - startX
                startX = event.x
                //在X轴和Y轴滑动的距离
                val DX: Float = Math.abs(endX - downX)
                if (DX > 8) {
                    intercept = true
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return intercept
    }


    /**
     * 监听SlideLayout状态的改变
     */
    interface OnStateChangeListenter {
        fun onClose(layout: SlideLayout?)
        fun onDown(layout: SlideLayout?)
        fun onOpen(layout: SlideLayout?)
    }

    private var onStateChangeListenter: OnStateChangeListenter? = null

    /**
     * 设置SlideLayout状态的监听
     * @param onStateChangeListenter
     */
  open  fun setOnStateChangeListenter(onStateChangeListenter: OnStateChangeListenter?) {
        this.onStateChangeListenter = onStateChangeListenter
    }

}


