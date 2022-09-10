package com.mredrock.cyxbs.noclass.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.noclass.R

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      NoClassGatheringAdapter
 * @Author:         Yan
 * @CreateDate:     2022年09月11日 01:09:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassGatheringAdapter(private val context : Context,private val mMap : HashMap<Int,HashMap<String,Boolean>>) :  RecyclerView.Adapter<NoClassGatheringAdapter.VH>(){
  
  inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
      val parentView: GridLayout = itemView.findViewById(R.id.noclass_gl_container)
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_layout_gathering,parent,false)
    return VH(view)
  }
  
  override fun onBindViewHolder(holder: VH, position: Int) {
    val left = 8 - mMap[position]!!.size
    mMap[position]!!.forEach {
      val view = noclassView(context)
      val linearLayout = view.findViewById<LinearLayout>(R.id.noclass_ll_gathering_item_container)
      val textView = view.findViewById<TextView>(R.id.noclass_tv_gathering_name).apply {
        text = it.key
      }
      if (!it.value){
        linearLayout.setBackgroundResource(R.drawable.noclass_shape_gathering_class_bg)
        textView.setTextColor(R.color.noclass_gather_text_class_bg.color)
      }
      holder.parentView.addView(view)
    }
    (1 .. left).forEach{ _ ->
      holder.parentView.addView(emptyView(context))
    }
  }
  
  private fun noclassView(context: Context):View{
    return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering,null).apply {
      layoutParams = GridLayout.LayoutParams().apply {
        setMargins(12,14,12,14)
        columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f)
      }
    }
  }
  
  private fun emptyView(context: Context):View{
    return LayoutInflater.from(context).inflate(R.layout.noclass_item_gathering,null).apply {
      layoutParams = GridLayout.LayoutParams().apply {
        setMargins(12,14,12,14)
        columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f)
      }
      visibility = View.INVISIBLE
    }
  }
  
  override fun getItemCount(): Int {
    return mMap.size
  }
  
  
}