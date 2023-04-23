package com.mredrock.cyxbs.lib.course.item.helper.expand

import android.animation.ValueAnimator
import android.view.Gravity
import android.view.View
import androidx.core.animation.doOnEnd
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.utils.forEachReversed
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * .
 *
 * @author 985892345
 * 2023/4/19 21:24
 */
class DoubleSideExpandableHelper : IDoubleSideExpandable {
  
  private var mIsRunning = false
  
  private var mTopMargin = 0
  private var mBottomMargin = 0
  private var mGravity = Gravity.NO_GRAVITY
  
  private var mTopEndValueAnimator: ValueAnimator? = null
  private var mBottomEndValueAnimator: ValueAnimator? = null
  
  private val mOnRowChangedListeners = arrayListOf<ISingleSideExpandable.OnRowChangedListener>()
  private val mAnimListeners = arrayListOf<ISingleSideExpandable.OnAnimListener>()
  
  override fun isMoveStart(): Boolean {
    return mIsRunning
  }
  
  override fun onMoveStart(course: ICourseViewGroup, item: IItem, child: View) {
    check(!mIsRunning) { "不支持并发修改" }
    mIsRunning = true
    mTopEndValueAnimator?.end()
    mBottomEndValueAnimator?.end()
    mTopMargin = item.lp.topMargin
    mBottomMargin = item.lp.bottomMargin
    mGravity = item.lp.gravity
  }
  
  override fun onDoubleMove(course: ICourseViewGroup, item: IItem, child: View, y1: Int, y2: Int) {
    check(mIsRunning) { "未调用 onMoveStart 方法" }
    val minY = min(y1, y2)
    val maxY = max(y1, y2)
    val row1 = course.getRow(minY)
    val row2 = course.getRow(maxY)
    val oldStartRow: Int
    val oldEndRow: Int
    if (row1 == row2) {
      item.lp.apply {
        oldStartRow = startRow
        oldEndRow = endRow
        startRow = row1
        endRow = row2
        topMargin = mTopMargin
        bottomMargin = mBottomMargin
      }
    } else {
      item.lp.apply {
        oldStartRow = startRow
        oldEndRow = endRow
        startRow = row1
        endRow = row2
        topMargin = minY - (course.getRowsHeight(0, row1 - 1) + course.getPaddingTop())
        bottomMargin = course.getRowsHeight(0, row2) + course.getPaddingTop() - maxY
      }
    }
    child.layoutParams = item.lp // 刷新布局
    forEachOnRowChangedListener(course, item, child, oldStartRow, oldEndRow)
  }
  
  override fun onSingleMove(course: ICourseViewGroup, item: IItem, child: View, row: Int, y: Int) {
    check(mIsRunning) { "未调用 onMoveStart 方法" }
    val otherRow = course.getRow(y)
    val oldStartRow: Int
    val oldEndRow: Int
    if (otherRow == row) {
      item.lp.apply {
        oldStartRow = startRow
        oldEndRow = endRow
        startRow = row
        endRow = row
        topMargin = mTopMargin
        bottomMargin = mBottomMargin
        gravity = mGravity
      }
    } else if (otherRow > row) {
      item.lp.apply {
        oldStartRow = startRow
        oldEndRow = endRow
        startRow = row
        endRow = otherRow
        topMargin = mTopMargin
        bottomMargin = course.getRowsHeight(0, otherRow) + course.getPaddingTop() - y
        gravity = Gravity.TOP
      }
    } else {
      item.lp.apply {
        oldStartRow = startRow
        oldEndRow = endRow
        startRow = otherRow
        endRow = row
        topMargin = y - (course.getRowsHeight(0, otherRow - 1) + course.getPaddingTop())
        bottomMargin = mBottomMargin
        gravity = Gravity.BOTTOM
      }
    }
    child.layoutParams = item.lp // 刷新布局
    forEachOnRowChangedListener(course, item, child, oldStartRow, oldEndRow)
  }
  
  override fun onMoveEnd(course: ICourseViewGroup, item: IItem, child: View) {
    check(mIsRunning) { "未调用 onMoveStart 方法" }
    val oldStartRow = item.lp.startRow
    val oldEndRow = item.lp.endRow
    if (runTopMarginAnim(course, item, child) or runBottomMarginAnim(course, item, child)) {
      // 使用 or 保证两个都会执行
      forEachAnimStartListener(course, item, child)
    }
    forEachOnRowChangedListener(course, item, child, oldStartRow, oldEndRow)
    mIsRunning = false
  }
  
