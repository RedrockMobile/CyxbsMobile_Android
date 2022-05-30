package com.mredrock.cyxbs.discover.schoolcar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.schoolcar.bean.Line
import com.mredrock.cyxbs.schoolcar.R
import com.mredrock.cyxbs.schoolcar.databinding.SchoolcarItemCarPageBinding

/**
 *@Author:SnowOwlet
 *@Date:2022/5/11 21:45
 *
 */
class CarPageAdapter(val context: Context?, val lines:List<Line>): RecyclerView.Adapter<CarPageAdapter.ViewHolder>() {

  inner class ViewHolder(val binding: SchoolcarItemCarPageBinding) : RecyclerView.ViewHolder(binding.root) {
    val rv = binding.schoolCarDetailSiteRv.apply {
      this.layoutManager = LinearLayoutManager(context).apply {
        orientation = LinearLayoutManager.HORIZONTAL
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = SchoolcarItemCarPageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    return ViewHolder(binding)
  }
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val line = lines[position]
    holder.rv.adapter = CarPageSiteAdapter(context, line.stations,line.id)
    holder.binding.apply {
      schoolCarDetailIv.setImageResource(getIcon(line.id))
      schoolCarDetailCardLineTvType.text = line.sendType
      schoolCarDetailCardRunTvType.text = line.runType
      schoolCarDetailTvTime.text = "运行时间: ${line.runTime}"
      schoolCarDetailTvTitle.text = line.name
    }
  }

  override fun getItemCount(): Int = lines.size

  private fun getIcon(id:Int):Int{
    return when(id+1){
      1-> R.drawable.schoolcar_car_icon_1
      2-> R.drawable.schoolcar_car_icon_2
      3-> R.drawable.schoolcar_car_icon_3
      4-> R.drawable.schoolcar_car_icon_4
      else -> R.drawable.schoolcar_car_icon_1
    }
  }
}