package com.mredrock.cyxbs.mine.page.mine.binder

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFanBinding
import com.mredrock.cyxbs.mine.network.model.Fan

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanBinder(private val fan: Fan,
                private val isSelf: Boolean,
                private var onFocusClick:((view: View, user:Fan)->Unit)? = null)
    : BaseDataBinder<MineRecycleItemFanBinding>() {

    override val itemId
        get() = fan.redid

    override fun layoutId() = R.layout.mine_recycle_item_fan

    override fun onBindViewHolder(binding: MineRecycleItemFanBinding) {
        binding.apply {
            mineFanItemTvNickname.text = fan.nickname
            mineFanItemTvIntroduction.text = fan.introduction

            Glide.with(root.context)
                .load(fan.avatar)
                .placeholder(R.drawable.common_default_avatar)
                .into(mineFanItemIvAvatar)

            if (!isSelf){
                mineFanItemTvFocus.gone()
            } else {
                if (fan.isFocus) {
                    mineFanItemTvFocus.background = ContextCompat
                        .getDrawable(root.context, R.drawable.mine_shape_tv_focused)
                    mineFanItemTvFocus.text = "互相关注"
                } else {
                    mineFanItemTvFocus.background = ContextCompat
                        .getDrawable(root.context, R.drawable.mine_shape_tv_unfocus)
                    mineFanItemTvFocus.text = "+关注"
                }

                mineFanItemTvFocus.setOnClickListener {
                    onFocusClick?.invoke(it,fan)
                }
            }


        }
    }
}