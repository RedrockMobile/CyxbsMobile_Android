package com.mredrock.cyxbs.course.adapters

import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.TimeSelectDialog
import kotlinx.android.synthetic.main.course_time_select_auto_warp_item.view.*

/**
 * @author Jovines
 * @create 2020-01-24 10:34 AM
 *
 * 描述:
 *   事务添加页面事务在一周中的时间的适配器
 */
class TimeSelectedAdapter(private val timeList: MutableList<Pair<Int, Int>>, private val mTimeSelectDialog: TimeSelectDialog) : RedRockAutoWarpView.Adapter() {

    private val timeArray = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val dayOfWeekArray = listOf("一二节课", "三四节课", "五六节课", "七八节课", "九十节课", "十一十二节课")

    override fun getItemId(position: Int): Int {
        return if (timeList.size == 0) {
            if (position == 0) {
                R.layout.course_time_select_auto_warp_item
            } else {
                R.layout.course_affair_time_add
            }
        } else {
            if (position == timeList.size) {
                R.layout.course_affair_time_add
            } else R.layout.course_time_select_auto_warp_item
        }
    }

    override fun getItemCount(): Int {
        return if (timeList.size == 0) {
            return 2
        } else timeList.size + 1
    }

    override fun initItem(item: View, position: Int) {
        //如果用户没有选择周数，则显示请选择周数，并添加显示按钮
        if (timeList.size == 0) {
            if (position == 0) {
                item.course_time.text = context.resources.getString(R.string.course_time_select)
                item.affair_iv_cancel.visibility = View.GONE
            } else {
                item.setOnClickListener {
                    if (!mTimeSelectDialog.isShowing) {
                        mTimeSelectDialog.show()
                    }
                }
            }
        } else {
            if (position == timeList.size) {
                item.setOnClickListener {
                    if (!mTimeSelectDialog.isShowing) {
                        mTimeSelectDialog.show()
                    }
                }
            } else {
                val time = "${dayOfWeekArray[timeList[position].first]} ${timeArray[timeList[position].second]}"
                item.apply {
                    course_time.text = time
                    affair_iv_cancel.setOnClickListener {
                        timeList.removeAt(position)
                        view.refreshData()
                    }
                }
            }
        }
    }
}