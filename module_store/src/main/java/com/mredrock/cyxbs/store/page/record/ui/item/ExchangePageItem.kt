package com.mredrock.cyxbs.store.page.record.ui.item

import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.common.utils.SimpleRvAdapter
import com.mredrock.cyxbs.store.bean.ExchangeRecord
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemRecordExchangeBinding
import com.mredrock.cyxbs.store.page.record.ui.activity.ExchangeDetailActivity
import com.mredrock.cyxbs.store.utils.Date

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/29
 */
class ExchangePageItem(
    list: List<ExchangeRecord>,
    startPosition: Int
) : SimpleRvAdapter.DBItem<StoreRecyclerItemRecordExchangeBinding, ExchangeRecord>(
    list,
    startPosition,
    R.layout.store_recycler_item_record_exchange
) {

    /**
     * 用于传入新数据使用差分刷新
     */
    fun refresh(list: List<ExchangeRecord>, startPosition: Int) {
        diffRefreshAllItemMap(list, startPosition,
            isSameName = { oldData, newData ->
                oldData.date == newData.date
            },
            isSameData = { oldData, newData ->
                oldData == newData
            }
        )
    }

    override fun onCreate(
        binding: StoreRecyclerItemRecordExchangeBinding,
        holder: SimpleRvAdapter.BindingVH,
        map: Map<Int, ExchangeRecord>
    ) {
        binding.storeLayoutExchangeRecord.setOnSingleClickListener {
            ExchangeDetailActivity.activityStart(
                holder.itemView.context,
                map.getValue(holder.layoutPosition)
            )
        }
    }

    override fun onRefactor(
        binding: StoreRecyclerItemRecordExchangeBinding,
        holder: SimpleRvAdapter.BindingVH,
        position: Int,
        value: ExchangeRecord
    ) {
        //绑定数据
        binding.data = value
        //单独处理时间
        binding.storeItemExchangeRecordTvDate.text = Date.getTime(value.date)

        // 先默认隐藏 如果未领取就加载动画
        binding.storeBtnProductReceiveTips.alpha = 0F
        if (!value.isReceived) {
            binding.storeBtnProductReceiveTips.animate()
                .alpha(1F)
                .setDuration(600)
                .start()
        }
    }

    override fun onViewRecycled(
        binding: StoreRecyclerItemRecordExchangeBinding,
        holder: SimpleRvAdapter.BindingVH
    ) {
        // 当 item 被回收时调用, 取消动画效果, 防止因复用而出现闪动
        binding.storeBtnProductReceiveTips.animate().cancel()
        binding.storeBtnProductReceiveTips.alpha = 0F
    }
}