package com.cyxbsmobile_single.module_todo.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import kotlinx.android.synthetic.main.todo_rv_item_choose_year.view.*

/**
 * Author: RayleighZ
 * Time: 2021-09-17 9:08
 */
class ChooseYearAdapter(
    private val stringArray: ArrayList<String>,
    private val onItemClick: (year: Int) -> Unit
) :
    SimpleTextAdapter(stringArray, R.layout.todo_rv_item_choose_year) {

    var curSelPosition: Int = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //默认第一年展示
        holder.itemView.apply {
            todo_tv_year.apply {
                pressToZoomOut()
                text = stringArray[position]
                background =
                    if (position == curSelPosition)
                        ContextCompat.getDrawable(context, R.drawable.todo_shape_clicked_choose_years)
                    else
                        ContextCompat.getDrawable(context, R.drawable.todo_shape_default_choose_years)

                setOnClickListener {
                    //更改背景颜色
                    curSelPosition = position
                    //回到年份
                    onItemClick(Integer.parseInt(stringArray[position]))
                    notifyDataSetChanged()
                }
            }
        }
    }
}