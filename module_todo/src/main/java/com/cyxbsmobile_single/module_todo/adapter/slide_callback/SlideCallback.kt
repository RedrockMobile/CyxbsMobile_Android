package com.cyxbsmobile_single.module_todo.adapter.slide_callback

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.adapter.slide_callback.SlideCallback.UserIntent.*
import com.mredrock.cyxbs.common.utils.LogUtils
import kotlinx.android.synthetic.main.todo_rv_item_todo.view.*

/**
 * @date 2021-08-14
 * @author Sca RayleighZ
 */
class SlideCallback :
    ItemTouchHelper.Callback() {

    //用户意图
    enum class UserIntent {
        RIGHT,
        LEFT,
        UNDEFINE
    }

    var delWidth = 0
    var itemWidth = 0

    var userIntent = UNDEFINE

    //是否是第一次手指离开屏幕
    var isFirstTimeReleaseFinger = true

    //手指第一次按下屏幕的时候的dX
    var onTouchDx = 0f

    //滑动屏幕的计数器，用于判断用于意图
    var touchFingerCount = 0L

    //滑动条目的时候，条目已经位移的距离
    var onStartItemPos = 0f

    //过渡滑动用的动画
    private val transAnime by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            //配置插值器
            interpolator = DecelerateInterpolator()
        }
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * 侧滑逻辑
     * 1、不使用此类自带的动画效果，所有动画效果均由手撸
     * 2、状态转换判断逻辑：
     *  if(dX > 0)
     *      判定为右滑
     *          if(距离 > 删除按钮宽度的1/2)
     *              判定为转换为关闭状态，并触发动画
     *          else
     *              判定为转换为起始状态，并触发动画
     *  else
     *      判定为左滑
     *          if(距离 > 删除按钮宽度的1/2)
     *              判定为转换为打开状态，并触发动画
     *          else
     *              判定为转换为起始状态，并触发动画
     */

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

            //记录条目宽度与删除按钮宽度
            itemWidth = width
            delWidth = todo_fl_del.width


            LogUtils.d(
                "RayleighZ",
                "delWidth = $delWidth, dX = $dX， touchCount = $touchFingerCount"
            )

            fun delBtnAnime(process: Float) {
                //删除按钮的动画
                todo_fl_del.apply {
                    val animeProcess = (process * 0.2f + 0.8f).coerceAtMost(1f)
                    pivotX = 0f
                    pivotY = height.toFloat() / 2f
                    scaleX = animeProcess
                    scaleY = animeProcess
                    alpha = animeProcess
                }
            }

            if (isCurrentlyActive) {
                isFirstTimeReleaseFinger = true
                if (touchFingerCount == 0L) {
                    //第一次不做处理，只记录点击位置
                    onStartItemPos = todo_cl_item_main.translationX
                    touchFingerCount++
                    return
                } else if (touchFingerCount == 1L) {
                    userIntent = if (dX > 0) {
                        RIGHT
                    } else {
                        LEFT
                    }
                }
                //判定为可以跟随手指
                if (userIntent == RIGHT) {
                    if (onStartItemPos == 0f){
                        //如果已经位于item的最右侧，就不再允许继续滑动
                        return
                    }
                    //判定为右滑
                    //计算真正的位移距离（dX不大于delWidth为前提）
                    val curMove = dX.coerceAtMost(delWidth.toFloat()).coerceAtLeast(0f)
                    //配置滑动进度和删除按钮动画
                    delBtnAnime((delWidth - curMove) / delWidth)
                    //配置主条目位移动画
                    todo_cl_item_main.translationX = (onStartItemPos + curMove).coerceAtMost(0f)
                } else {
                    if (onStartItemPos == -delWidth.toFloat()){
                        //如果已经位于item的最左侧，就不再允许继续滑动
                        return
                    }
                    //判定为左滑
                    //配置滑动进度和删除按钮动画
                    val curMove = dX.coerceAtLeast(-delWidth.toFloat()).coerceAtMost(0f)
                    delBtnAnime(-curMove / delWidth)
                    //配置主条目位移动画
                    todo_cl_item_main.translationX = curMove
                }
                touchFingerCount++
            } else {
                if (!isFirstTimeReleaseFinger) return
                when (dX) {
                    in -delWidth.toFloat()..-delWidth / 2f -> {
                        //| * |   |
                        //判定为需要展开动画
                        val startX = dX.coerceAtLeast(-delWidth.toFloat())
                        //进度为100，就不需要动画了
                        if (startX == -delWidth.toFloat()) return
                        transAnime.addUpdateListener {
                            //当前剩余的宽度，也是滑动动画需要跨越的距离
                            val remainWidth = delWidth + startX
                            //滑动是自dX开始的，到-delWidth结束
                            todo_cl_item_main.translationX =
                                startX - remainWidth * (it.animatedValue as Float)
                            //按钮动画的进度，真实进度是起始进度+动画进度
                            val delProcess =
                                -startX / delWidth + (1 + startX / delWidth) * (it.animatedValue as Float)
                            delBtnAnime(delProcess)
                        }
                    }
                    in -delWidth / 2f..0f -> {
                        //判定为需要关闭动画
                        val startX = dX.coerceAtMost(0f)
                        //进度为100，就不需要动画了
                        if (startX == 0f) return
                        transAnime.addUpdateListener {
                            //当前剩余的宽度，也是滑动动画需要跨越的距离
                            val remainWidth = -startX
                            //滑动是自-startX开始的，到0结束
                            todo_cl_item_main.translationX =
                                startX + remainWidth * (it.animatedValue as Float)
                            //按钮动画的进度，真实进度是起始进度+动画进度
                            val delProcess =
                                (-startX) / delWidth - (1 + startX / delWidth) * (it.animatedValue as Float)
                            delBtnAnime(delProcess)
                        }
                    }
                    in 0f..delWidth / 2f -> {
                        //判定为需要展开动画
                        val startX = dX.coerceAtLeast(0f)
                        //进度为100，就不需要动画了
                        if (startX == 0f) return
                        transAnime.addUpdateListener {
                            //当前剩余的宽度，也是滑动动画需要跨越的距离
                            val remainWidth = -startX
                            //滑动是自startX开始的，到-delWidth结束
                            todo_cl_item_main.translationX =
                                (-delWidth + startX) + remainWidth * (it.animatedValue as Float)
                            //按钮动画的进度，真实进度是起始进度+动画进度
                            val delProcess =
                                startX / delWidth + (1 - startX / delWidth) * (it.animatedValue as Float)
                            delBtnAnime(delProcess)
                        }
                    }
                    in delWidth / 2f..delWidth.toFloat() -> {
                        //判定为需要关闭动画
                        val startX = dX.coerceAtMost(delWidth.toFloat())
                        //进度为100，就不需要动画了
                        if (startX == delWidth.toFloat()) return
                        transAnime.addUpdateListener {
                            //当前剩余的宽度，也是滑动动画需要跨越的距离
                            val remainWidth = delWidth - startX
                            //滑动是自dX开始的，到0结束
                            todo_cl_item_main.translationX =
                                (-delWidth + startX) + remainWidth * (it.animatedValue as Float)
                            //按钮动画的进度，真实进度是起始进度+动画进度
                            val delProcess =
                                startX / delWidth - (1 - startX / delWidth) * (it.animatedValue as Float)
                            delBtnAnime(delProcess)
                        }
                    }
                }
                transAnime.start()
                isFirstTimeReleaseFinger = false
                touchFingerCount = 0
            }
        }
    }

    //此处配置为：不论滑动的多块或者多远，均不会触发onSwiped
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float =
        Int.MAX_VALUE.toFloat()

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = defaultValue * 100

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //以为我们是侧滑按钮点击删除，所以这里不写任何删除相关逻辑
    }
}