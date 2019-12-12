package com.mredrock.cyxbs.discover.pages.morefunction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider
import kotlinx.android.synthetic.main.discover_more_function_recycler_item.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

/**
 * @author zixuan
 * 2019/11/20
 */
open class MoreFunctionRvAdapter(private val functions: List<MoreFunctionProvider.Function>) : RecyclerView.Adapter<MoreFunctionRvAdapter.MoreFunctionViewHolder>() {
    private val LONG_TYPE = 0
    private val LEFT_TYPE = 1
    private val RIGHT_TYPE = 2
    private var isSettingMode = false
    private val chosenList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreFunctionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.discover_more_function_recycler_item, parent, false)
        when (viewType) {
            0 -> {
                view.layoutParams.height = parent.context.dip(255)
                (view.layoutParams as RecyclerView.LayoutParams).rightMargin = parent.context.dip(16)
            }
            1 -> (view.layoutParams as RecyclerView.LayoutParams).leftMargin = parent.context.dip(16)
            2 -> (view.layoutParams as RecyclerView.LayoutParams).rightMargin = parent.context.dip(16)
        }

        return MoreFunctionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return functions.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MoreFunctionViewHolder, position: Int) {
        holder.itemView.iv_discover_recycler_item.setImageResource(functions[position].resource)
        holder.itemView.tv_discover_recycler_item_title.setText(functions[position].title)
        holder.itemView.tv_discover_recycler_item_detail.setText(functions[position].detail)
        if (chosenList.contains(position)) {
            holder.itemView.cl_discover_more_function.backgroundResource = R.drawable.discover_shape_more_function_recycler_item_chosen
        } else {
            holder.itemView.cl_discover_more_function.backgroundResource = R.drawable.discover_shape_more_function_recycler_item
        }
        holder.itemView.setOnClickListener {
            if (isSettingMode) {
                if (!chosenList.contains(position)) {
                    if (chosenList.size > 2) {
                        Toast.makeText(holder.itemView.context, "只能选择三个哦", Toast.LENGTH_SHORT).show()
                    } else {
                        chosenList.add(position)
                        notifyItemChanged(position)
                    }
                } else {
                    chosenList.remove(position)
                    notifyItemChanged(position)
                }
            } else {
                functions[position].activityStarter.startActivity()
            }
        }
    }

    fun getIsSettingMode() = isSettingMode
    fun changeSettingMode() {
        this.isSettingMode = !isSettingMode
    }

    fun getChosenList(): List<Int> {
        return chosenList
    }

    fun resetChosenList() {
        chosenList.removeAll(chosenList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 1) return LONG_TYPE
        return if (position % 2 == 0) LEFT_TYPE
        else RIGHT_TYPE
    }

    class MoreFunctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}