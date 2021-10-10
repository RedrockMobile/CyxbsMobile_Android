package com.mredrock.cyxbs.qa.pages.search.ui.binder

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.UserBrief
import com.mredrock.cyxbs.qa.databinding.QaRecyclerItemSearchUserBinding

/**
 * @class
 * @author YYQF
 * @data 2021/9/26
 * @description
 **/
class RelateUserBinder(private val user: UserBrief,
                       //关注按钮点击事件
                       private var onFocusClick:((view:View, user:UserBrief)->Unit)? = null)
    : BaseDataBinder<QaRecyclerItemSearchUserBinding>() {

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

            qaTvSearchUserFocus.background = ContextCompat
                .getDrawable(root.context,R.drawable.qa_shape_tv_search_focused)

            if (user.isFocus){
                qaTvSearchUserFocus.text = "互相关注"
            }else {
                qaTvSearchUserFocus.text = "已关注"
            }

            qaTvSearchUserFocus.setOnClickListener {
                onFocusClick?.invoke(it,user)
            }
        }
    }

}