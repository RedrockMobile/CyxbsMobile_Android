package com.mredrock.cyxbs.discover.map.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_recycle_item_favorite_list.view.*

/**
 *@author zhangzhe
 *@date 2020/8/12
 *@description
 */

class FavoriteListAdapter(context: Context, val viewModel: MapViewModel, private var mList: MutableList<FavoritePlace>) :
        RecyclerView.Adapter<FavoriteListAdapter.ViewHolder>() {

    private var mLayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(mLayoutInflater.inflate(R.layout.map_recycle_item_favorite_list, container, false))

    }

    fun setList(list: MutableList<FavoritePlace>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeNickname.text = mList[position].placeNickname
        if (position == mList.size - 1) {
            holder.line.gone()
        } else {
            holder.line.visible()
        }
        holder.itemView.setOnClickListener {
            viewModel.showIconById.value = mList[position].placeId
            viewModel.getPlaceDetails(mList[position].placeId, false)
            viewModel.showPopUpWindow.value = false
            viewModel.bottomSheetStatus.postValue(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeNickname = itemView.map_tv_favorite_list_item_text
        val line = itemView.map_view_favorite_line
    }

}