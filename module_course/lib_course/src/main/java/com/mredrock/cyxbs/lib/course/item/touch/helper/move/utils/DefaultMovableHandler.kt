package com.mredrock.cyxbs.lib.course.item.touch.helper.move.utils

import android.view.MotionEvent
import android.view.View
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.move.IMovableItemHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.hypot
import kotlin.math.pow

/**
 * 默认的处理 View 移动的工具类
 *
 * @author 985892345
 * 2023/4/20 17:20
 */
open class DefaultMovableHandler : IMovableItemHandler {
  
  /**
   * 重新设置 [mLastChildLeft]、[mLastChildTop]
   */
  fun resetChildLeftTop(left: Int, top: Int) {
    mLastChildLeft = left
    mLastChildTop = top
  }
  
  /**
   * 重新设置 [mLastMoveX]、[mLastMoveY]
   */
  fun resetLastMoveXY(x: Int, y: Int) {
    mLastMoveX = x
    mLastMoveY = y
  }
  
  protected var mPointerId: Int = MotionEvent.INVALID_POINTER_ID
    private set
  
  protected var mLastMoveX = 0
    private set
  protected var mLastMoveY = 0
    private set
  
  protected var mLastChildLeft = 0
    private set
  protected var mLastChildTop = 0
    private set
  
  protected var mOldTranslationX = 0F
    private set
  protected var mOldTranslationY = 0F
    private set
  protected var mOldTranslationZ = 0F
    private set
  
  override fun onDown(page: ICoursePage, item: ITouchItem, child: View, event: IPointerEvent) {
    mPointerId = event.pointerId
  }
  
  override fun onLongPressed(page: ICoursePage, item: ITouchItem, child: View, x: Int, y: Int) {
    resetLastMoveXY(x, y)
    resetChildLeftTop(child.left, child.top)
    mOldTranslationX = child.translationX
    mOldTranslationY = child.translationY
    mOldTranslationZ = child.translationZ
    child.translationZ = mOldTranslationZ + mPointerId * 0.1F + 2.1F // 让 View 显示在其他 View 上方
  }
  
  override fun onMove(page: ICoursePage, item: ITouchItem, child: View, x: Int, y: Int) {
    child.translationX += x - mLastMoveX + mLastChildLeft - child.left
    child.translationY += y - mLastMoveY + mLastChildTop - child.top
    resetLastMoveXY(x, y)
    resetChildLeftTop(child.left, child.top)
  }
  
  protected var mAnimDx = 0F
    private set
  protected var mAnimDy = 0F
    private set
  protected var mAnimTranslationZ = 0F
    private set
  
  override fun onOverAnimStart(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ) {
    mAnimDx = (mLastChildLeft + child.translationX) - (child.left + mOldTranslationX)
    mAnimDy = (mLastChildTop + child.translationY) - (child.top + mOldTranslationY)
    mAnimTranslationZ = child.translationZ
  }
  
  /**
   * 得到最后移动到新位置或者回到原位置的动画时长
   */
  override fun getMoveAnimatorDuration(): Long {
    // 自己拟合的一条由距离求出时间的函数，感觉比较适合动画效果 :)
    // y = 50 * x^0.25 + 90
    return (hypot(mAnimDx, mAnimDx).pow(0.25F) * 50 + 90).toLong()
  }
  
  override fun onOverAnimUpdate(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    fraction: Float
  ) {
    child.translationX = mOldTranslationX + mAnimDx * (1 - fraction)
    child.translationY = mOldTranslationY + mAnimDy * (1 - fraction)
    child.translationZ = (mAnimTranslationZ - mOldTranslationZ) * (1 - fraction) + mOldTranslationZ
  }
  
  override fun onOverAnimEnd(
    newLocation: LocationUtil.Location?,
    page: ICoursePage,
    item: ITouchItem,
    child: View
  ) {
    child.translationX = mOldTranslationX
    child.translationY = mOldTranslationY
    child.translationZ = mOldTranslationZ
  }
}