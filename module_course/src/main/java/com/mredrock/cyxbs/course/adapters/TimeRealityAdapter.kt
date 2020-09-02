package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.course.R
import kotlinx.android.synthetic.main.course_grid_course_time_item.view.*
import java.util.*

/**
 * @author  Jovines
 * @date  2020/4/5 17:21
 * description：
 */
class TimeRealityAdapter(private val dayOfWeekList: Array<String>, private val dayOfMonthList: Array<String>,val position:Int?) : BaseAdapter() {

    private val calendar: Calendar = Calendar.getInstance()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = if (convertView !== null) convertView.tag as ViewHolder else ViewHolder(parent.context, parent)
        viewHolder.view.apply {
            course_tv_week_day.text = dayOfWeekList[position]
            course_tv_day_of_month.text = dayOfMonthList[position]
            val dayOfWeek = if (calendar[Calendar.DAY_OF_WEEK] == 1) 6 else calendar[Calendar.DAY_OF_WEEK] - 2
            val color = if (dayOfWeek == position&&this@TimeRealityAdapter.position!=null)
                ContextCompat.getColor(parent.context, R.color.common_discover_academic_online_colors)
            else
                ContextCompat.getColor(parent.context, R.color.common_level_one_font_color)
            course_tv_week_day.setTextColor(color)
            course_tv_day_of_month.setTextColor(color)
        }
        return viewHolder.view
    }

    override fun getItem(position: Int) = null

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = 7

    class ViewHolder(context: Context, parent: ViewGroup) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.course_grid_course_time_item, parent, false)

        init {
            view.tag = this
        }
    }
}