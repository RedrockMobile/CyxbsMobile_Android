package com.mredrock.cyxbs.discover.grades.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseAdapter
import com.mredrock.cyxbs.discover.grades.utils.baseRv.BaseHolder
import kotlinx.android.synthetic.main.grades_item_exam.view.*
import java.util.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class ExamAdapter(val context: Context,
                  data: MutableList<Exam>?,
                  layoutIds: IntArray)
    : BaseAdapter<Exam>(context, data, layoutIds) {
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

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    @SuppressLint("SetTextI18n")
    override fun onBinds(holder: BaseHolder, list: MutableList<Exam>?, position: Int, viewType: Int) {
        list ?: return
        when (viewType) {
            NORMAL -> {
                //减一是因为要删除Header的占位
                list[position - 1].let { it ->
                    it.let {
                        if (it.week?.toInt() != 0) {
                            val drawableTime = ResourcesCompat.getDrawable(context.resources, R.drawable.grades_time, null)
                            drawableTime?.setBounds(0, 0, 30, 30)
                            holder.itemView.tv_exam_month.setCompoundDrawables(drawableTime, null, null, null)

                            val drawableLocation = ResourcesCompat.getDrawable(context.resources, R.drawable.grades_exam_location, null)
                            drawableLocation?.setBounds(0, 0, 30, 30)
                            holder.itemView.tv_exam_location.setCompoundDrawables(drawableLocation, null, null, null)
                            var schoolCalendar: SchoolCalendar? = null
                            it.week?.let { week ->
                                it.weekday?.let { weekday ->
                                    schoolCalendar = SchoolCalendar(week.toInt(), weekday.toInt())
                                }
                            }
                            val isSuccess = examDataHelper.tryModifyData(it)
                            if (isSuccess) {
                                val distance = getDay(it.date)

                                when {
                                    distance == 0 -> {
                                        holder.itemView.grades_distance.text = "今天考试"
                                        holder.itemView.grades_distance.setTextColor(Color.parseColor("#3A39D3"))
                                    }
                                    distance > 0 -> {
                                        holder.itemView.grades_distance.text = "还剩${distance}天考试"
                                        holder.itemView.grades_distance.setTextColor(Color.parseColor("#3A39D3"))
                                    }
                                    distance == -1 -> {
                                        holder.itemView.grades_distance.text = "考试已结束"
                                        holder.itemView.grades_distance.setTextColor(Color.parseColor("#2A4E84"))
                                    }
                                    distance == -2 -> {
                                        holder.itemView.grades_distance.text = "暂时无法获取到考试天数"
                                        holder.itemView.grades_distance.setTextColor(Color.parseColor("#2A4E84"))
                                    }
                                }
                                holder.itemView.tv_exam_kind.text = it.type ?: ""
                                it.week?.let { week ->
                                    holder.itemView.tv_exam_day_of_week.text = String.format("%s周周%s", getChineseWeek(week.toInt() - 1), it.chineseWeekday
                                            ?: "--")
                                }
                                holder.itemView.tv_exam_day_of_month.text = String.format("%s" + "号", schoolCalendar?.day
                                        ?: "--")
                                holder.itemView.tv_exam_month.text = String.format("%s月", schoolCalendar?.month
                                        ?: "--")
                            } else {
                                holder.itemView.tv_exam_day_of_week.text = String.format("%s周周%s", "-", "-")
                                holder.itemView.tv_exam_day_of_month.text = String.format("%s" + "号", "-")
                                holder.itemView.tv_exam_month.text = String.format("%s月", "-")
                            }
                        }

                        holder.itemView.tv_exam_name.text = it.course
                        var seat = it.seat ?: "--"

                        if (seat.length < 2) seat = "0$seat"
                        seat = "${seat}号"

                        holder.itemView.tv_exam_location.text = "${it.classroom}场"
                        holder.itemView.tv_exam_location_number.text = seat

                        if (it.begin_time != null && it.end_time != null) {
                            holder.itemView.tv_exam_time.text = String.format("%s - %s", it.begin_time, it.end_time)
                        } else {
                            holder.itemView.tv_exam_time.text = it.time
                        }
                    }
                }

                //要去除header的占位
                if (position - 1 == list.size - 1) {
                    holder.itemView.iv_exam_circle.setLineVisible(false)
                } else {
                    holder.itemView.iv_exam_circle.setLineVisible(true)
                }
            }
        }
    }

    /**
     * 需要做一个防止数组越界的处理
     */
    private fun getChineseWeek(week: Int): String {
        val array = listOf("第一", "第二", "第三", "第四", "第五", "第六", "第七", "第八",
                "第九", "第十", "十一", "十二", "十三", "十四", "十五",
                "十六", "十七", "十八", "十九", "二十", "二十一",
                "二十二", "二十三", "二十四", "二十五", "二十六", "二十七")
        if (week in 0..26) {
            return array[week]
        } else {
            return "第--周"
        }

    }

    companion object {
        private val examDataHelper: ExamDataHelper = ExamDataHelper()
    }


    private class ExamDataHelper {
        private val numArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
        private val numChineseArray = arrayOf("一", "二", "三", "四", "五", "六", "日")

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

    //传入一个2019-11-24格式的时间
    private fun getDay(time: String?): Int {
        time?.let { time ->
            val now = Date()
            val examDate = Calendar.getInstance()

            val array = time.split("-")//分割为2019，11，24
            //由于后端补考接口date字段可能会返回"月日"，因此需要做个处理防止NumberFormatException
            for (item in array) {
                if (!item.isDigitsOnly()) {
                    return -2
                }
            }
            examDate.set(array[0].toInt(), array[1].toInt() - 1, array[2].toInt())
            val diff = examDate.time.time - now.time
            val day = 1000 * 24 * 60 * 60
            return if (diff >= 0) {
                (diff / day).toInt()
            } else {
                -1//-1表示考试已结束
            }
        }
        return -2//-2表示time非法
    }
}