package com.mredrock.cyxbs.course.adapters

import android.view.View
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.WeekSelectDialog
import kotlinx.android.synthetic.main.course_week_selected_item_auto_warp.view.*
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 * @author Jovines
 * @create 2020-01-23 1:53 PM
 *
 * 描述:
 *   周数选择结果的适配器
 */
class WeekSelectedAdapter(private val weekSelectedList: List<Int>, private val weekSelectDialog: WeekSelectDialog) : RedRockAutoWarpView.Adapter() {

    override fun getItemId(): Int {
        return R.layout.course_week_selected_item_auto_warp
    }

    override fun getItemCount(): Int {
        if (weekSelectedList.isEmpty() || weekSelectedList.size == 21) {
            return 1
        }
        return weekSelectedList.size
    }

    override fun initItem(item: View, position: Int) {
        item.course_tv_week_item.apply {
            text = if (weekSelectedList.isEmpty()) "请选择周数" else if (weekSelectedList.size == 21) "整学期" else "第${weekSelectedList[position]}周"
            setOnSingleClickListener {
                if (!weekSelectDialog.isShowing) {
                    weekSelectDialog.show()
                }
            }
        }
    }
}