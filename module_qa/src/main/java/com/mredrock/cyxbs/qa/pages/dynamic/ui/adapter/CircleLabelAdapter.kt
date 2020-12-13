package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic


class CircleLabelAdapter(val context: Context, private val mList: MutableList<Topic>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class CircleLabel(view: View) : RecyclerView.ViewHolder(view) {
        val tv_dynamic_label: TextView = view.findViewById(R.id.tv_dynamic_label)
    }

    private val positionSet = HashSet<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleLabel {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.qa_recycler_item_dynamic_label, parent, false)
        return CircleLabel(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setList(list: List<Topic>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    var onLabelClickListener: ((String) -> Unit)? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CircleLabel
        viewHolder.tv_dynamic_label.text = mList[position].topicName
        //目前简单实现了一个单选逻辑，逻辑上有点没绕过来，后面在优化吧
        if (positionSet.contains(position)) {
            viewHolder.tv_dynamic_label.setBackgroundResource(R.drawable.qa_shape_common_label_text_view_background2)

            positionSet.clear()
        } else {
            viewHolder.tv_dynamic_label.setBackgroundResource(R.drawable.qa_shape_common_label_text_view_background)
        }
        viewHolder.tv_dynamic_label.setOnSingleClickListener {
            onLabelClickListener?.invoke(mList[position].topicName)
            positionSet.add(position)
            notifyDataSetChanged()
        }
    }
}