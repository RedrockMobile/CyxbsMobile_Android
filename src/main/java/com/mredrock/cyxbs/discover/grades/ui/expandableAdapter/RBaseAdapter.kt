package com.mredrock.cyxbs.discover.grades.ui.expandableAdapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAData
import com.mredrock.cyxbs.discover.grades.bean.analyze.SingleGrade
import com.mredrock.cyxbs.discover.grades.bean.analyze.TermGrade
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseHolder
import kotlinx.android.synthetic.main.grades_item_gpa_list_child.view.*
import kotlinx.android.synthetic.main.grades_item_gpa_list_normal_top.view.*

/**
 * Created by roger on 2020/3/22
 */
class RBaseAdapter(
        val context: Context,
        private val type2ResId: HashMap<Int, Int>,
        private val gpaData: GPAData
) : RecyclerView.Adapter<BaseHolder>() {
    companion object {
        const val HEADER = 0
        const val NORMAL_TOP = 2
        const val NORMAL_BOTTOM = 4
        const val CHILD = 5
    }

    private var arrayList: ArrayList<Any> = arrayListOf()

    init {
        refreshArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val res = type2ResId[viewType] ?: throw Exception("Invalid viewType")
        return BaseHolder.getHolder(context, parent, res)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val type = getItemViewType(position)
        onBind(holder, position, type)
    }

    private fun onBind(holder: BaseHolder, position: Int, type: Int) {
        when (arrayList[position]) {

            is HeaderData -> {

            }
            is TermGrade -> {
                val termGrade = arrayList[position] as TermGrade
                holder.itemView.grades_gpa_list_tv_average_credit_num.text = termGrade.gpa
                holder.itemView.grades_gpa_list_tv_average_grades_num.text = termGrade.grade
                //rank可能会出现为0，因为教务在线还没有排名
                if (termGrade.rank == "0") {
                    holder.itemView.grades_gpa_list_tv_average_rank_num.text = "--"
                } else {
                    holder.itemView.grades_gpa_list_tv_average_rank_num.text = termGrade.rank
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
            is NormalBottom -> {
                holder.itemView.setOnClickListener {
                    val bean = arrayList[position] as NormalBottom
                    if (bean.termGrade.isClose()) {
                        bean.termGrade.status = TermGrade.EXPAND
                    } else {
                        bean.termGrade.status = TermGrade.CLOSE
                    }
                    refreshArrayList()

                }
            }
            is SingleGrade -> {
                val single = arrayList[position] as SingleGrade
                holder.itemView.tv_grade_course.text = single.className
                holder.itemView.tv_grade_score.text = single.grade
                holder.itemView.tv_grade_property.text = single.classType
            }
        }
    }

    private fun refreshArrayList() {
        arrayList = arrayListOf()

        arrayList.add(HeaderData())
        for (item in gpaData.termGrade) {
            arrayList.add(item)
            if (!item.isClose()) {
                for (it in item.singleGrade) {
                    arrayList.add(it)
                }
            }
            arrayList.add(NormalBottom(item))
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        when (arrayList[position]) {
            is HeaderData -> {
                return HEADER
            }
            is NormalBottom -> {
                return NORMAL_BOTTOM
            }
            is TermGrade -> {
                return NORMAL_TOP
            }
            is SingleGrade -> {
                return CHILD
            }
        }
        return NORMAL_TOP
    }
}