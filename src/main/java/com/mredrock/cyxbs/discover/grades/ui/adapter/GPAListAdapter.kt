package com.mredrock.cyxbs.discover.grades.ui.adapter

import android.content.Context
import com.mredrock.cyxbs.discover.grades.bean.analyze.TermGrade
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseAdapter
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseHolder
import kotlinx.android.synthetic.main.grades_item_gpa_list.view.*

/**
 * Created by roger on 2020/3/22
 */
class GPAListAdapter(val context: Context,
                     data: MutableList<TermGrade>?,
                     layoutIds: IntArray) : BaseAdapter<TermGrade>(context, data, layoutIds) {

    private val HEADER = 0
    override fun getItemType(position: Int): Int {
        NORMAL = 1
        getData()?.let { _ ->
            return if (position == 0) {
                HEADER
            } else {
                NORMAL
            }
        }
        return FOOTER
    }

    override fun onBinds(holder: BaseHolder, t: MutableList<TermGrade>?, position: Int, viewType: Int) {
        when (viewType) {
            NORMAL -> {
                //-1是因为有header
                t?.get(position - 1)?.let { termGrade ->
                    holder.itemView.grades_gpa_list_tv_average_credit_num.text = termGrade.gpa
                    holder.itemView.grades_gpa_list_tv_average_grades_num.text = termGrade.grade
                    //rank可能会出现为0，因为教务在线还没有排名
                    if (termGrade.rank == "0") {
                        holder.itemView.grades_gpa_list_tv_average_rank_num.text = "--"
                    } else {
                        holder.itemView.grades_gpa_list_tv_average_rank_num.text = termGrade.rank
                    }
                    holder.itemView.grades_gpa_list_tv_grades_detail.setOnClickListener {
                    }

                    val term = termGrade.term
                    val termOfYear = term.substring(4)
                    val year = term.substring(0, 4).toInt()
                    if (termOfYear == "1") {
                        //说明是上学期
                        holder.itemView.grades_gpa_list_tv_term.text = "${year}-${year + 1}第一学期"
                    } else {
                        holder.itemView.grades_gpa_list_tv_term.text = "${year}-${year + 1}第二学期"
                    }
                }
            }
            HEADER -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return getDataSize() + 1
    }
}