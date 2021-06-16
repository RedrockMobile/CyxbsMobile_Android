package com.mredrock.cyxbs.qa.pages.search.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.common.BaseApp
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
import com.mredrock.cyxbs.qa.config.RequestResultCode
import com.mredrock.cyxbs.qa.config.RequestResultCode.ClickKnowledge
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.widget.NineGridView
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*
import kotlinx.android.synthetic.main.qa_recycler_knowledge_detail.view.*

/**
 * Created by yyfbe, Date on 2020/8/13.
 */
class SearchResultHeaderAdapter( val searchKnowledgeAdapter: SearchKnowledgeAdapter,
                                val recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var knowledge: Knowledge?=null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val baseTitle: TextView = view.findViewById(R.id.tv_knowledge_base_title);
        val baseDetail: TextView = view.findViewById(R.id.tv_knowledge_base_detail);
        val cancleImage: ImageView = view.findViewById(R.id.qa_knowledge_cancle);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qa_recycler_knowledge_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ViewHolder
        holder.baseTitle.text = knowledge?.title
        holder.baseDetail.text = knowledge?.description
        val flexBoxManager = FlexboxLayoutManager(BaseApp.context)
        flexBoxManager.flexWrap = FlexWrap.WRAP

        holder.cancleImage.setOnClickListener {
            ClickKnowledge = false
            recyclerView.adapter = searchKnowledgeAdapter
            recyclerView.layoutManager = flexBoxManager
        }
    }

    override fun getItemCount() = 1

}