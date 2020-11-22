package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R


class CircleLabelAdapter(val context: Context, private val mList: MutableList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class CircleLabel(view: View) : RecyclerView.ViewHolder(view) {
        val tv_dynamic_label: TextView = view.findViewById(R.id.tv_dynamic_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleLabel {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.qa_recycler_item_dynamic_label, parent, false)
        return CircleLabel(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setList(list: List<String>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CircleLabel
        viewHolder.tv_dynamic_label.text = mList[position]
        viewHolder.tv_dynamic_label.setOnSingleClickListener {
            it.setBackgroundResource(R.drawable.qa_shape_common_label_text_view_background2)
        }
    }


}