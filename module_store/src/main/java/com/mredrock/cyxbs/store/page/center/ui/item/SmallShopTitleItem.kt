package com.mredrock.cyxbs.store.page.center.ui.item

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.utils.SimpleRvAdapter

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRvAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/8/9
 */
class SmallShopTitleItem(
  titleMap: Map<Int, Pair<String, String>>
) : SimpleRvAdapter.VHItem<SmallShopTitleItem.SmallShopTitleVH, Pair<String, String>>(
  titleMap, R.layout.store_recycler_item_small_shop_title
) {
  
  /**
   * 该方法调用了 [diffRefreshAllItemMap] 用于自动刷新
   *
   * 因为我在 Item 中整合了 DiffUtil 自动刷新, 只有你全部的 Item 都调用了 [diffRefreshAllItemMap],
   * 就会自动启动 DiffUtil
   */
  fun resetData(titleMap: Map<Int, Pair<String, String>>) {
    diffRefreshAllItemMap(
      titleMap,
      isSameName = { oldData, newData ->
        oldData.first == newData.first
      }, isSameData = { oldData, newData ->
        oldData.second == newData.second
      })
  }
  
  class SmallShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.store_tv_small_shop_title)
    val tvOther: TextView = itemView.findViewById(R.id.store_tv_small_shop_other)
  }
  
  override fun getNewViewHolder(itemView: View): SmallShopTitleVH {
    return SmallShopTitleVH(itemView)
  }
  
  override fun onCreate(holder: SmallShopTitleVH, map: Map<Int, Pair<String, String>>) {
  }
  
  override fun onRefactor(holder: SmallShopTitleVH, position: Int, value: Pair<String, String>) {
    holder.tvTitle.text = value.first
    holder.tvOther.text = value.second
  }
}