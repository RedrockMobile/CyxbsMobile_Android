package com.mredrock.cyxbs.discover.othercourse

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.discover.othercourse.room.History

class AutoWrapAdapter(val list: List<History>, private val onItemClickListener: (String) -> Unit) : RedRockAutoWarpView.Adapter() {
    override fun getItemId(): Int = R.layout.discover_other_course_history_item


    override fun getItemCount(): Int = list.size

    override fun initItem(item: View, position: Int) {
        if (position >= list.size) return
        (item as AppCompatTextView).text = list[position].info
        item.setOnClickListener {
            onItemClickListener.invoke(list[position].info)
        }
    }

}