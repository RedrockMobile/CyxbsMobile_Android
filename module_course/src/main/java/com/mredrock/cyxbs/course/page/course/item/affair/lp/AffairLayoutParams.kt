package com.mredrock.cyxbs.course.page.course.item.affair.lp

import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.expose.IWeek
import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.lib.course.internal.affair.BaseAffairLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:44
 */
class AffairLayoutParams(data: AffairData) : BaseAffairLayoutParams(data), IRank, IWeek by data {
  
  override val rank: Int
    get() = 2
  
  override fun compareTo(other: NetLayoutParams): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
}