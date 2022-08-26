package com.mredrock.cyxbs.discover.schoolcar.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.schoolcar.bean.Station
import com.mredrock.cyxbs.schoolcar.R
import java.util.*

/**
 *@Author:SnowOwlet
 *@Date:2022/5/12 09:02
 *
 */
class CarPageSiteAdapter(val context: Context?, var stations:List<Station>,val lineId:Int): RecyclerView.Adapter<CarPageSiteAdapter.ViewHolder>() {

  companion object{
    //开始和终点
    val SIDE_VIEW = 1
    //普通线
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
    return if (viewType == SIDE_VIEW){
      ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.schoolcar_item_carsite_onset, parent, false),
        R.id.schoolcar_item_iv_car_site_onset,
        R.id.schoolcar_item_tv_car_site_onset)
    }else{
      ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.schoolcar_item_carsite, parent, false),
        R.id.schoolcar_item_iv_car_site,
        R.id.schoolcar_item_tv_car_site)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val type = getItemViewType(position)
    holder.tv.text = stations[position].name
    if(type == SIDE_VIEW){
      when(position){
        0 -> holder.iv.setImageResource(R.drawable.schoolcar_car_site_line_first)
        stations.size-1 -> holder.iv.setImageResource(R.drawable.schoolcar_car_site_line_last)
      }
    }else{
        holder.iv.setImageResource(R.drawable.schoolcar_car_site_line)
    }
  }

  override fun getItemCount(): Int = stations.size

  override fun getItemViewType(position: Int): Int {
    when(position){
      0,stations.size-1 -> return SIDE_VIEW
      else ->  return COMMON_VIEW
    }
  }
}