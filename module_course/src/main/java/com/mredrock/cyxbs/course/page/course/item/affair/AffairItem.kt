package com.mredrock.cyxbs.course.page.course.item.affair

import android.content.Context
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.ICourseItemData
import com.mredrock.cyxbs.course.page.course.item.BaseItem
import com.mredrock.cyxbs.course.page.course.item.affair.lp.AffairLayoutParams
import com.mredrock.cyxbs.course.page.course.item.affair.helper.AffairMovableHelper
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.course.page.course.utils.container.base.IRecycleItem
import com.mredrock.cyxbs.lib.course.item.affair.IAffairItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItem
import com.mredrock.cyxbs.lib.course.item.touch.ITouchItemHelper

/**
 * 显示事务的 Item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:43
 */
class AffairItem(
  private var affairData: AffairData,
  private val iMovableAffairManager: IMovableAffairManager
) : BaseItem<AffairView>(),
  IDataOwner<AffairData>,
  IAffairItem,
  IRecycleItem,
  ITouchItem {
  
  override fun setNewData(newData: AffairData) {
    getChildIterable().forEach {
      if (it is AffairView) {
        it.setNewData(newData)
      }
    }
    lp.setNewData(newData)
    affairData = newData
    getRootView()?.requestLayout()
    refreshShowOverlapTag()
  }
  
  override fun createView(context: Context): AffairView {
    return AffairView(context, affairData)
  }
  
  override val isHomeCourseItem: Boolean
    get() = true
  
  override fun onRecycle(): Boolean {
    return true
  }
  
  override fun onReuse(): Boolean {
    val view = getNetView() ?: return true
    return view.run {
      parent == null && !isAttachedToWindow
    }
  }
  
  override val rank: Int
    get() = lp.rank
  
  override val iCourseItemData: ICourseItemData
    get() = affairData
  
  override val lp: AffairLayoutParams = AffairLayoutParams(affairData)
  
  override val week: Int
    get() = affairData.week
  
  override val data: AffairData
    get() = affairData
  
  override fun initializeTouchItemHelper(): List<ITouchItemHelper> {
    return super.initializeTouchItemHelper() + listOf(
      AffairMovableHelper(this, iMovableAffairManager)
    )
  }
}
