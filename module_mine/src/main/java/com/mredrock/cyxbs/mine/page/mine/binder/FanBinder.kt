package com.mredrock.cyxbs.mine.page.mine.binder

import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFanBinding

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanBinder() : BaseDataBinder<MineRecycleItemFanBinding>() {
    override val itemId = ""

    override fun layoutId(): Int {
        return R.layout.mine_recycle_item_fan
    }

    override fun onBindViewHolder(binding: MineRecycleItemFanBinding) {
        super.onBindViewHolder(binding)
    }

    override fun areContentTheSame(): Boolean {
        return false
    }
}