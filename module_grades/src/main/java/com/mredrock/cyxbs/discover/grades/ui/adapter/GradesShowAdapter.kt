/**
 * @Author fxy
 * @Date 2019-12-11 22:23
 */

package com.mredrock.cyxbs.discover.grades.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Grade

class GradesShowAdapter(val data : MutableList<Grade>,
                        val context: Context)
    : RecyclerView.Adapter<GradesShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grades_item_gpa_list_child,parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            holder.mTvGradeCourse.text = it.course
            holder.mTvGradeScore.text = it.grade
            holder.mTvGradeProperty.text = it.property
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mTvGradeCourse:TextView = itemView.findViewById(R.id.tv_grade_course)
        val mTvGradeScore:TextView = itemView.findViewById(R.id.tv_grade_score)
        val mTvGradeProperty:TextView = itemView.findViewById(R.id.tv_grade_property)
    }
}