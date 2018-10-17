package com.mredrock.cyxbs.grades.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.grades.R
import com.mredrock.cyxbs.grades.bean.Exam
import com.mredrock.cyxbs.grades.utils.baseRv.BaseAdapter
import com.mredrock.cyxbs.grades.utils.baseRv.BaseHolder
import kotlinx.android.synthetic.main.grades_item_exam.view.*
import org.jetbrains.anko.imageResource

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class ExamAdapter(val context: Context,
                  data: MutableList<Exam>?,
                  layoutIds: IntArray)
    : BaseAdapter<Exam>(context, data, layoutIds) {

    override fun getItemType(position: Int): Int {
        getData()?.let { it ->
            return if (position != it.size) NORMAL else FOOTER
        }
        return FOOTER
    }

    @SuppressLint("SetTextI18n")
    override fun onBinds(holder: BaseHolder, t: MutableList<Exam>?, position: Int, viewType: Int) {
        when (viewType) {
            NORMAL -> {
                if (position == 0)
                    holder.itemView.setPadding(0, 50, 0, 0)

                t?.get(position).let { it ->

                    if (it?.week?.toInt() != 0) {
                        val schoolCalendar = SchoolCalendar(it?.week!!.toInt(), it.weekday!!.toInt())
                        val isSuccess = examDataHelper.tryModifyData(it)
                        if (isSuccess) {
                            holder.itemView.tv_exam_day_of_week.text = String.format("%s周周%s", it.week, it.chineseWeekday)
                            holder.itemView.tv_exam_day_of_month.text = String.format("%d", schoolCalendar.day)
                            holder.itemView.tv_exam_month.text = String.format("%d月", schoolCalendar.month)
                        } else {
                            holder.itemView.tv_exam_day_of_week.text = String.format("%s周周%s", "-", "-")
                            holder.itemView.tv_exam_day_of_month.text = String.format("%s", "-")
                            holder.itemView.tv_exam_month.text = String.format("%s月", "")
                        }
                    }

                    holder.itemView.tv_exam_name.text = it.course
                    var seat = if (it.seat?.toInt() != 0) it.seat else "--"

                    if (seat!!.length < 2) seat = "0$seat"
                    seat = "${seat}号"

                    holder.itemView.tv_exam_location.text = "${it?.classroom}场$seat"

                    if (it.begin_time != null && it.end_time != null) {
                        holder.itemView.tv_exam_time.text = String.format("%s - %s", it.begin_time, it.end_time)
                    } else {
                        holder.itemView.tv_exam_time.text = it.time
                    }

                    holder.itemView.iv_exam_circle.imageResource = circleId[position % 3]
                }

                if (position != getDataSize() - 1) {
                    holder.itemView.view_exam_line.visible()
                }
            }
        }
    }

    companion object {
        private val examDataHelper: ExamDataHelper = ExamDataHelper()
        private val circleId = intArrayOf(R.drawable.grades_circle_pink, R.drawable.grades_circle_blue, R.drawable.grades_circle_yellow)
    }


    private class ExamDataHelper {
        private val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
        private val numChineseArray = arrayOf("一", "二", "三", "四", "五", "六", "七")

        fun tryModifyData(exam: Exam): Boolean {
            if (exam.weekday.equals("0") || exam.week.equals("0")) {
                return false
            } else {
                toChineseNum(exam)
            }
            return true
        }

        private fun toChineseNum(exam: Exam) {
            for (i in numArray.indices) {
                if (exam.weekday.equals(numArray[i])) {
                    // 这里不应该改变原有的值
                    //it.weekday = numChineseArray[i];
                    exam.chineseWeekday = numChineseArray[i]
                }
            }
        }
    }
}