package com.mredrock.cyxbs.grades.ui.adapter

import android.content.Context
import com.mredrock.cyxbs.grades.bean.Grade
import com.mredrock.cyxbs.grades.utils.baseRv.BaseAdapter
import com.mredrock.cyxbs.grades.utils.baseRv.BaseHolder
import kotlinx.android.synthetic.main.grades_item_grade.view.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class GradesAdapter(context: Context,
                    data: MutableList<Grade>?,
                    layoutIds: IntArray) :
        BaseAdapter<Grade>(
                context,
                data,
                layoutIds) {

    override fun getItemType(position: Int): Int {
        getData()?.let { it ->
            return if (position != it.size) NORMAL else FOOTER
        }
        return FOOTER
    }

    override fun onBinds(holder: BaseHolder, t: MutableList<Grade>?, position: Int, viewType: Int) {
        when (viewType) {
            NORMAL -> {
                t?.get(position).let {
                    holder.itemView.tv_grade_course.text = it?.course
                    holder.itemView.tv_grade_score.text = it?.grade
                    holder.itemView.tv_grade_property.text = it?.property
                }
            }
        }
    }
}