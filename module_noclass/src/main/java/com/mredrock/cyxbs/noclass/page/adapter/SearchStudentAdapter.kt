package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.bean.Student

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.adapter
 * @ClassName:      SearchStudentAdapter
 * @Author:         Yan
 * @CreateDate:     2022年09月02日 03:02:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    查找学生RV的adapter
 */
class SearchStudentAdapter(
  private val onAddClick : (NoclassGroup.Member) -> Unit
) : ListAdapter<Student,SearchStudentAdapter.VH>(studentDiffUtil){
 
  companion object{
    private val studentDiffUtil : DiffUtil.ItemCallback<Student> = object : DiffUtil.ItemCallback<Student>(){
      
      override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem.stuNum == newItem.stuNum
      }
  
      override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem.stuNum == newItem.stuNum && oldItem.major == newItem.major && oldItem.name == newItem.name
      }
  
    }
  }
  
  inner class VH(itemView : View) : RecyclerView.ViewHolder(itemView){
    val tvName: TextView = itemView.findViewById(R.id.noclass_tv_student_name)
    val tvMajor: TextView = itemView.findViewById(R.id.noclass_tv_student_major)
    val tvNum: TextView = itemView.findViewById(R.id.noclass_tv_student_id)
    private val btnAdd = itemView.findViewById<ImageView>(R.id.noclass_iv_student_add).apply {
      setOnClickListener {
        val student = currentList[absoluteAdapterPosition]
        val member = NoclassGroup.Member(student.name,student.stuNum)
        onAddClick.invoke(member)
      }
    }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_search_student,parent,false)
    return VH(view)
  }
  
  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.tvName.text = currentList[position].name
    holder.tvMajor.text = currentList[position].major
    holder.tvNum.text = currentList[position].stuNum
  }
  
}