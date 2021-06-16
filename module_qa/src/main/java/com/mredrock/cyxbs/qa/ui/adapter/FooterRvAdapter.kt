package com.mredrock.cyxbs.qa.ui.adapter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.network.NetworkState
import kotlinx.android.synthetic.main.qa_recycler_item_footer.view.*

/**
 * Created By jay68 on 2018/9/23.
 */
class FooterRvAdapter(private val retryCallback: () -> Unit) : BaseRvAdapter<Int>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FooterViewHolder(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Int>, position: Int, data: Int) {
        super.onItemClickListener(holder, position, data)
        if (data == NetworkState.FAILED) {
            retryCallback.invoke()
        }
    }

    class FooterViewHolder(parent: ViewGroup) : BaseViewHolder<Int>(parent, R.layout.qa_recycler_item_footer) {
        override fun refresh(data: Int?) {
            when (data) {
                NetworkState.LOADING -> {
                    itemView.visible()
                    itemView.tv_hint.gone()
                    itemView.progress_bar.visible()
                }

                NetworkState.FAILED -> {
                    itemView.tv_hint.visible()
                    itemView.progress_bar.gone()
                    itemView.tv_hint.text = context.getString(R.string.qa_loading_error)
                }

                NetworkState.NO_MORE_DATA, NetworkState.SUCCESSFUL -> {
                    itemView.tv_hint.visible()
                    itemView.progress_bar.gone()
                    itemView.tv_hint.text = context.getString(R.string.qa_comment_list_no_more_comment)

                }
                else -> Unit
            }
        }
    }
}