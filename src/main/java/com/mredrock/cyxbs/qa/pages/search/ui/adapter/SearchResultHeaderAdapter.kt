package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.loadRedrockImage
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Knowledge
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.event.QASearchEvent
import kotlinx.android.synthetic.main.qa_recycler_item_header_from_knowledge.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchResultHeaderAdapter : BaseRvAdapter<Knowledge>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Knowledge> = ViewHolder(parent)


    class ViewHolder(parent: ViewGroup) : BaseViewHolder<Knowledge>(parent, R.layout.qa_recycler_item_header_from_knowledge) {
        private var textViews = mutableListOf<TextView>()

        @SuppressLint("ResourceAsColor")
        override fun refresh(data: Knowledge?) {
            if (data == null) return
            itemView.apply {
                tv_knowledge_base_title.text = data.title
                tv_knowledge_base_detail.text = data.description
                tv_knowledge_from.text = data.from
                context?.loadRedrockImage(data.photoUrl, iv_knowledge)
                if (data.related.isEmpty()) {
                    cv_related.gone()
                } else {
                    cv_related.visible()
                    for ((index, value) in data.related.withIndex()) {
                        if (textViews.size < index + 1) {
                            val textView = View.inflate(context, R.layout.qa_view_knowledge_related, null) as TextView
                            textView.setTextColor(R.color.common_menu_font_color_found)
                            textView.setOnClickListener { EventBus.getDefault().post(QASearchEvent(textView.text.toString())) }
                            textViews.add(textView)
                            (ll_related as LinearLayout).addView(textView)
                        }
                        textViews[index].text = value.title
                    }
                }
            }
        }
    }

    public fun hideHolder() {
        refreshData(emptyList())
    }
}