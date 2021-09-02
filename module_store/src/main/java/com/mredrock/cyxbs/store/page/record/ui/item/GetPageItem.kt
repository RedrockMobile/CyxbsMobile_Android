package com.mredrock.cyxbs.store.page.record.ui.item

import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRvAdapter
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemRecordGetBinding
import com.mredrock.cyxbs.store.utils.Date

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/29
 */
class GetPageItem(
    list: List<StampGetRecord>,
    startPosition: Int
) : SimpleRvAdapter.DBItem<StoreRecyclerItemRecordGetBinding, StampGetRecord>(
    list,
    startPosition,
    R.layout.store_recycler_item_record_get
) {

    override fun onCreate(
        binding: StoreRecyclerItemRecordGetBinding,
        holder: SimpleRvAdapter.BindingVH,
        map: Map<Int, StampGetRecord>
    ) {
    }

    override fun onRefactor(
        binding: StoreRecyclerItemRecordGetBinding,
        holder: SimpleRvAdapter.BindingVH,
        position: Int,
        value: StampGetRecord
    ) {
        binding.data = value
        //单独处理时间
        binding.storeItemGetRecordTvDate.text = Date.getTime(value.date)
    }
}