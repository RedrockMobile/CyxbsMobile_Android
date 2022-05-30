package com.mredrock.cyxbs.discover.schoolcar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.schoolcar.bean.Line
import com.mredrock.cyxbs.schoolcar.R
import okhttp3.internal.notifyAll
import java.util.*

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 15:09
 *
 */
class CarIconAdapter(val context: Context?, val lines:List<Line>): RecyclerView.Adapter<CarIconAdapter.ViewHolder>() {

  private var block:((position:Int,isIcon:Boolean)->Unit)?= null
  private val checks = BooleanArray(lines.size+1).apply { Arrays.fill(this,false) }
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iv = itemView.findViewById<ImageView>(R.id.schoolcar_item_iv_car_line)
    val tv = itemView.findViewById<TextView>(R.id.schoolcar_item_tv_car_line)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val item = LayoutInflater.from(context).inflate(R.layout.schoolcar_item_car_line, parent, false)
    return ViewHolder(item)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (position == lines.size){
      holder.iv.setImageResource(getIcon(-1,checks[position]))
      holder.tv.text = "乘车指南"
      holder.iv.setOnClickListener {
        block?.invoke(-1,true)
        Arrays.fill(checks,false)
        checks[position] = true
        notifyDataSetChanged()
      }
    }else{
      holder.iv.setImageResource(getIcon(position,checks[position]))
      holder.tv.text = lines[position].name
      holder.iv.setOnClickListener {
        if (!checks[position]){
          block?.invoke(position,true)
          Arrays.fill(checks,false)
          checks[position] = true
          notifyDataSetChanged()
        }else{
          block?.invoke(-2,true)
          checks[position] = false
          notifyDataSetChanged()
        }
      }
    }
  }
  //+1是因为最后一个指南
  override fun getItemCount(): Int = lines.size+1

  fun choose(id:Int,isCheckIcon: Boolean = false,isShowIcon:Boolean = false){
    var need = -1
    lines.forEachIndexed { index, line ->
      if (line.id == id) {
        Arrays.fill(checks,false)
        if (isCheckIcon || isShowIcon)
        if (id != -2) checks[index] = true
        need = index
      }
    }

    block?.invoke(need,isCheckIcon)
    notifyDataSetChanged()
  }

  fun clear(){
    Arrays.fill(checks,false)
    block?.invoke(-2,true)
    notifyDataSetChanged()
  }

  fun setOnItemListener(block:(position:Int,isIcon:Boolean)->Unit){
    this.block = block
  }

  private fun getIcon(id:Int,check:Boolean):Int{
    if (check){
      return when(id+1){
        0-> R.drawable.schoolcar_car_icon_0
        1-> R.drawable.schoolcar_car_icon_1_select
        2-> R.drawable.schoolcar_car_icon_2_select
        3-> R.drawable.schoolcar_car_icon_3_select
        4-> R.drawable.schoolcar_car_icon_4_select
        else -> R.drawable.schoolcar_car_icon_1_select
      }
    }else{
      return when(id+1){
        0-> R.drawable.schoolcar_car_icon_0
        1-> R.drawable.schoolcar_car_icon_1
        2-> R.drawable.schoolcar_car_icon_2
        3-> R.drawable.schoolcar_car_icon_3
        4-> R.drawable.schoolcar_car_icon_4
        else -> R.drawable.schoolcar_car_icon_1
      }
    }
  }
}