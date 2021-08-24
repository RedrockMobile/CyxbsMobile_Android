package com.mredrock.cyxbs.mine.page.feedback.center.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineItemQuestionBinding
import com.mredrock.cyxbs.mine.page.feedback.center.ui.FeedbackCenterActivity

/**
 * @Date : 2021/8/24   18:27
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterAdapter : RecyclerView.Adapter<FeedbackCenterAdapter.InnerViewHolder>() {

    private var eventHandler:FeedbackCenterActivity.EventHandler?=null

    private val mContentList by lazy {
        mutableListOf<String>()
    }

    inner class InnerViewHolder(itemView:View,val itemBinding:MineItemQuestionBinding):RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val itemBinding = DataBindingUtil.inflate<MineItemQuestionBinding>(
                LayoutInflater.from(parent.context),
                R.layout.mine_item_question,
                parent,
                false
        )
        return InnerViewHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val title = mContentList[position]
        holder.itemBinding.eventHandler = eventHandler
        holder.itemBinding.tvCenterTitle.text = title
    }

    override fun getItemCount(): Int {
        return mContentList.size
    }

    fun setData(contentList:List<String>){
        mContentList.clear()
        mContentList.addAll(contentList)

        notifyDataSetChanged()
    }

    fun setEventHandler(eventHandler:FeedbackCenterActivity.EventHandler){
        this.eventHandler = eventHandler
    }



}