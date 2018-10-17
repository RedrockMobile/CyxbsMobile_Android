package com.mredrock.cyxbs.grades.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
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
class ReExamAdapter(val context: Context,
                    data: MutableList<Exam>?,
                    layoutIds: IntArray) : BaseAdapter<Exam>(context, data, layoutIds) {
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
                        val month: String = it?.date?.split("月")!![0]
                        val day: String = it.date?.split("月")!![1].split("日")[0]
                        holder.itemView.tv_exam_day_of_month.text = day
                        holder.itemView.tv_exam_month.text = "${month}月"
                    }
                    holder.itemView.tv_exam_name.text = it.course?.split("-")?.get(1)

                    holder.itemView.tv_exam_day_of_week.text = it.time

                    var seat = if (it.seat?.toInt() != 0) it.seat else "--"

                    if (seat!!.length < 2) seat = "0$seat"
                    seat = "${seat}号"

                    holder.itemView.tv_exam_location.text = "${it.classroom}场$seat"

                    holder.itemView.tv_exam_time.text = ""

                    holder.itemView.iv_exam_circle.imageResource = circleId[position % 3]

                }
                if (position != getDataSize() - 1) {
                    holder.itemView.view_exam_line.visible()
                }
            }
        }
    }

    companion object {
        private val circleId = intArrayOf(R.drawable.grades_circle_pink, R.drawable.grades_circle_blue, R.drawable.grades_circle_yellow)
    }
}