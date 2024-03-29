package com.mredrock.cyxbs.course.page.course.item.affair.lp

import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.ISingleDayRank
import com.mredrock.cyxbs.course.page.course.utils.container.base.IDataOwner
import com.mredrock.cyxbs.lib.course.item.affair.BaseAffairLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:44
 */
class AffairLayoutParams(
  override var data: AffairData
) : BaseAffairLayoutParams(data), ISingleDayRank, IDataOwner<AffairData> {
  
  override val rank: Int
    get() = 2
  
  override fun compareTo(other: NetLayoutParams): Int {
    return if (other is ISingleDayRank) compareToInternal(other) else 1
  }
  
  override val week: Int
    get() = data.week
  
  override fun setNewData(newData: AffairData) {
    data = newData
    changeSingleDay(newData)
  }
}