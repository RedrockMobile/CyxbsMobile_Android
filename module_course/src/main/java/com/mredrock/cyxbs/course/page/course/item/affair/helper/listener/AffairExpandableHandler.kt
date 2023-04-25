package com.mredrock.cyxbs.course.page.course.item.affair.helper.listener

import android.view.View
import com.mredrock.cyxbs.course.page.course.item.affair.AffairItem
import com.mredrock.cyxbs.course.page.course.item.affair.IAffairManager
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.utils.DefaultExpandableHandler
import com.mredrock.cyxbs.lib.course.item.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.course.item.helper.expand.ISingleSideExpandable
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.helper.expand.ExpandableItemHelper
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 针对于 [AffairItem] 的 [ExpandableItemHelper] 的扩展逻辑处理类
 *
 * @author 985892345
 * 2023/4/21 21:25
 */
class AffairExpandableHandler(
  private val iAffairManager: IAffairManager
) : DefaultExpandableHandler() {
  
  private val mListener =
    object : ISingleSideExpandable.OnAnimListener, ISingleSideExpandable.OnRowChangedListener {
      
      private var mIsInAnim = false
      
      // 因为动画是延后的，所以需要单独保存一份 mPage 变量
      private var mPageTemp: ICoursePage? = null
      
      override fun onAnimStart(
        side: ISingleSideExpandable,
        course: ICourseViewGroup,
        item: IItem,
        child: View
      ) {
        mIsInAnim = true
        mPageTemp = mPage
      }
      
      override fun onAnimEnd(
        side: ISingleSideExpandable,
        course: ICourseViewGroup,
        item: IItem,
        child: View
      ) {
        side.removeAnimListener(this)
        if (item is IOverlapItem) {
          mPageTemp!!.refreshOverlap()
        }
        mIsInAnim = false
        mPageTemp = null
      }
      
      override fun onChanged(
        course: ICourseViewGroup,
        item: IItem,
        child: View,
        oldTopRow: Int,
        oldBottomRow: Int,
        newTopRow: Int,
        newBottomRow: Int
      ) {
        if (item is IOverlapItem && !mIsInAnim) {
          // 不在动画的期间才刷新，并且取消 item 的动画
          mPage!!.refreshOverlap(listOf(item))
        }
      }
    }
  
  private var mPage: ICoursePage? = null
  
  override fun onDown(page: ICoursePage, item: ITouchItem, child: View, event: IPointerEvent) {
    super.onDown(page, item, child, event)
    val side = mSideExpandable!!
    side.addAnimListener(mListener)
    side.addOnRowChangedListener(mListener)
    mPage = page
  }
  
  override fun onEventEnd(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent,
    isInLongPress: Boolean?,
    isCancel: Boolean
  ) {
    val side = mSideExpandable!!
    super.onEventEnd(page, item, child, event, isInLongPress, isCancel)
    side.removeOnRowChangedListener(mListener)
    mPage = null
  }
  
  override fun getIsMoveValid(
    page: ICoursePage,
    item: ITouchItem,
    child: View,
    event: IPointerEvent,
    isInLongPress: Boolean?,
    isCancel: Boolean
  ): Boolean {
    item as AffairItem
    val isSingleAffair =  iAffairManager.isSingleAffair(item.data)
    if (!isSingleAffair) {
      toast("暂不支持扩展重复事务")
    }
    return isSingleAffair
  }
}