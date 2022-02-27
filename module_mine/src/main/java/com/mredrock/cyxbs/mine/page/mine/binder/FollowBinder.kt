package com.mredrock.cyxbs.mine.page.mine.binder

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineRecycleItemFollowBinding
import com.mredrock.cyxbs.mine.network.model.Fan

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FollowBinder(
    val follower: Fan,
    private val isSelf: Boolean,
    private var onFocusClick: ((view: View, user: Fan) -> Unit)? = null,
    private var onAvatarClick: ((redid: String) -> Unit)? = null
) : BaseDataBinder<MineRecycleItemFollowBinding>() {

    override val itemId
        get() = follower.redid

    override fun layoutId() = R.layout.mine_recycle_item_follow

    override fun onBindViewHolder(binding: MineRecycleItemFollowBinding) {
        binding.apply {
            mineFollowItemTvNickname.text = follower.nickname
            mineFollowItemTvIntroduction.text = follower.introduction

            Glide.with(root.context)
                .load(follower.avatar)
                .placeholder(R.drawable.common_default_avatar)
                .into(mineFollowItemIvAvatar)

            if (!isSelf) {
                mineFollowItemTvFocus.gone()
            } else {
                mineFollowItemTvFocus.background = ContextCompat
                    .getDrawable(root.context, R.drawable.mine_shape_tv_focused)

                if (follower.isFocus) {
                    mineFollowItemTvFocus.text = "互相关注"
                } else {
                    mineFollowItemTvFocus.text = "已关注"
                }

                mineFollowItemTvFocus.setOnClickListener {
                    onFocusClick?.invoke(it, follower)
                }

                mineFollowItemIvAvatar.setOnClickListener {
                    onAvatarClick?.invoke(follower.redid)
                }
            }
        }
    }

    override fun areContentsTheSame(binder: BaseDataBinder<*>): Boolean {
        return if (binder !is FollowBinder){
            false
        }else {
            follower == binder.follower
        }
    }
}