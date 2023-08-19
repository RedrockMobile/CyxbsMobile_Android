package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassBatchResponseInfo.Student

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/19
 * @Description:
 *
 */
class SameNameStudentsAdapter : ListAdapter<Student, SameNameStudentsAdapter.ViewHolder>(diffUtil){
    companion object {
        private val diffUtil : DiffUtil.ItemCallback<Student> = object : DiffUtil.ItemCallback<Student>(){
            override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
        val studentName : TextView = itemView.findViewById(R.id.noclass_batch_tv_student_name)
        val studentNumber : TextView = itemView.findViewById(R.id.noclass_batch_tv_student_id)
        val isSelected : CheckBox = itemView.findViewById(R.id.noclass_batch_cb_student_select_status)
        init {
            isSelected.setOnCheckedChangeListener { button, status ->
                button.isChecked = status
                getItem(bindingAdapterPosition).isSelected = status
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noclass_item_batch_student,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = getItem(position)
        holder.studentName.text = itemData.name
        holder.studentNumber.text = itemData.id
    }
}