package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.item.affair.AffairItem
import com.mredrock.cyxbs.course.page.course.item.affair.IMovableAffairManager
import com.mredrock.cyxbs.course.page.course.utils.container.base.ItemPoolController
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage

/**
 * 代理添加 [AffairItem]
 *
 * - 提供差分刷新方法 [diffRefresh]
 * - 实现了 [AffairItem] 的回收池用于复用
 * - 监听了使用其他方式删除的 [AffairItem]，用于解决差分旧数据不同步的问题
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 18:24
 */
class AffairContainerProxy(
  val page: ICoursePage,
  val iMovableAffairManager: IMovableAffairManager
) : ItemPoolController<AffairItem, AffairData>(page) {
  
  override fun areItemsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
    return AffairData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: AffairData, newItem: AffairData): Boolean {
    return AffairData.areContentsTheSame(oldItem, newItem)
  }
  
  override val itemClass: Class<AffairItem>
    get() = AffairItem::class.java
  
  override fun onChanged(oldData: AffairData, newData: AffairData) {
    val item = mOldDataMap[oldData]
    page.changeOverlap(item, false) // 取消重叠
    super.onChanged(oldData, newData)
    page.changeOverlap(item, true) // 恢复重叠
  }
  
  override fun addItem(item: AffairItem) {
    page.addAffair(item)
  }
  
  override fun removeItem(item: AffairItem) {
    page.removeAffair(item)
  }
  
  override fun newItem(data: AffairData): AffairItem {
    return AffairItem(data, iMovableAffairManager)
  }
  
  override fun onDiffRefreshOver(oldData: List<AffairData>, newData: List<AffairData>) {
    super.onDiffRefreshOver(oldData, newData)
    page.refreshOverlap() // 刷新重叠
  }
}