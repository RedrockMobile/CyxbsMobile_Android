package com.mredrock.cyxbs.mine.page.mine.binder

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFanBinding
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFollowBinding
import com.mredrock.cyxbs.mine.network.model.Fan

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FollowBinder(private val fan: Fan,
                   private var onFocusClick:((view: View, user:Fan)->Unit)? = null)
    : BaseDataBinder<MineRecycleItemFollowBinding>() {

    override val itemId
        get() = fan.redid

    override fun layoutId() = R.layout.mine_recycle_item_follow

    override fun onBindViewHolder(binding: MineRecycleItemFollowBinding) {
        binding.apply {
            mineFollowItemTvNickname.text = fan.nickname
            mineFollowItemTvIntroduction.text = fan.introduction

            Glide.with(root.context)
                .load(fan.avatar)
                .placeholder(R.drawable.common_default_avatar)
                .into(mineFollowItemIvAvatar)

            mineFollowItemTvFocus.background = ContextCompat
                .getDrawable(root.context, R.drawable.mine_shape_tv_focused)

            if (fan.isFocus) {
                mineFollowItemTvFocus.text = "互相关注"
            } else {
                mineFollowItemTvFocus.text = "已关注"
            }

            mineFollowItemTvFocus.setOnClickListener {
                onFocusClick?.invoke(it,fan)
            }
        }
    }
}