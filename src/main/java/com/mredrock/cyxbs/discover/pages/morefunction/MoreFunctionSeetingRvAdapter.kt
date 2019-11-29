package com.mredrock.cyxbs.discover.pages.morefunction

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MoreFunctionSeetingRvAdapter(private val moreFunctionRvAdapter: MoreFunctionRvAdapter): RecyclerView.Adapter<MoreFunctionRvAdapter.MoreFunctionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreFunctionRvAdapter.MoreFunctionViewHolder {
        return moreFunctionRvAdapter.onCreateViewHolder(parent,viewType)
    }

    override fun getItemCount(): Int {
        return moreFunctionRvAdapter.itemCount
    }

    override fun onBindViewHolder(holder: MoreFunctionRvAdapter.MoreFunctionViewHolder, position: Int) {
        moreFunctionRvAdapter.onBindViewHolder(holder,position)
        holder.itemView.setOnClickListener {

        }
    }


}