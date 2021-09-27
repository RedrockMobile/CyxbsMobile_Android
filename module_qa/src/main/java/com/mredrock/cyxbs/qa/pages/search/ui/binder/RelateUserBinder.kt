package com.mredrock.cyxbs.qa.pages.search.ui.binder

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.UserInfo
import com.mredrock.cyxbs.qa.databinding.QaRecyclerItemSearchUserBinding

/**
 * @class
 * @author YYQF
 * @data 2021/9/26
 * @description
 **/
class RelateUserBinder(private val user: UserInfo.Data.User) :  BaseDataBinder<QaRecyclerItemSearchUserBinding>() {
    override fun layoutId() = R.layout.qa_recycler_item_search_user

    override val itemId: String
        get() = user.id

    override fun onBindViewHolder(binding: QaRecyclerItemSearchUserBinding) {
        super.onBindViewHolder(binding)
        binding.apply {
            Glide.with(root.context).load(user.avatar).into(binding.qaIvSearchUserAvatar)
            qaTvSearchUserNickname.text = user.nickname
            qaTvSearchUserIntroduction.text = user.introduction
            if (user.isFocus){
                qaTvSearchUserFocus.background = ContextCompat
                    .getDrawable(root.context,R.drawable.qa_shape_tv_search_focused)
                qaTvSearchUserFocus.text = "互相关注"
            }else {
                qaTvSearchUserFocus.background = ContextCompat
                    .getDrawable(root.context,R.drawable.qa_shape_tv_search_unfocused)
                qaTvSearchUserFocus.text = "关注"
            }
        }
    }
}