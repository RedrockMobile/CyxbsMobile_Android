package com.mredrock.cyxbs.discover.othercourse

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.discover.othercourse.room.History
import kotlinx.android.synthetic.main.othercourse_discover_other_course_history_item.view.*

class AutoWrapAdapter(val list: List<History>, private val onTextClickListener: (History) -> Unit, private val onDeleteClickListener: (Int) -> Unit) : RedRockAutoWarpView.Adapter() {
    override fun getItemId(): Int = R.layout.othercourse_discover_other_course_history_item


    override fun getItemCount(): Int = list.size

    override fun initItem(item: View, position: Int) {
        if (position >= list.size) return
        (item.tv_search_history as AppCompatTextView).apply {
            text = list[position].info
            setOnClickListener {
                onTextClickListener(list[position])
            }
        }
        (item.iv_search_history_delete as AppCompatImageView).setOnClickListener {
            onDeleteClickListener(list[position].historyId)
        }
    }


}