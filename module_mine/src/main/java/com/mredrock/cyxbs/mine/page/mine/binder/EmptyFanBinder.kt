package com.mredrock.cyxbs.mine.page.mine.binder

import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFanBinding
import com.mredrock.cyxbs.mine.databinding.MineRecyclerItemEmptyFanBinding

/**
 * @class
 * @author YYQF
 * @data 2021/10/8
 * @description
 **/
class EmptyFanBinder(private val isSelf: Boolean) : BaseDataBinder<MineRecyclerItemEmptyFanBinding>() {
    override fun layoutId() = R.layout.mine_recycler_item_empty_fan

    override fun onBindViewHolder(binding: MineRecyclerItemEmptyFanBinding) {
        if (isSelf){
            binding.mineTvEmptyUser.setText(R.string.mine_person_fan_empty_self)
        }else {
            binding.mineTvEmptyUser.setText(R.string.mine_person_fan_empty)
        }
    }
}