  // 上边界动画
  private fun runTopMarginAnim(course: ICourseViewGroup, item: IItem, child: View): Boolean {
    var nowTopMargin = item.lp.topMargin
    if (nowTopMargin != mTopMargin) {
      val finaTopMargin = isFinalTopRow(course, item)
      if (finaTopMargin != null) {
        item.lp.startRow += 1
        nowTopMargin = finaTopMargin
      }
      mTopEndValueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
        addUpdateListener {
          val fraction = it.animatedFraction
          item.lp.topMargin = (mTopMargin + (nowTopMargin - mTopMargin) * (1 - fraction)).toInt()
          child.layoutParams = item.lp
        }
        doOnEnd {
          mTopEndValueAnimator = null
          doIfOnAllAnimEnd(course, item, child)
        }
        duration = getAnimDuration(nowTopMargin - mTopMargin)
        start()
      }
    }
    return mTopEndValueAnimator != null
  }
  
  // 下边界动画
  private fun runBottomMarginAnim(course: ICourseViewGroup, item: IItem, child: View): Boolean {
    var nowBottomMargin = item.lp.bottomMargin
    if (nowBottomMargin != mBottomMargin) {
      val finalBottomMargin = isFinalBottomRow(course, item)
      if (finalBottomMargin != null) {
        item.lp.endRow -= 1
        nowBottomMargin = finalBottomMargin
      }
      mBottomEndValueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
        addUpdateListener {
          val fraction = it.animatedFraction
          item.lp.bottomMargin =
            (mBottomMargin + (nowBottomMargin - mBottomMargin) * (1 - fraction)).toInt()
          child.layoutParams = item.lp
        }
        doOnEnd {
          mBottomEndValueAnimator = null
          doIfOnAllAnimEnd(course, item, child)
        }
        duration = getAnimDuration(nowBottomMargin - mBottomMargin)
        start()
      }
    }
    return mBottomEndValueAnimator != null
  }
  
  
  
  ////////////////////////////////////
  //
  //          不怎么改动的区域
  //
  ////////////////////////////////////
  
  // 根据 topMargin 判断是否是最终显示的行，如果不是的话将返回达到显示效果的 负 margin 值，但你需要手动 item.lp.startRow += 1
  private fun isFinalTopRow(course: ICourseViewGroup, item: IItem): Int? {
    val topRowHeight = course.getRowsHeight(item.lp.startRow, item.lp.startRow)
    if (item.lp.topMargin > topRowHeight / 2) {
      return item.lp.topMargin - topRowHeight
    }
    return null
  }
  
  // 根据 bottomMargin 判断是否是最终显示的行，如果不是的话将返回达到显示效果的 负 margin 值，但你需要手动 item.lp.endRow -= 1
  private fun isFinalBottomRow(course: ICourseViewGroup, item: IItem): Int? {
    val bottomRowHeight = course.getRowsHeight(item.lp.endRow, item.lp.endRow)
    if (item.lp.bottomMargin > bottomRowHeight / 2) {
      return item.lp.bottomMargin - bottomRowHeight
    }
    return null
  }
  
  private fun getAnimDuration(diff: Int): Long {
    return (abs(diff).toFloat().pow(0.25F) * 20 + 40).toLong()
  }
  
  private fun doIfOnAllAnimEnd(course: ICourseViewGroup, item: IItem, child: View) {
    if (mTopEndValueAnimator == null && mBottomEndValueAnimator == null) {
      item.lp.gravity = mGravity
      forEachAnimEndListener(course, item, child)
    }
  }
  
  override fun addOnRowChangedListener(l: ISingleSideExpandable.OnRowChangedListener) {
    mOnRowChangedListeners.add(l)
  }
  
  override fun removeOnRowChangedListener(l: ISingleSideExpandable.OnRowChangedListener) {
    mOnRowChangedListeners.remove(l)
  }
  
  private fun forEachOnRowChangedListener(
    course: ICourseViewGroup,
    item: IItem,
    child: View,
    oldStartRow: Int,
    oldEndRow: Int
  ) {
    if (oldStartRow != item.lp.startRow || oldEndRow != item.lp.endRow) {
      mOnRowChangedListeners.forEachReversed {
        it.onChanged(course, item, child, oldStartRow, oldEndRow, item.lp.startRow, item.lp.endRow)
      }
    }
  }
  
  override fun addAnimListener(l: ISingleSideExpandable.OnAnimListener) {
    mAnimListeners.add(l)
  }
  
  override fun removeAnimListener(l: ISingleSideExpandable.OnAnimListener) {
    mAnimListeners.remove(l)
  }
  
  private fun forEachAnimStartListener(course: ICourseViewGroup, item: IItem, child: View) {
    mAnimListeners.forEachReversed {
      it.onAnimStart(this, course, item, child)
    }
  }
  
  private fun forEachAnimEndListener(course: ICourseViewGroup, item: IItem, child: View) {
    mAnimListeners.forEachReversed {
      it.onAnimEnd(this, course, item, child)
    }
  }
}