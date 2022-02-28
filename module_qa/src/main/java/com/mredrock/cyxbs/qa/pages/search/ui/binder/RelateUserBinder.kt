package com.mredrock.cyxbs.qa.pages.search.ui.binder

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.UserBrief
import com.mredrock.cyxbs.qa.databinding.QaRecyclerItemSearchUserBinding

/**
 * @class
 * @author YYQF
 * @data 2021/9/26
 * @description
 **/
class RelateUserBinder(
    val user: UserBrief,
    val itemIsSelf: Boolean,
    //关注按钮点击事件
    private var onFocusClick: ((view: View, user: UserBrief) -> Unit)? = null,
    private var onAvatarClick: ((redid: String) -> Unit)? = null
) : BaseDataBinder<QaRecyclerItemSearchUserBinding>() {

    override fun layoutId() = R.layout.qa_recycler_item_search_user

    override val itemId: String
        get() = user.redid

    override fun onBindViewHolder(binding: QaRecyclerItemSearchUserBinding) {
        super.onBindViewHolder(binding)
        binding.apply {
            Glide.with(root.context)
                .load(user.avatar)
                .placeholder(R.drawable.common_default_avatar)
                .into(qaIvSearchUserAvatar)
            qaTvSearchUserNickname.text = user.nickname
            qaTvSearchUserIntroduction.text = user.introduction

            if (itemIsSelf){
                qaTvSearchUserFocus.gone()
            }else {
                if (user.isFocus) {
                    with(qaTvSearchUserFocus) {
                        text = if (user.isBeFocused) "互相关注" else "已关注"
                        setTextColor(ContextCompat.getColor(context, R.color.qa_tv_focus))
                        background = ContextCompat
                            .getDrawable(root.context, R.drawable.qa_shape_tv_search_focused)
                    }

                } else {
                    with(qaTvSearchUserFocus) {
                        text = "+关注"
                        setTextColor(ContextCompat.getColor(context, R.color.qa_tv_unfocus))
                        background = ContextCompat
                            .getDrawable(root.context, R.drawable.qa_shape_tv_search_unfocused)
                    }
                }

                qaTvSearchUserFocus.setOnClickListener {
                    onFocusClick?.invoke(it, user)
                }

                qaIvSearchUserAvatar.setOnClickListener {
                    onAvatarClick?.invoke(user.redid)
                }
            }
        }
    }

    override fun getName(): String {
        return user.nickname
    }

    override fun areContentsTheSame(binder: BaseDataBinder<*>): Boolean {
        return if (binder !is RelateUserBinder){
            false
        }else {
            user == binder.user
        }
    }
}