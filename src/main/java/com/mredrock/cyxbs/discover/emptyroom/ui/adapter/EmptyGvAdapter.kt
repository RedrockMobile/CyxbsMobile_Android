package com.mredrock.cyxbs.discover.emptyroom.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mredrock.cyxbs.discover.emptyroom.R

/**
 * Created by Cynthia on 2018/9/19
 */
class EmptyGvAdapter(private val context: Context, private val data: List<String>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convert = convertView
        if (convert == null) {
            convert = LayoutInflater.from(context).inflate(R.layout.emptyroom_grid_item, parent, false)
        }
        val room = convert?.findViewById<TextView>(R.id.tv_grid_item_empty_room)
        val raw = data[position]
        room?.text = raw
        return convert
    }

    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = data.size

}