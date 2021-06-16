package com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Ignore
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.qa_recycler_item_ignore.view.*

class MyIgnoreViewHolder(val item: View) : BaseViewHolder<Ignore>(item) {

    val avatar: CircleImageView = itemView.findViewById(R.id.qa_circle_iv_ignore_avatar)
    val nickName: AppCompatTextView = itemView.findViewById(R.id.qa_tv_ignore_item_nickname)
    val introduction: AppCompatTextView = itemView.findViewById(R.id.qa_tv_ignore_item_user_assign)
    val antiIgnore: AppCompatButton = itemView.findViewById(R.id.qa_btn_ignore_item_anti_ignore)

    override fun refresh(data: Ignore?) {
        data?.let {
            avatar.setAvatarImageFromUrl(data.avatar)
            nickName.text = data.nickName
            introduction.text = data.introduction
        }
    }

}