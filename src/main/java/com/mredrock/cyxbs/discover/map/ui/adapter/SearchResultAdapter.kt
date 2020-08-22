package com.mredrock.cyxbs.discover.map.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.PlaceItem
import com.mredrock.cyxbs.discover.map.model.DataSet
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_recycle_item_search_result.view.*

/**
 *@author zhangzhe
 *@date 2020/8/12
 *@description
 */

class SearchResultAdapter(context: Context, private val viewModel: MapViewModel) :
        RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var mLayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mLayoutInflater.inflate(R.layout.map_recycle_item_search_result, container, false))
    }


    init {
        viewModel.searchResult.addOnListChangedCallback(
                object : ObservableList.OnListChangedCallback<ObservableArrayList<PlaceItem>>() {
                    override fun onChanged(sender: ObservableArrayList<PlaceItem>?) {
                        notifyDataSetChanged()
                    }

                    override fun onItemRangeRemoved(sender: ObservableArrayList<PlaceItem>?, positionStart: Int, itemCount: Int) {
                        notifyItemRangeChanged(positionStart, itemCount)
                        notifyDataSetChanged()
                    }

                    override fun onItemRangeMoved(sender: ObservableArrayList<PlaceItem>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                        if (itemCount == 1) {
                            notifyItemMoved(fromPosition, toPosition)
                        } else {
                            notifyDataSetChanged()
                        }
                    }

                    override fun onItemRangeInserted(sender: ObservableArrayList<PlaceItem>?, positionStart: Int, itemCount: Int) {
                        notifyItemRangeInserted(positionStart, itemCount)
                    }

                    override fun onItemRangeChanged(sender: ObservableArrayList<PlaceItem>?, positionStart: Int, itemCount: Int) {
                        notifyItemRangeChanged(positionStart, itemCount)
                    }
                }
        )
    }

    override fun getItemCount(): Int {
        return viewModel.searchResult.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeName.text = viewModel.searchResult[position].placeName
        holder.itemView.setOnClickListener {
            DataSet.deleteSearchHistory(viewModel.searchResult[position].placeName)
            DataSet.addSearchHistory(viewModel.searchResult[position].placeName)
            viewModel.notifySearchHistoryChange()
            viewModel.showSomePlaceIconById.value = mutableListOf(viewModel.searchResult[position].placeId)
            viewModel.getPlaceDetails(viewModel.searchResult[position].placeId, false)
            viewModel.closeSearchFragment.value = true
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName = itemView.map_tv_search_result_place_name
    }

}