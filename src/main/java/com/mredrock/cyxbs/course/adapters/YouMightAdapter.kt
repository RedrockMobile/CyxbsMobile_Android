package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import kotlinx.android.synthetic.main.course_recycle_you_might_item.view.*

class YouMightAdapter(private val editText:EditText): RedRockAutoWarpView.Adapter() {

    //仿造数据
    val data = listOf("开会开会开会开会开会开会开会开会开会开会开会开会","啦啦啦啦","嘿咻嘿咻黑","开会开会开会\n开会开会开会","开会","开会","开会","自习")
    override fun getItemId(): Int {
        return R.layout.course_recycle_you_might_item
    }

    override fun initItem(item: View, position: Int) {
        item.apply {
            tv_might.text = data[position]
            tv_might.setOnClickListener {
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
