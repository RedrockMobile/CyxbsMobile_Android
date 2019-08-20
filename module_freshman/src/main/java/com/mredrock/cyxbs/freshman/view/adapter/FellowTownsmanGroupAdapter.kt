package com.mredrock.cyxbs.freshman.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.FellowTownsmanGroupText
import com.mredrock.cyxbs.freshman.config.INTENT_DATA
import com.mredrock.cyxbs.freshman.config.INTENT_NAME
import com.mredrock.cyxbs.freshman.view.activity.CopyQQNumberActivity

/**
 * Create by yuanbing
 * on 2019/8/4
 */
class FellowTownsmanGroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mFellowTownsmanGroup: List<FellowTownsmanGroupText> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_online_communication_group, parent, false)
            FellowTownsmanGroupViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_online_communication_group_footer, parent, false)
            FooterFellowTownsmanGroupViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount - 1) 0 else 1
    }

    override fun getItemCount() = mFellowTownsmanGroup.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val fellowTownsmanGroup = mFellowTownsmanGroup[position]
            holder.itemView.setBackgroundResource(R.drawable.freshman_recycle_item_common_bg)
            (holder as FellowTownsmanGroupViewHolder).mText.text = fellowTownsmanGroup.name

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, CopyQQNumberActivity::class.java)
                intent.putExtra(INTENT_NAME, fellowTownsmanGroup.name)
                intent.putExtra(INTENT_DATA, fellowTownsmanGroup.data)
                it.context.startActivity(intent)
            }
        }
    }
}

class FellowTownsmanGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mText: TextView = view.findViewById(R.id.tv_recycle_item_online_communication_group)
}

class FooterFellowTownsmanGroupViewHolder(view:View) : RecyclerView.ViewHolder(view)