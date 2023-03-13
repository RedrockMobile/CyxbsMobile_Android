package com.mredrock.cyxbs.food.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.food.R
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * Create by bangbangp on 2023/2/4 20:03
 * Email:1678921845@qq.com
 * Description:
 */
class FoodMainRvAdapter(
    private val data: List<String>,
    private val click: (state: Boolean, position: Int) -> Unit
) :
    RecyclerView.Adapter<FoodMainRvAdapter.InnerHolder>() {

    inner class InnerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCl = view.findViewById<ConstraintLayout>(R.id.food_rv_item_cl)
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
        holder.mBtn.text = data[position]
    }

    override fun getItemCount(): Int = data.size
}