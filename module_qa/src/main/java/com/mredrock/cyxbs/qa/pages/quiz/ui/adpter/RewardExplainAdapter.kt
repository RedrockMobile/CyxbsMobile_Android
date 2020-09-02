package com.mredrock.cyxbs.qa.pages.quiz.ui.adpter

import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_item_reward_explain.view.*

/**
 * Created by yyfbe, Date on 2020/4/7.
 */
class RewardExplainAdapter : BaseRvAdapter<DownMessageText>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DownMessageText> = RewardExplainViewHolder(parent)
    class RewardExplainViewHolder(parent: ViewGroup) : BaseViewHolder<DownMessageText>(parent, R.layout.qa_recycler_item_reward_explain) {
        override fun refresh(data: DownMessageText?) {
            itemView.tv_title.text = data?.title
            itemView.tv_content.text = data?.content
        }

    }

}