package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.YouMightView
import kotlinx.android.synthetic.main.course_recycle_you_might_item.view.*

class YouMightAdapter(private val editText:EditText): YouMightView.Adapter() {

    //仿造数据
    val data = listOf("自习", "值班", "会议","自习", "值班", "会议","自习", "值班", "会议","自习", "值班", "会议","自习", "值班", "会议","自习", "值班", "会议","自习", "值班", "会议")

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
