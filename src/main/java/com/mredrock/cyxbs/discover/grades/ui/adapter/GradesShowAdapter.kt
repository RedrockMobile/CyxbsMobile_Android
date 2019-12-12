/**
 * @Author fxy
 * @Date 2019-12-11 22:23
 */

package com.mredrock.cyxbs.discover.grades.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Grade
import kotlinx.android.synthetic.main.grades_item_grade.view.*

class GradesShowAdapter(val data : MutableList<Grade>,
                        val context: Context)
    : RecyclerView.Adapter<GradesShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grades_item_grade,parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            holder.itemView.tv_grade_course.text = it.course
            holder.itemView.tv_grade_score.text = it.grade
            holder.itemView.tv_grade_property.text = it.property
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}