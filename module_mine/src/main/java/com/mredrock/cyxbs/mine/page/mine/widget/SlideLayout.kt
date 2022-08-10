package com.mredrock.cyxbs.mine.page.mine.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.mine.R

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
     * 设置图标view
     */
    private var settingView:View?=null
    /**
     * 设置图标的文字
     */
    private var settingTextView:View?=null
    /**
     * 侧滑需要展示的设置view
     */
    private var menuViewSetting: View? = null
    /**
     * 侧滑需要展示的删除view
     */
private var menuViewDelete:View? = null
    /**
     * 滚动者
     */
    private var scroller: Scroller? = null

    /**
     * Content的宽
     */
    private var contentWidth = 0

    /**
     * 侧滑设置view的宽
     */
    private var menuSettingWidth = 0

    /**
     * 侧滑删除View的宽
     */
    private var menuDeleteWidth = 0

    private var viewHeight //他们的高都是相同的
            = 0
    private var percentage=0f

    /**
     * 这是一个滑动过程中根据滑动的距离来计算的 整个过程的百分比 好对透明度等做出改变
     */
    private var x=MutableLiveData<Float>()

    /**
     * 上下的偏移量 为了让三个item保持在同一水平线上
     */
    private var verticalOffset=0

    private var isOpen = false
    init {
        scroller = Scroller(context)
        x.observeForever {
          percentage =it/ (menuSettingWidth+menuDeleteWidth)
            menuViewSetting?.alpha = percentage
            menuViewDelete?.alpha = percentage*1.5f
            if(percentage>0.3){
                settingView?.scaleX=percentage
                settingView?.scaleY=percentage
            }
        }
    }

    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView = getChildAt(2)
        menuViewDelete = getChildAt(1)
        menuViewSetting = getChildAt(0)
        settingView=menuViewSetting?.findViewById(R.id.rl_menu_setting)
     //   settingTextView=menuViewSetting?.findViewById(R.id.tv_item_setting)
    }


    /**
     * 在测量方法里，得到各个控件的高和宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        contentWidth = contentView!!.measuredWidth
        menuSettingWidth = menuViewSetting!!.measuredWidth-context.dp2px(16f)*2
        menuDeleteWidth = menuViewDelete!!.measuredWidth-context.dp2px(16f)*2
        viewHeight = contentView!!.measuredHeight
        verticalOffset=(measuredHeight-viewHeight)/2
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //指定菜单的位置
        menuViewSetting!!.layout(contentWidth, verticalOffset, contentWidth + menuSettingWidth+context.dp2px(16f)*2, viewHeight+verticalOffset)
       menuViewDelete!!.layout(contentWidth + menuSettingWidth, verticalOffset, contentWidth + menuSettingWidth+menuDeleteWidth+context.dp2px(16f)*2, viewHeight+verticalOffset)//layout(contentWidth + menuSettingWidth+context.dp2px(16f)*2, 0, contentWidth + menuSettingWidth+context.dp2px(16f)*2+menuDeleteWidth, viewHeight)
    }

    private var startX = 0f
    private var startY = 0f
    private var downX //只赋值一次
            = 0f
    private var downY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isOpen) {
            //水平方向滑动
            //响应侧滑
            //反拦截-事件给SlideLayout
            parent.requestDisallowInterceptTouchEvent(true)
        }
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                //1.按下记录坐标
                //1.按下记录坐标
                downX = event.x.also { startX = it }
                downY = event.y.also { startY = it }


            }
            MotionEvent.ACTION_MOVE->{
                //2.记录结束值
                //2.记录结束值
                val endX = event.x
                val endY = event.y
                //3.计算偏移量
                //3.计算偏移量
                val distanceX = (endX - startX)*2
                var toScrollX = (scrollX - distanceX).toInt()
                if (toScrollX < 0) {
                    toScrollX = 0
                    if (!isOpen){
                        return false
                    }

                } else if (toScrollX > menuSettingWidth+menuDeleteWidth) {
                    toScrollX = menuSettingWidth+menuDeleteWidth
                }
                scrollTo(toScrollX, scrollY)

                if (scrollX!=0){
                    x.value =scrollX.toFloat()
                }
                startX = event.x
                startY = event.y
                //在X轴和Y轴滑动的距离
                //在X轴和Y轴滑动的距离
                val DX = Math.abs(endX - downX)
                val DY = Math.abs(endY - downY)
                if ( DX > 8||isOpen) {
                    //水平方向滑动
                    //响应侧滑
                    //反拦截-事件给SlideLayout
                    parent.requestDisallowInterceptTouchEvent(true)
                }

            }
            MotionEvent.ACTION_UP->{
                val totalScrollX = scrollX //偏移量
                if (totalScrollX < (menuSettingWidth+menuDeleteWidth )/ 2) {
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

                if (totalScrollX < (menuSettingWidth+menuDeleteWidth) / 2) {
                    //关闭Menu
                    closeMenu()
                } else {
                    //打开Menu
                    openMenu()
                }
            }
        }
        super.onTouchEvent(event)
      return  true
    }


    /**
     * 打开menu
     */
    fun openMenu() {
        isOpen = true
        //--->menuWidth
        val distanceX = menuSettingWidth+menuDeleteWidth - scrollX
        Log.i("开关","打开需要移动的距离"+distanceX)
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
        menuViewSetting?.alpha = 0f//防止快速滑动的时候 数据不连续 造成透明度未完全的bug

          val s = ValueAnimator.ofFloat(0f,2f)
        s.interpolator = OvershootInterpolator()
        //--->0
        val distanceX = 0 - scrollX
        Log.i("开关","关闭需要移动的距离"+distanceX)
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
        if (isOpen) {
            //水平方向滑动
            //响应侧滑
            //反拦截-事件给SlideLayout
            parent.requestDisallowInterceptTouchEvent(true)
        }
        var intercept = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                run {
                    downX = event.x.also { startX = it }
                    downY = event.y.also { startY = it }
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
                if (DX > 1) {
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


