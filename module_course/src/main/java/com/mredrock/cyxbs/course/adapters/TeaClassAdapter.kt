package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.TextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R

/**
 * @author Jovines
 * @create 2020-02-11 7:05 PM
 *
 * 描述:
 *   课程详细信息弹出dialog下面显示的班级
 */
class TeaClassAdapter(private val dataList: List<String>) : RedRockAutoWarpView.Adapter() {

    override fun getItemId(): Int? {
        return R.layout.course_week_selected_item_auto_warp
    }

    override fun getItemCount(): Int = dataList.size

    override fun initItem(item: View, position: Int) {
        (item as TextView).text = dataList[position]
    }
}