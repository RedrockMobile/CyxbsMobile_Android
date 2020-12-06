package com.mredrock.cyxbs.mine.page.security.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.SecurityQuestion

/**
 * @date 2020-11-01
 * @author Sca RayleighZ
 */
class SelQuestionRVAdapter(val list: List<SecurityQuestion>, val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<SelQuestionRVAdapter.InnerViewHolder>() {
    class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.mine_tv_security_question_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mine_item_security_sel_question, parent, false)
        return InnerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.textView.text = list[position].content
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}