package com.cyxbsmobile_single.module_todo.adapter

import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyxbsmobile_single.module_todo.R
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut

/**
 * Author: RayleighZ
 * Time: 2021-09-17 9:08
 */
class ChooseYearAdapter(
    val stringArray: ArrayList<String>,
    private val onItemClick: (year: Int) -> Unit
) : SimpleTextAdapter(stringArray, R.layout.todo_rv_item_choose_year) {

    var curSelPosition: Int = 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //默认第一年展示
        holder.itemView.apply {
            val todo_tv_year = findViewById<AppCompatTextView>(R.id.todo_tv_year)
            todo_tv_year.apply {

                if (position == curSelPosition) {
                    background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.todo_shape_clicked_choose_years
                        )
                    todo_tv_year.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.todo_add_todo_choose_year_text_chosen_color
                        )
                    )
                    todo_tv_year.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.todo_shape_default_choose_years
                        )
                    todo_tv_year.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.todo_add_todo_choose_year_text_default_color
                        )
                    )
                    todo_tv_year.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                }

                pressToZoomOut()
                text = stringArray[position]
                setOnClickListener {
                    //更改背景颜色
                    curSelPosition = position
                    //回到年份
                    onItemClick(curSelPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }
}