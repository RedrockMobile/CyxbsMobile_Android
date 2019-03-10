package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.widget.TextView
import com.mredrock.cyxbs.course.R

/**
 * Created by wenyipeng on 2019/3/10.
 * desc：
 */
class NameListRecAdapter(context: Context, private val mPeople: List<String>) : BaseRecAdapter(context) {


    override fun getThePositionLayoutId(position: Int): Int =
            R.layout.course_name_list_rec_item

    override fun getItemCount(): Int = mPeople.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_name).apply {
            mPeople[position]
        }
    }
}