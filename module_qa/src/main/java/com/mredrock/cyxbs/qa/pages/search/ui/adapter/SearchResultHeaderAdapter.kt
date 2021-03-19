package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.showPhotos
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.loadRedrockImage
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Knowledge
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*
import kotlinx.android.synthetic.main.qa_recycler_knowledge_detail.view.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchResultHeaderAdapter(val knowledge: Knowledge) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baseTitle: TextView = view.findViewById(R.id.tv_knowledge_base_title);
        val baseDetail: TextView = view.findViewById(R.id.tv_knowledge_base_detail);
        val baseFrom: TextView = view.findViewById(R.id.tv_knowledge_from);
        val knowledgePhoto: ImageView = view.findViewById(R.id.iv_knowledge);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_knowledge_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ViewHolder
        holder.baseTitle.text = knowledge.title
        holder.baseDetail.text = knowledge.description
        holder.baseFrom.text = holder.itemView.context.resources.getString(R.string.qa_search_knowledge_from_where, knowledge.from)
        holder.itemView.context?.loadRedrockImage(knowledge.photoUrl, holder.knowledgePhoto)
        if (knowledge.photoUrl.isNotEmpty()) {
            holder.knowledgePhoto.setOnClickListener {
                showPhotos(holder.itemView.context, listOf(knowledge.photoUrl), 0)
            }
        }
    }
    override fun getItemCount() = 1

}