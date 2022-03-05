package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.config.RequestResultCode.ClickKnowledge

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchResultHeaderAdapter(
    private val searchKnowledgeAdapter: SearchKnowledgeAdapter,
    val recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var knowledge: Knowledge? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baseTitle: TextView = view.findViewById(R.id.tv_knowledge_base_title)
        val baseDetail: TextView = view.findViewById(R.id.tv_knowledge_base_detail)
        val cancelImage: View = view.findViewById(R.id.qa_knowledge_cancle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.qa_recycler_knowledge_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.baseTitle.text = knowledge?.title
        viewHolder.baseDetail.text = knowledge?.description
        val flexBoxManager = FlexboxLayoutManager(holder.itemView.context)
        flexBoxManager.flexWrap = FlexWrap.WRAP

        viewHolder.cancelImage.setOnClickListener {
            ClickKnowledge = false
            recyclerView.adapter = searchKnowledgeAdapter
            recyclerView.layoutManager = flexBoxManager
        }
    }

    override fun getItemCount() = 1

}