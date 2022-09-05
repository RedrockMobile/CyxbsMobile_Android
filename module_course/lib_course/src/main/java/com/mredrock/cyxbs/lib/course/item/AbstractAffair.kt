package com.mredrock.cyxbs.lib.course.item

import com.mredrock.cyxbs.lib.course.fragment.item.IAffair
import com.mredrock.cyxbs.lib.course.internal.affair.BaseAffairLayoutParams
import com.mredrock.cyxbs.lib.course.internal.affair.IAffairData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 17:28
 */
abstract class AbstractAffair(
  private val data: IAffairData
) : AbstractOverlapItem(data), IAffair {
  abstract override val lp: BaseAffairLayoutParams
}