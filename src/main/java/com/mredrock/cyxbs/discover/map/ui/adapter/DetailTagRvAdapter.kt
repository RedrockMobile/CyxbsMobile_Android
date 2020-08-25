package com.mredrock.cyxbs.discover.map.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.discover.map.R
import kotlinx.android.synthetic.main.map_recycle_item_detail_tag.view.*

class DetailTagRvAdapter(val context: Context, private val mList: MutableList<String>) : RecyclerView.Adapter<DetailTagRvAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tag: TextView = view.map_tv_recycle_item_detail_tag.apply { pressToZoomOut() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.map_recycle_item_detail_tag, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag.text = mList[position]

    }


    fun setList(list: List<String>) {
        mList.clear()
        mList.addAll(list)


    }

}