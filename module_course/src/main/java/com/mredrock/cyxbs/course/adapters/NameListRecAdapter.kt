package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.TextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R

/**
 * Created by wenyipeng on 2019/3/10.
 * desc：
 */
class NameListRecAdapter(private val mPeople: List<String>) : RedRockAutoWarpView.Adapter() {

    override fun getItemCount(): Int = mPeople.size

    override fun getItemId(): Int? {
        return R.layout.course_name_list_rec_item
    }

    override fun initItem(item: View, position: Int) {
        item.findViewById<TextView>(R.id.tv_name).apply {
            text = mPeople[position]
        }
    }

}