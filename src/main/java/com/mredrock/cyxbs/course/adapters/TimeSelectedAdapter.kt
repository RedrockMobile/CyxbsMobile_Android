package com.mredrock.cyxbs.course.adapters

import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import kotlinx.android.synthetic.main.course_time_select_auto_warp_item.view.*

/**
 * @author jon
 * @create 2020-01-24 10:34 AM
 *
 * 描述:
 *   事务添加页面事务在一周中的时间的适配器
 */
class TimeSelectedAdapter(private val timeList: MutableList<Pair<Int, Int>>, val editAffairActivity: EditAffairActivity) : RedRockAutoWarpView.Adapter() {

    private val timeArray = editAffairActivity.mEditAffairViewModel.timeArray
    private val dayOfWeekArray = editAffairActivity.mEditAffairViewModel.dayOfWeekArray
    private val mTimeSelectDialogFragment = editAffairActivity.mTimeSelectDialogFragment


    override fun getItemId(position: Int): Int {
        return if (position == timeList.size) {
            return R.layout.course_affair_time_add
        } else R.layout.course_time_select_auto_warp_item
    }

    override fun getItemCount(): Int {
        return timeList.size + 1
    }

    override fun initItem(item: View, position: Int) {
        if (position == timeList.size) {
            item.setOnClickListener {
                if (!mTimeSelectDialogFragment.isShowing) {
                    mTimeSelectDialogFragment.show()
                }
            }
        } else {
            val time = "${dayOfWeekArray[timeList[position].second]} ${timeArray[timeList[position].first]}"
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