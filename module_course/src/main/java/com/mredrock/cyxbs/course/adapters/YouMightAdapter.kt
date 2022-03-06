package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import kotlinx.android.synthetic.main.course_auto_warp_you_might_item.view.*
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

class YouMightAdapter(val data:List<String>, private val editText: EditText) : RedRockAutoWarpView.Adapter() {

    override fun getItemId(): Int {
        return R.layout.course_auto_warp_you_might_item
    }

    override fun initItem(item: View, position: Int) {
        item.apply {
            tv_might.text = data[position]
            tv_might.setOnSingleClickListener {
                editText.text.clear()
                editText.setText(data[position], TextView.BufferType.EDITABLE)
                editText.setSelection(data[position].length)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}
