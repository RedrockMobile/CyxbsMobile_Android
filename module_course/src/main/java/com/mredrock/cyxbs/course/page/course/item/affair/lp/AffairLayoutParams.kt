package com.mredrock.cyxbs.course.page.course.item.affair.lp

import com.mredrock.cyxbs.course.page.course.item.IRank
import com.mredrock.cyxbs.lib.course.internal.affair.BaseAffairLayoutParams
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairData
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 16:44
 */
class AffairLayoutParams(data: IAffairData) : BaseAffairLayoutParams(data), IRank {
  
  override val rank: Int
    get() = 3
  
  override fun compareTo(other: NetLayoutParams): Int {
    return if (other is IRank) compareToInternal(other) else 1
  }
}