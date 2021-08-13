package com.cyxbsmobile_single.module_todo.adapter.slide_callback

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.SlidePosition.*
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.SlideStatus.*
import com.mredrock.cyxbs.common.utils.LogUtils
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

/**
 * Author: RayleighZ
 * Time: 2021-08-09 19:24
 * 侧滑卡片折叠按钮的RecyclerView Callback
 */
class SlideCallback :
    ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT)

    enum class SlideStatus {
        OPEN,
        CLOSED
    }

    enum class SlidePosition{
        HALF_LEFT,
        HALF_RIGHT,
        FULL_RIGHT,
        FULL_LEFT
    }

    //当前滑动状态记录
    var curStatus = CLOSED

    //当前滑动处于那种状态
    var curSlideStatus = FULL_LEFT

    //标识是否可以跟随dX
    var canFollowDx = true

    //标识是否是第一次松开手指
    var isFirstTimeReleaseFinger = true

    var peakDx = 0f

    var delWidth = 0
    var itemWidth = 0

    //过渡滑动用的动画
    private val transAnime by lazy {
        ValueAnimator.ofFloat(1f, 0f).apply {
            //配置插值器
            interpolator = DecelerateInterpolator()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        viewHolder.itemView.apply {
            itemWidth = width
            //跟着Dx位移
            fun transWidthWithDx() {
                todo_fl_del.apply {
                    delWidth = width
                    val process = (-dX / width * 0.2f + 0.8f).coerceAtMost(1f)
                    pivotX = 0f
                    pivotY = height.toFloat() / 2f
                    scaleX = process
                    scaleY = process
                    alpha = process
                }
                todo_cl_item_main.apply {
                    translationX =
                        when (curStatus) {
                            CLOSED -> dX.coerceAtLeast(-delWidth.toFloat())
                                .coerceAtMost(0f)
                            OPEN -> (-(delWidth - (dX - peakDx))).coerceAtLeast(-delWidth.toFloat())
                                .coerceAtMost(0f)
                        }
                }
            }

            if (canFollowDx)
                transWidthWithDx()

            LogUtils.d(
                "RayleighZ",
                "transDx = $dX, delWidth = ${todo_fl_del.width}, peakDx = $peakDx, canFollowDx = $canFollowDx, dx - peakDx = ${dX - peakDx}"
            )

            if (!isCurrentlyActive) {

                if (isFirstTimeReleaseFinger){
                    isFirstTimeReleaseFinger = false
                } else {
                    //此处判断是否需要跟进dX更新
                    peakDx = if (curStatus == OPEN) dX  else 0f
                    return
                }

                when {
                    (dX - peakDx) <= -delWidth / 2f -> {
                        //手指离开屏幕时，已经处于打开状态
                        curStatus = OPEN
                        //这时尚未滑动到delWidth之外，但根据计算需要，将peakDx赋值为-delWidth
                        peakDx = dX
                        curSlideStatus = HALF_RIGHT
                    }

                    (dX - peakDx) >= delWidth / 2f -> {
                        //这里的逻辑是：如果是从关闭状态滑过来，需要跟随系统动画
                        //只有当手动关闭的时候，才不跟随
                        canFollowDx = curStatus == CLOSED
                        LogUtils.d("RayleighZ","setStatus CLOSED")
                        //手指离开屏幕时，以处于关闭状态
                        curStatus = CLOSED
                        curSlideStatus = HALF_LEFT
                        if (!canFollowDx){
                            //这个时候不能跟随dX，因为系统会直接弹回去
                            //此时需要开启我们设定的动画
                            val remainWidth = delWidth - (dX - peakDx)
                            transAnime.duration = (remainWidth / delWidth * 500).toLong()
                            transAnime.addUpdateListener {
                                //计算需要位移的距离
                                val curRemainWidth = remainWidth * (it.animatedValue as Float)
                                viewHolder.itemView.apply {
                                    LogUtils.d("RayleighZ", "动画在更新, curRemainWidth = $curRemainWidth")
                                    todo_cl_item_main.translationX = (-curRemainWidth).coerceAtLeast(-delWidth.toFloat())
                                        .coerceAtMost(0f)
                                }
                            }
                            transAnime.doOnEnd {
                                peakDx = 0f
                                canFollowDx = true
                            }
                            transAnime.start()
                        } else {
                            peakDx = 0f
                        }
                    }
                }
            } else {
                isFirstTimeReleaseFinger = true
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
//        //判断为：如果滑动到hide的部分的1/2，就认定为出去
//        val hide = viewHolder.itemView.todo_fl_del
//        val itemWidth = viewHolder.itemView.width
//
//        return (hide.width / 2f) / itemWidth.toFloat()
        return Int.MAX_VALUE.toFloat()
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = defaultValue * 100

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //以为我们是侧滑按钮点击删除，所以这里不写任何删除相关逻辑
    }
}