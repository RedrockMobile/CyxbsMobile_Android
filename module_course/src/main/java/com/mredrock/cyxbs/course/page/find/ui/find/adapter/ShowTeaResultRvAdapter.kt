package com.mredrock.cyxbs.course.page.find.ui.find.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.page.find.bean.FindTeaBean
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/24 16:21
 */
class ShowTeaResultRvAdapter(
  private val teacherList: List<FindTeaBean>,
) : RecyclerView.Adapter<ShowTeaResultRvAdapter.ShowTeaResultViewHolder>() {
  
  fun setOnItemClick(onItemCLick: FindTeaBean.(position: Int) -> Unit): ShowTeaResultRvAdapter {
    mOnItemClick = onItemCLick
    return this
  }
  
  private var mOnItemClick: (FindTeaBean.(position: Int) -> Unit)? = null
  
  inner class ShowTeaResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.course_tv_item_show_tea_result_name)
    val tvContent: TextView = itemView.findViewById(R.id.course_tv_item_show_tea_result_content)
    val tvNum: TextView = itemView.findViewById(R.id.course_tv_item_show_tea_result_num)
    init {
      itemView.setOnSingleClickListener {
        mOnItemClick?.invoke(teacherList[layoutPosition], layoutPosition)
      }
    }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowTeaResultViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.course_rv_item_show_tea_result, parent, false)
    
    return ShowTeaResultViewHolder(view)
  }
  
  override fun onBindViewHolder(holder: ShowTeaResultViewHolder, position: Int) {
    holder.tvName.text = teacherList[position].name
    holder.tvContent.text = teacherList[position].content
    holder.tvNum.text = teacherList[position].num
  }
  
  override fun getItemCount(): Int = teacherList.size
}