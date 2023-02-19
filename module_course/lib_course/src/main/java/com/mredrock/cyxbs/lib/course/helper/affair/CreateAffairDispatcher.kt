package com.mredrock.cyxbs.lib.course.helper.affair

import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.fragment.course.expose.fold.FoldState
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.helper.ScrollTouchHandler
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ICreateAffairConfig
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.helper.base.AbstractLongPressDispatcher
import com.mredrock.cyxbs.lib.course.helper.base.ILongPressTouchHandler
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*
import kotlin.math.abs

/**
 * 长按生成事务的事件分发者
 *
 * ## 注意
 * - 如果你需要添加一些约束性的交互 (比如某时候不允许生成 View)，建议写在 [iCreateAffair] 中
 *
 * @author 985892345
 * @date 2022/9/19 14:48
 */
class CreateAffairDispatcher(
  val page: ICoursePage,
  val iCreateAffair: ICreateAffairConfig = ICreateAffairConfig.Default, // 如果你需要与外界进行交互，建议写在这个里面
) : AbstractLongPressDispatcher() {
  
  /**
   * 设置点击 [ITouchAffairItem] 的点击监听
   */
  fun setOnClickListener(onClick: ITouchAffairItem.() -> Unit) {
    mOnClickListener = onClick
  }
  
  /**
   * 长按手指移动的回调
   */
  fun addTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.add(callback)
  }
  
  fun removeTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.remove(callback)
  }
  
  // 暂时保存 ITouchAffairItem 用于取消显示
  private val mTouchAffairViews = arrayListOf<ITouchAffairItem>()
  private var mIsAllowIntercept = true
  private var mOnClickListener: (ITouchAffairItem.() -> Unit)? = null
  
  override fun handleEvent(event: IPointerEvent, view: ViewGroup): ILongPressTouchHandler? {
    if (mIsAllowIntercept) {
      if (iCreateAffair.isValidDown(page, event)) {
        return getHandler(event)
      }
    }
    return null
  }
  
  override fun onCancelLongPress(
    event: IPointerEvent,
    view: ViewGroup,
    initialX: Int,
    initialY: Int
  ): IPointerTouchHandler? {
    return when (event.action) {
      UP -> {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
        if (abs(x - initialX) <= touchSlop && abs(y - initialY) <= touchSlop) {
          // 这里说明移动的距离小于 touchSlop，但还是得把点击的事务给绘制上，但是只有一格
          val item = initializeTouchAffairItem(event)
          val initialRow = page.course.getRow(y)
          val initialColumn = page.course.getColumn(x)
          item.show(initialRow, initialRow, initialColumn)
        }
        null
      }
      MOVE -> ScrollTouchHandler // MOVE 中被取消长按说明手指移动距离过大
      else -> null
    }
  }
  
  override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    super.onDispatchTouchEvent(event, view)
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mIsAllowIntercept = true // 还原
        val item = page.course.findItemUnderByXY(event.x.toInt(), event.y.toInt())
        if (item !is ITouchAffairItem) {
          // DOWN 事件时如果点击的不是 TouchAffair，则取消已经显示的 TouchAffair
          mTouchAffairViews.forEach {
            if (it.isInShow()) {
              mIsAllowIntercept = false // 如果存在正在显示的 TouchAffair，则这次 DOWN 事件不拦截
              it.cancelShow() // 取消正在显示的 TouchAffair
            }
          }
          mTouchAffairViews.clear()
        }
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        mIsAllowIntercept = true // 但第二跟手指触摸时就允许拦截 (如果没有被其他拦截的话)
      }
    }
  }
  
  /**
   * 得到一个 [CreateAffairHandler]
   */
  private fun getHandler(event: IPointerEvent): CreateAffairHandler {
    val touchAffairItem = initializeTouchAffairItem(event)
    return CreateAffairHandler(page.course, mTouchCallbackImpl, iCreateAffair, touchAffairItem)
  }
  
  /**
   * 初始化 [ITouchAffairItem]
   */
  private fun initializeTouchAffairItem(event: IPointerEvent): ITouchAffairItem {
    val item = iCreateAffair.createTouchAffairItem(page.course, event)
    mTouchAffairViews.add(item)
    // 这里统一给 TouchAffairItem 设置点击事件
    item.setOnClickListener { mOnClickListener?.invoke(item) }
    return item
  }
  
  /**
   * [ITouchCallback] 的实现类
   */
  private val mTouchCallbackImpl = object : ITouchCallback {
    
    val mTouchCallbacks = arrayListOf<ITouchCallback>()
    
    override fun onLongPressStart(pointerId: Int, initialRow: Int, initialColumn: Int) {
      mTouchCallbacks.forEachReversed { it.onLongPressStart(pointerId, initialRow, initialColumn) }
    }
    
    override fun onTouchMove(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      touchRow: Int,
      topRow: Int,
      bottomRow: Int,
    ) {
      unfoldNoonOrDuskIfNecessary(initialRow, touchRow)
      mTouchCallbacks.forEachReversed {
        it.onTouchMove(
          pointerId,
          initialRow,
          initialColumn,
          touchRow,
          topRow,
          bottomRow
        )
      }
    }
    
    override fun onTouchEnd(
      pointerId: Int,
      initialRow: Int,
      initialColumn: Int,
      touchRow: Int,
      topRow: Int,
      bottomRow: Int,
    ) {
      mTouchCallbacks.forEachReversed {
        it.onTouchEnd(
          pointerId,
          initialRow,
          initialColumn,
          touchRow,
          topRow,
          bottomRow
        )
      }
    }
    
    /**
     * 判断当前滑动中是否需要自动展开中午或者傍晚时间段
     * @param initialRow 最开始触摸的行数
     * @param touchRow 当前触摸的行数
     */
    private fun unfoldNoonOrDuskIfNecessary(initialRow: Int, touchRow: Int) {
      when (page.getNoonRowState()) {
        FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
          if (page.compareNoonPeriodByRow(initialRow) * page.compareNoonPeriodByRow(touchRow) <= 0) {
            page.unfoldNoon()
          }
        }
        else -> {}
      }
      when (page.getDuskRowState()) {
        FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
          if (page.compareDuskPeriodByRow(initialRow) * page.compareDuskPeriodByRow(touchRow) <= 0) {
            page.unfoldDusk()
          }
        }
        else -> {}
      }
    }
  }
}