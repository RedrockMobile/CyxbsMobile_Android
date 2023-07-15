package com.mredrock.cyxbs.discover.grades.ui.expandableAdapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
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
import com.mredrock.cyxbs.discover.grades.utils.widget.AverageRule
import com.mredrock.cyxbs.discover.grades.utils.widget.GpAGraph
import com.mredrock.cyxbs.discover.grades.utils.widget.NoDoubleClickListener

/**
 * Created by roger on 2020/3/22
 * 一个多type的可以展开的adapter，时间匆忙，没有更多抽象
 */
class GPAAdapter(
    val context: Context,
    private val type2ResId: HashMap<Int, Int>,
    private val gpaData: GPAData
) : RBaseAdapter<GPAAdapter.GpaVH>() {
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

    /**
     * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
     * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
     * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
     * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
     * stackoverflow上的回答：
     * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
     */
    open class GpaVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    class HeaderVH(itemView: View) : GpaVH(itemView) {
        val mGpAGraph: GpAGraph = itemView.findViewById(R.id.grades_view_gpa_graph)
        val fl: FrameLayout = itemView.findViewById(R.id.grades_tv_gpa_fl_title)
        val mTvGpaTitle: TextView = itemView.findViewById(R.id.grades_tv_gpa_title)
        val mTabLayout: TabLayout = itemView.findViewById(R.id.grades_tab_layout)
        val mIvGpaArrow: ImageView = itemView.findViewById(R.id.grades_iv_gpa_arrow)
        val mTvACreditNumber: TextView =
            itemView.findViewById(R.id.grades_tv_a_credit_number)
        val mTvBCreditNumber: TextView =
            itemView.findViewById(R.id.grades_tv_b_credit_number)
        val mTvACredit: TextView = itemView.findViewById(R.id.grades_tv_a_credit)
    }

    class NormalTopVH(itemView: View) : GpaVH(itemView) {
        val mTvAverageCreditNum: TextView =
            itemView.findViewById(R.id.grades_gpa_list_tv_average_credit_num)
        val mTvAverageGradesNum: TextView =
            itemView.findViewById(R.id.grades_gpa_list_tv_average_grades_num)
        val mTvAverageRankNum: TextView =
            itemView.findViewById(R.id.grades_gpa_list_tv_average_rank_num)
        val mTvTerm: TextView = itemView.findViewById(R.id.grades_gpa_list_tv_term)
    }

    class NormalBottomVH(itemView: View) : GpaVH(itemView) {
        val mTvGradesDetail: TextView =
            itemView.findViewById(R.id.grades_gpa_list_tv_grades_detail)
    }

    class ChildVH(itemView: View) : GpaVH(itemView) {
        val mTvGradeCourse: TextView = itemView.findViewById(R.id.tv_grade_course)
        val mTvGradeScore: TextView = itemView.findViewById(R.id.tv_grade_score)
        val mTvGradeProperty: TextView = itemView.findViewById(R.id.tv_grade_property)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GpaVH {
        val res = type2ResId[viewType] ?: throw Exception("Invalid viewType")
        //return BaseHolder.getHolder(parent.context, parent, res)
        return when (viewType) {
            HEADER -> HeaderVH(
                LayoutInflater.from(parent.context).inflate(
                    res, parent, false
                )
            )
            NORMAL_TOP -> NormalTopVH(
                LayoutInflater.from(parent.context).inflate(
                    res, parent, false
                )
            )
            NORMAL_BOTTOM -> NormalBottomVH(
                LayoutInflater.from(parent.context).inflate(
                    res, parent, false
                )
            )
            CHILD -> ChildVH(
                LayoutInflater.from(parent.context).inflate(
                    res, parent, false
                )
            )
            else -> error("")
        }
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
        oldItem: Any,
        newItem: Any
    ): Boolean {
        if (newItemPosition == oldItemPosition && newItemPosition == 0) {
            //说明是header
            return true
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
        oldItem: Any,
        newItem: Any
    ): Boolean {
        if (newItemPosition == oldItemPosition && newItemPosition == 0) {
            //说明是header
            return true
        }
        return oldItem == newItem
    }

    private fun refreshArrayList() {
        val newArray = arrayListOf<Any>()
        newArray.clear()
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

    override fun getItemCount(): Int {
        return dataMirror.size
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
            CommonDialogFragment().apply {
                initView(
                    containerRes = R.layout.grades_dialog_a_credit,
                    positiveString = "知道了",
                    onPositiveClick = { dismiss() },
                    elseFunction = {
                        val data = gpaData.allCredit

                        val mTvACreditScore: TextView = it.findViewById(R.id.tv_a_credit_score)
                        mTvACreditScore.text = gpaData.aCredit

                        val mClGradesCompulsory: ConstraintLayout =
                            it.findViewById(R.id.grades_compulsory)
                        mClGradesCompulsory.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[0].credit
                        mClGradesCompulsory.findViewById<TextView>(R.id.tv_grade_type).text =
                            data[0].name

                        val mClGradesOptional: ConstraintLayout =
                            it.findViewById(R.id.grades_optional)
                        mClGradesOptional.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[1].credit
                        mClGradesOptional.findViewById<TextView>(R.id.tv_grade_type).text =
                            data[1].name

                        val mClGradesOthers: ConstraintLayout = it.findViewById(R.id.grades_others)
                        mClGradesOthers.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[2].credit
                        mClGradesOthers.findViewById<TextView>(R.id.tv_grade_type).text = data[2].name

                        val mClGradesHumanistic: ConstraintLayout =
                            it.findViewById(R.id.grades_humanistic)
                        mClGradesHumanistic.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[3].credit
                        mClGradesHumanistic.findViewById<TextView>(R.id.tv_grade_type).text =
                            data[3].name

                        val mClGradesNatural: ConstraintLayout = it.findViewById(R.id.grades_natural)
                        mClGradesNatural.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[4].credit
                        mClGradesNatural.findViewById<TextView>(R.id.tv_grade_type).text =
                            data[4].name

                        val mClGradesCrossFunctional: ConstraintLayout =
                            it.findViewById(R.id.grades_cross_functional)
                        mClGradesCrossFunctional.findViewById<TextView>(R.id.tv_grade_score).text =
                            data[5].credit
                        mClGradesCrossFunctional.findViewById<TextView>(R.id.tv_grade_type).text =
                            data[5].name

                    }
                )
            }.show(context.supportFragmentManager, tag)
        }
    }

    private fun changeGPAGraph(view: HeaderVH) {
        val gpaGraph = view.mGpAGraph
        when (tabSelectedType) {
            GPA -> {
                val gpaList = gpaData.termGrade.map {
                    it.gpa
                }
                gpaGraph.setRule(AverageRule(gpaList))
                gpaGraph.setData(gpaList)
            }
            GRADES -> {
                val list = gpaData.termGrade.map {
                    it.grade
                }
                gpaGraph.setRule(AverageRule(list))
                gpaGraph.setData(list)
            }
            RANK -> {
                val list = gpaData.termGrade.map {
                    it.rank
                }
                gpaGraph.setRule(object : AverageRule(list) {
                    override fun mappingRule(old: String): Float {
                        return 4.5f - super.mappingRule(old)
                    }
                })
                gpaGraph.setData(list)
            }
        }
        gpaGraph.invalidate()
    }

    override fun onBind(holder: GpaVH, position: Int, type: Int) {
        when (dataMirror[position]) {
            is HeaderData -> {
                if (holder is HeaderVH) {
                    val changeScene: () -> Unit = {
                        TransitionManager.beginDelayedTransition(holder.fl, TransitionSet().apply {
                            addTransition(Slide().apply {
                                slideEdge = Gravity.START
                                duration = 250
                            })
                        })
                        isExpand = !isExpand
                        if (isExpand) {
                            holder.mTvGpaTitle.gone()
                            holder.mTabLayout.visible()
                            holder.mIvGpaArrow.setImageResource(R.drawable.grades_ic_arrow_left)
                        } else {
                            holder.mTvGpaTitle.visible()
                            holder.mTabLayout.gone()
                            holder.mIvGpaArrow.setImageResource(R.drawable.grades_ic_arrow_right)
                        }

                    }
                    holder.fl.setOnClickListener {
                        changeScene.invoke()
                    }
                    changeGPAGraph(holder)
                    holder.mTabLayout.addOnTabSelectedListener(object :
                        TabLayout.OnTabSelectedListener {
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
                                    holder.mTvGpaTitle.text =
                                        context.getString(R.string.grades_bottom_sheet_gpa_rv_average_gpa)
                                }
                                GRADES -> {
                                    holder.mTvGpaTitle.text =
                                        context.getString(R.string.grades_bottom_sheet_gpa_rv_average_grades)

                                }
                                RANK -> {
                                    holder.mTvGpaTitle.text =
                                        context.getString(R.string.grades_bottom_sheet_gpa_rv_average_rank)
                                }
                            }
                            changeGPAGraph(holder)
                            if (isExpand) {
                                changeScene.invoke()
                            }
                        }

                    })
                    holder.mTvACreditNumber.text = gpaData.aCredit
                    holder.mTvBCreditNumber.text = gpaData.bCredit
                    holder.mTvACreditNumber.setOnClickListener {
                        clickACredit()
                    }
                    holder.mTvACredit.setOnClickListener {
                        clickACredit()
                    }
                }
            }
            is TermGrade -> {
                if (holder is NormalTopVH) {
                    val termGrade = dataMirror[position] as TermGrade
                    holder.mTvAverageCreditNum.text = termGrade.gpa
                    holder.mTvAverageGradesNum.text = termGrade.grade
                    //rank可能会出现为0，因为教务在线还没有排名
                    if (termGrade.rank == "0") {
                        holder.mTvAverageRankNum.text = "--"
                    } else {
                        holder.mTvAverageRankNum.text = termGrade.rank
                    }
                    val term = termGrade.term
                    val termOfYear = term.substring(4)
                    val year = term.substring(0, 4).toInt()
                    if (termOfYear == "1") {
                        //说明是上学期
                        holder.mTvTerm.text = "${year}-${year + 1}第一学期"
                    } else {
                        holder.mTvTerm.text = "${year}-${year + 1}第二学期"
                    }
                }
            }
            is NormalBottom -> {
                if (holder is NormalBottomVH) {
                    val bean = dataMirror[position] as NormalBottom
                    if (bean.termGrade.isClose()) {
                        holder.mTvGradesDetail.text = "查看各科成绩"
                    } else {
                        holder.mTvGradesDetail.text = "收起各科成绩"
                    }
                    holder.itemView.setOnClickListener(object : NoDoubleClickListener() {
                        override fun onNoDoubleClick(v: View) {
                            if (bean.termGrade.isClose()) {
                                bean.termGrade.status = TermGrade.EXPAND
                                holder.mTvGradesDetail.text = "收起各科成绩"
                            } else {
                                bean.termGrade.status = TermGrade.CLOSE
                                holder.mTvGradesDetail.text = "查看各科成绩"
                            }
                            refreshArrayList()
                        }
                    })
                }
            }
            is SingleGrade -> {
                if (holder is ChildVH) {
                    val single = dataMirror[position] as SingleGrade
                    holder.mTvGradeCourse.text = single.className
                    holder.mTvGradeScore.text = single.grade
                    holder.mTvGradeProperty.text = single.classType
                }
            }
        }
    }

}