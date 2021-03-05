package com.mredrock.cyxbs.qa.pages.mine.ui.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_recycler_item_ignore.view.*

class IgnoreViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

    val avatar: AppCompatImageView = itemView.findViewById(R.id.qa_circle_iv_ignore_avatar)
    val nickName: AppCompatTextView = itemView.findViewById(R.id.qa_tv_dynamic_nickname)
    val introduction: AppCompatTextView = itemView.findViewById(R.id.qa_tv_ignore_item_user_assign)
    val antiIgnore: AppCompatButton = itemView.findViewById(R.id.qa_btn_ignore_item_anti_ignore)

}