package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.item.affair.Affair
import com.mredrock.cyxbs.course.page.course.utils.container.base.CourseContainerProxy
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * 代理添加 [Affair]
 *
 * - 提供差分刷新方法 [diffRefresh]
 * - 实现了 [Affair] 的回收池用于复用
 * - 监听了使用其他方式删除的 [Affair]，用于解决差分旧数据不同步的问题
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 18:24
 */
class AffairContainerProxy(
  val container: ICourseContainer,
) : CourseContainerProxy<Affair, AffairData>(container) {
  
  override fun areItemsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
    return AffairData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
    return AffairData.areContentsTheSame(oldItem, newItem)
  }
  
  override fun checkItem(item: IItem): Boolean {
    return item is Affair
  }
  
  override fun addItem(item: Affair) {
    container.addAffair(item)
  }
  
  override fun removeItem(item: Affair) {
    container.removeAffair(item)
  }
  
  override fun newItem(data: AffairData): Affair {
    return Affair(data)
  }
}