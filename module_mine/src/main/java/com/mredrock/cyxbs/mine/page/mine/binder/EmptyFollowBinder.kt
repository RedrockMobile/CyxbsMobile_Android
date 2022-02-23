package com.mredrock.cyxbs.mine.page.mine.binder

import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecyclerItemEmptyFocusBinding

/**
 * @class
 * @author YYQF
 * @data 2021/10/8
 * @description
 **/
class EmptyFollowBinder(private val isSelf: Boolean) : BaseDataBinder<MineRecyclerItemEmptyFocusBinding>() {
    override fun layoutId() = R.layout.mine_recycler_item_empty_focus

    override fun onBindViewHolder(binding: MineRecyclerItemEmptyFocusBinding) {
        if (isSelf) {
            binding.mineTvEmptyUser.setText(R.string.mine_person_focus_empty_self)
        } else {
            binding.mineTvEmptyUser.setText(R.string.mine_person_focus_empty)
        }
    }
}