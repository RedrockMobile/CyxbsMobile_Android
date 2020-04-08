package com.mredrock.cyxbs.discover.grades.ui.expandableAdapter

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAData
import com.mredrock.cyxbs.discover.grades.bean.analyze.SingleGrade
import com.mredrock.cyxbs.discover.grades.bean.analyze.TermGrade
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseHolder
import com.mredrock.cyxbs.discover.grades.utils.widget.AverageRule
import kotlinx.android.synthetic.main.grades_dialog_a_credit.view.*
import kotlinx.android.synthetic.main.grades_item_dialog_a_credit.view.*
import kotlinx.android.synthetic.main.grades_item_gpa_list_child.view.*
import kotlinx.android.synthetic.main.grades_item_gpa_list_child.view.tv_grade_score
import kotlinx.android.synthetic.main.grades_item_gpa_list_header.view.*
import kotlinx.android.synthetic.main.grades_item_gpa_list_normal_bottom.view.*
import kotlinx.android.synthetic.main.grades_item_gpa_list_normal_top.view.*
import kotlinx.android.synthetic.main.grades_layout_transition.view.*

/**
 * Created by roger on 2020/3/22
 * 一个多type的可以展开的adapter，时间匆忙，没有更多抽象
 */
class GPAAdapter(
        val context: Context,
        private val type2ResId: HashMap<Int, Int>,
        private val gpaData: GPAData
) : RBaseAdapter(type2ResId) {
    companion object {
        const val HEADER = 0
        const val NORMAL_TOP = 2
        const val NORMAL_BOTTOM = 4
        const val CHILD = 5

        const val GPA = "GPA"
        const val GRADES = "GRADES"
        const val RANK = "RANK"
    }

    init {
        refreshArrayList()
    }

    private var tabSelectedType = GPA

    private var isExpand: Boolean = false

    override fun onBind(holder: BaseHolder, position: Int, type: Int) {
        when (dataMirror[position]) {

            is HeaderData -> {
                val fl = holder.itemView.grades_tv_gpa_fl_title
                val changeScene: () -> Unit = {
                    TransitionManager.beginDelayedTransition(fl, TransitionSet().apply {
                        addTransition(Slide().apply {
                            slideEdge = Gravity.START
                            duration = 250
                        })
                    })
                    isExpand = !isExpand
                    if (isExpand) {
                        holder.itemView.grades_tv_gpa_title.gone()
                        holder.itemView.grades_tab_layout.visible()
                        holder.itemView.grades_iv_gpa_arrow.setImageResource(R.drawable.grades_ic_arrow_left)
                    } else {
                        holder.itemView.grades_tv_gpa_title.visible()
                        holder.itemView.grades_tab_layout.gone()
                        holder.itemView.grades_iv_gpa_arrow.setImageResource(R.drawable.grades_ic_arrow_right)
                    }
                }
                fl.setOnClickListener {
                    changeScene.invoke()
                }


                changeGPAGraph(holder.itemView)
                holder.itemView.grades_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        if (isExpand) {
                            changeScene.invoke()
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {

                        tab?.let {
                            when (it.position) {
                                0 -> {
                                    tabSelectedType = GPA

                                }
                                1 -> {
                                    tabSelectedType = GRADES
                                }
                                2 -> {
                                    tabSelectedType = RANK
                                }
                            }
                        }
                        when (tabSelectedType) {
                            GPA -> {
                                holder.itemView.grades_tv_gpa_title.text = context.getString(R.string.grades_bottom_sheet_gpa_rv_average_gpa)
                            }
                            GRADES -> {
                                holder.itemView.grades_tv_gpa_title.text = context.getString(R.string.grades_bottom_sheet_gpa_rv_average_grades)

                            }
                            RANK -> {
                                holder.itemView.grades_tv_gpa_title.text = context.getString(R.string.grades_bottom_sheet_gpa_rv_average_rank)
                            }
                        }
                        changeGPAGraph(holder.itemView)
                        if (isExpand) {
                            changeScene.invoke()
                        }
                    }

                })




                holder.itemView.grades_tv_a_credit_number.text = gpaData.aCredit
                holder.itemView.grades_tv_b_credit_number.text = gpaData.bCredit
                holder.itemView.grades_tv_a_credit_number.setOnClickListener {
                    clickACredit()
                }
                holder.itemView.grades_tv_a_credit.setOnClickListener {
                    clickACredit()
                }
            }
            is TermGrade -> {
                val termGrade = dataMirror[position] as TermGrade
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
                val bean = dataMirror[position] as NormalBottom
                holder.itemView.setOnClickListener {
                    if (bean.termGrade.isClose()) {
                        bean.termGrade.status = TermGrade.EXPAND
                        holder.itemView.grades_gpa_list_tv_grades_detail.text = "收起各科成绩"
                    } else {
                        bean.termGrade.status = TermGrade.CLOSE
                        holder.itemView.grades_gpa_list_tv_grades_detail.text = "查看各科成绩"
                    }
                    refreshArrayList()

                }
            }
            is SingleGrade -> {
                val single = dataMirror[position] as SingleGrade
                holder.itemView.tv_grade_course.text = single.className
                holder.itemView.tv_grade_score.text = single.grade
                holder.itemView.tv_grade_property.text = single.classType
            }
        }
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int, oldItem: Any, newItem: Any): Boolean {
        if (newItemPosition == oldItemPosition && newItemPosition == 0) {
            //说明是header
            return true
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int, oldItem: Any, newItem: Any): Boolean {
        if (newItemPosition == oldItemPosition && newItemPosition == 0) {
            //说明是header
            return true
        }
        return oldItem == newItem
    }

    private fun refreshArrayList() {
        val newArray = arrayListOf<Any>()
        newArray.add(HeaderData())
        for (item in gpaData.termGrade) {
            newArray.add(item)
            if (!item.isClose()) {
                newArray.addAll(item.singleGrade)
            }
            newArray.add(NormalBottom(item))
        }
        refreshUI(newArray)
    }

    override fun getItemViewType(position: Int): Int {
        when (dataMirror[position]) {
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

    private fun clickACredit() {
        val tag = "a_credit"
        if ((context as AppCompatActivity).supportFragmentManager.findFragmentByTag(tag) == null) {
            val dialog = CommonDialogFragment().apply {
                initView(
                        containerRes = R.layout.grades_dialog_a_credit,
                        positiveString = "知道了",
                        onPositiveClick = { dismiss() },
                        elseFunction = {
                            val data = gpaData.allCredit
                            it.tv_a_credit_score.text = gpaData.aCredit
                            it.grades_compulsory.tv_grade_score.text = data[0].credit
                            it.grades_compulsory.tv_grade_type.text = data[0].name

                            it.grades_optional.tv_grade_score.text = data[1].credit
                            it.grades_optional.tv_grade_type.text = data[1].name

                            it.grades_others.tv_grade_score.text = data[2].credit
                            it.grades_others.tv_grade_type.text = data[2].name

                            it.grades_humanistic.tv_grade_score.text = data[3].credit
                            it.grades_humanistic.tv_grade_type.text = data[3].name

                            it.grades_natural.tv_grade_score.text = data[4].credit
                            it.grades_natural.tv_grade_type.text = data[4].name

                            it.grades_cross_functional.tv_grade_score.text = data[5].credit
                            it.grades_cross_functional.tv_grade_type.text = data[5].name

                        }
                )
            }.show(context.supportFragmentManager, tag)
        }

    }

    private fun changeGPAGraph(view: View) {
        val gpaGraph = view.grades_view_gpa_graph
        when (tabSelectedType) {
            GPA -> {
                val gpaList = gpaData.termGrade.map {
                    it.gpa.toFloat()
                }
                gpaGraph.setData(gpaList)
                gpaGraph.setRule(AverageRule(gpaList))
            }
            GRADES -> {
                val list = gpaData.termGrade.map {
                    it.grade.toFloat()
                }
                gpaGraph.setData(list)
                gpaGraph.setRule(AverageRule(list))
            }
            RANK -> {
                val list = gpaData.termGrade.map {
                    it.rank.toFloat()
                }
                gpaGraph.setData(list)
                gpaGraph.setRule(object : AverageRule(list) {
                    override fun mappingRule(old: Float): Float {
                        if (old == 0F) {
                            return 0F
                        }
                        return 4 - super.mappingRule(old)
                    }
                })
            }
        }

        gpaGraph.invalidate()
    }

}