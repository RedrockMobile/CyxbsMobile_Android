package com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap

import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * 支持重叠的 item
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 17:30
 */
interface IOverlapItem : IItem, Comparable<IOverlapItem> {
  val overlap: IOverlap
}