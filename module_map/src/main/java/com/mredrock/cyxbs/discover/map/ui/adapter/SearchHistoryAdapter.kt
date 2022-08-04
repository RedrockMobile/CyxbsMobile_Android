package com.mredrock.cyxbs.discover.map.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 *@author zhangzhe
 *@date 2020/8/12
 *@description
 */

class SearchHistoryAdapter(context: Context, private val viewModel: MapViewModel) :
        RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    private var mLayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.map_recycle_item_search_history, container, false))
    }


    override fun getItemCount(): Int {
        return viewModel.searchHistory.value?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTvPlaceName.text = viewModel.searchHistory.value?.get(position) ?: ""
        holder.itemView.setOnSingleClickListener {
            viewModel.searchHistoryString.value = viewModel.searchHistory.value?.get(position) ?: ""
        }
        holder.mIvDelete.setOnSingleClickListener {
            DataSet.deleteSearchHistory(viewModel.searchHistory.value?.get(position) ?: "")
            viewModel.notifySearchHistoryChange()
            notifyDataSetChanged()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvPlaceName:TextView = itemView.findViewById(R.id.map_tv_search_history_place_name)
        val mIvDelete:ImageView = itemView.findViewById(R.id.map_iv_search_history_icon)
    }

}