package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.network.Student

/**
 * Created by anriku on 2018/10/13.
 */

class StudentListRecAdapter(private val mContext: Context, private val mStudentList: List<Student>) : BaseRecAdapter(mContext) {

    override fun getThePositionLayoutId(position: Int): Int {
        return R.layout.course_student_list_rec_item
    }

    override fun getItemCount(): Int = mStudentList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemView = holder.itemView

        itemView.findViewById<TextView>(R.id.stu_name).apply {
            text = "姓名:${mStudentList[position].stuName}"
        }
        itemView.findViewById<TextView>(R.id.stu_sex).apply {
            text = "性别:${mStudentList[position].stuSex}"
        }
        itemView.findViewById<TextView>(R.id.stu_year).apply {
            text = "入学年份${mStudentList[position].year}"
        }
        itemView.findViewById<TextView>(R.id.stu_major).apply {
            text = "专业${mStudentList[position].major}"
        }
        itemView.findViewById<TextView>(R.id.stu_school).apply {
            text = "学院${mStudentList[position].school}"
        }
        itemView.findViewById<TextView>(R.id.stu_id).apply {
            text = "学号${mStudentList[position].stuId}"
        }
    }

}