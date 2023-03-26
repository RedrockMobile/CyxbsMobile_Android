package com.mredrock.cyxbs.food.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.food.R

/**
 * Create by bangbangp on 2023/2/4 20:03
 * Email:1678921845@qq.com
 * Description:
 */
class FoodMainRvAdapter(
    private val click: (state: Boolean, position: Int) -> Unit
) :
    ListAdapter<String, FoodMainRvAdapter.InnerHolder>(object :DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class InnerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mBtn = view.findViewById<Button>(R.id.food_rv_item_btn)
            .apply {
                this.setOnClickListener {
                    click(!isSelected, absoluteAdapterPosition)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        return InnerHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.food_rv_item_main, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        holder.mBtn.text = getItem(position)
    }

}