package com.mredrock.cyxbs.discover.schoolcar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.schoolcar.bean.Station
import com.mredrock.cyxbs.schoolcar.R

/**
 *@Author:SnowOwlet
 *@Date:2022/5/12 09:02
 *
 */
class CarPageSiteAdapter(val context: Context?, var stations:List<Station>,val lineId:Int): RecyclerView.Adapter<CarPageSiteAdapter.ViewHolder>() {

  companion object{
    val END_VIEW = 0
    val START_VIEW = 1
    val COMMON_VIEW = 2
  }

  class ViewHolder(
    itemView: View,
    ivRes:Int,
    tvRes:Int
  ) : RecyclerView.ViewHolder(itemView) {
    val iv: ImageView = itemView.findViewById(ivRes)
    val tv: TextView = itemView.findViewById(tvRes)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      START_VIEW ->
        ViewHolder(
          LayoutInflater.from(context)
            .inflate(R.layout.schoolcar_item_carsite_start, parent, false),
          R.id.schoolcar_item_iv_car_site_start,
          R.id.schoolcar_item_tv_car_site_start
        )
      END_VIEW ->
        ViewHolder(
          LayoutInflater.from(context)
            .inflate(R.layout.schoolcar_item_carsite_end, parent, false),
          R.id.schoolcar_item_iv_car_site_end,
          R.id.schoolcar_item_tv_car_site_end
        )
      else ->
        ViewHolder(
          LayoutInflater.from(context).inflate(R.layout.schoolcar_item_carsite, parent, false),
          R.id.schoolcar_item_iv_car_site,
          R.id.schoolcar_item_tv_car_site
        )
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val type = getItemViewType(position)
    holder.tv.text = stations[position].name
  }

  override fun getItemCount(): Int = stations.size

  override fun getItemViewType(position: Int): Int {
    return when(position){
      0 -> START_VIEW
      stations.size-1 -> END_VIEW
      else -> COMMON_VIEW
    }
  }
}