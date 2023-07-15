package com.mredrock.cyxbs.discover.grades.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.DashGapLine
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import java.util.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
class ExamAdapter(val data: MutableList<Exam>) : RecyclerView.Adapter<ExamAdapter.ExamVH>() {
    /**
     * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
     * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
     * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
     * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
     * stackoverflow上的回答：
     * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
     */
    open class ExamVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    class HeadVH(itemView: View) : ExamVH(itemView)

    class OtherVH(itemView: View) : ExamVH(itemView) {
        val mTvExamMonth: TextView = itemView.findViewById(R.id.tv_exam_month)
        val mTvExamLocation:TextView = itemView.findViewById(R.id.tv_exam_location)
        val mTvGradesDistance:TextView = itemView.findViewById(R.id.grades_distance)
        val mTvExamKind:TextView = itemView.findViewById(R.id.tv_exam_kind)
        val mTvExamDayOfWeek:TextView = itemView.findViewById(R.id.tv_exam_day_of_week)
        val mTvExamDayOfMonth:TextView = itemView.findViewById(R.id.tv_exam_day_of_month)
        val mTvExamName:TextView = itemView.findViewById(R.id.tv_exam_name)
        val mTvExamLocationNumber:TextView = itemView.findViewById(R.id.tv_exam_location_number)
        val mTvExamTime:TextView = itemView.findViewById(R.id.tv_exam_time)
        val mIvExamCircle:DashGapLine = itemView.findViewById(R.id.iv_exam_circle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamVH {
        return when (viewType) {
            HeadVH::class.hashCode() -> HeadVH(
                LayoutInflater.from(parent.context).inflate(R.layout.grades_item_head, parent, false)
            )
            OtherVH::class.hashCode() -> OtherVH(
                LayoutInflater.from(parent.context).inflate(R.layout.grades_item_exam, parent, false)
            )
            else -> error("")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HeadVH::class.hashCode() else OtherVH::class.hashCode()
    }

    override fun onBindViewHolder(holder: ExamVH, position: Int) {
        val context = holder.itemView.context
        if (holder is OtherVH) {
            data[position-1].let { it ->
                it.let {
                    if (it.week?.toInt() != 0) {
                        val drawableTime = ResourcesCompat.getDrawable(context.resources, R.drawable.grades_time, null)
                        drawableTime?.setBounds(0, 0, 30, 30)
                        holder.mTvExamMonth.setCompoundDrawables(drawableTime, null, null, null)

                        val drawableLocation = ResourcesCompat.getDrawable(context.resources, R.drawable.grades_exam_location, null)
                        drawableLocation?.setBounds(0, 0, 30, 30)
                        holder.mTvExamLocation.setCompoundDrawables(drawableLocation, null, null, null)
                        var schoolCalendar: SchoolCalendar? = null
                        it.week?.let { week ->
                            it.weekday?.let { weekday ->
                                schoolCalendar = SchoolCalendar(week.toInt(), weekday.toInt())
                            }
                        }
                        val isSuccess = ExamDataHelper.tryModifyData(it)
                        if (isSuccess) {
                            val distance = getDay(it.date)

                            when {
                                distance == 0 -> {
                                    holder.mTvGradesDistance.text = "今天考试"
                                    holder.mTvGradesDistance.setTextColor(Color.parseColor("#3A39D3"))
                                }
                                distance > 0 -> {
                                    holder.mTvGradesDistance.text = "还剩${distance}天考试"
                                    holder.mTvGradesDistance.setTextColor(Color.parseColor("#3A39D3"))
                                }
                                distance == -1 -> {
                                    holder.mTvGradesDistance.text = "考试已结束"
                                    holder.mTvGradesDistance.setTextColor(Color.parseColor("#2A4E84"))
                                }
                                distance == -2 -> {
                                    holder.mTvGradesDistance.text = "暂时无法获取到考试天数"
                                    holder.mTvGradesDistance.setTextColor(Color.parseColor("#2A4E84"))
                                }
                            }
                            holder.mTvExamKind.text = it.type ?: ""
                            it.week?.let { week ->
                                holder.mTvExamDayOfWeek.text = String.format("%s周周%s", getChineseWeek(week.toInt() - 1), it.chineseWeekday
                                    ?: "--")
                            }
                            holder.mTvExamDayOfMonth.text = String.format("%s" + "号", schoolCalendar?.day
                                ?: "--")
                            holder.mTvExamMonth.text = String.format("%s月", schoolCalendar?.month
                                ?: "--")
                        } else {
                            holder.mTvExamDayOfWeek.text = String.format("%s周周%s", "-", "-")
                            holder.mTvExamDayOfMonth.text = String.format("%s" + "号", "-")
                            holder.mTvExamMonth.text = String.format("%s月", "-")
                        }
                    }

                    holder.mTvExamName.text = it.course
                    var seat = it.seat ?: "--"

                    if (seat.length < 2) seat = "0$seat"
                    seat = "${seat}号"

                    holder.mTvExamLocation.text = "${it.classroom}场"
                    holder.mTvExamLocationNumber.text = seat

                    if (it.begin_time != null && it.end_time != null) {
                        holder.mTvExamTime.text = String.format("%s - %s", it.begin_time, it.end_time)
                    } else {
                        holder.mTvExamTime.text = it.time
                    }
                }
            }

            //要去除header的占位
            if (position - 1 == data.size - 1) {
                holder.mIvExamCircle.setLineVisible(false)
            } else {
                holder.mIvExamCircle.setLineVisible(true)
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


    private object ExamDataHelper {
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

    override fun getItemCount(): Int {
        return data.size + 1 // 加一是为了头布局
    }
}