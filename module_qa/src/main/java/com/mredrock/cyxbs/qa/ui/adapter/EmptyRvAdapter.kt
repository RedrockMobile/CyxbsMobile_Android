package com.mredrock.cyxbs.qa.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_empty_holder.view.*

/**
 * Created By jay68 on 2018/9/23.
 */
class EmptyRvAdapter(private val hint: String) : BaseRvAdapter<Boolean>() {
    private var showed = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EmptyViewHolder(hint, parent)

    fun showHolder(size: Int = 1) {
        if (!showed) {
            showed = true
            refreshData(BooleanArray(size) { false }.toList())
        }
    }

    fun hideHolder() {
        refreshData(listOf(true))
    }

    class EmptyViewHolder(private val hint: String, parent: ViewGroup) : BaseViewHolder<Boolean>(parent, R.layout.qa_recycler_item_empty_holder) {
        override fun refresh(data: Boolean?) {
            if (data == true) {
                itemView.card_holder.gone()
                itemView.tv_hint.text = hint
                itemView.iv_hint.visible()
                itemView.tv_hint.visible()
            } else {
                itemView.card_holder.visible()
                itemView.tv_hint.gone()
                itemView.iv_hint.gone()
            }
        }
    }
}