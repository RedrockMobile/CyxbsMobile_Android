package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Comment
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_reply_inner.view.*

/**
 *@author zhangzhe
 *@date 2020/12/15
 *@description
 */

class ReplyListAdapter() : BaseRvAdapter<Comment>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Comment>
        = ReplyViewHolder(parent)


    class ReplyViewHolder(parent: ViewGroup) : BaseViewHolder<Comment>(parent, R.layout.qa_recycler_item_dynamic_reply_inner) {
        override fun refresh(data: Comment?) {
            data?: return
            itemView.apply {
                qa_tv_reply_inner_nickname.text = data.nickName
                qa_tv_reply_inner_content.text = data.content
                qa_tv_reply_inner_praise_count.text = data.praiseCount.toString()
                qa_iv_reply_inner_praise_count_image.isChecked = data.isPraised
                qa_iv_reply_inner_avatar.setAvatarImageFromUrl(data.avatar)


            }
        }
    }

}