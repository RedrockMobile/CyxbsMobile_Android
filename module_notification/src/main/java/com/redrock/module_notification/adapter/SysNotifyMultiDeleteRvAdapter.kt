package com.redrock.module_notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.SelectedItem
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.util.Date

/**
 * Author by OkAndGreat
 * Date on 2022/5/7 16:37.
 *
 */
class SysNotifyMultiDeleteRvAdapter(private var list: List<SystemMsgBean>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_SECOND = 2
    }

    val selectedItemInfos = SelectedItem(ArrayList(), ArrayList(), ArrayList())

    inner class BlankHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var isChecked = false
        val itemSysNotificationSelect: ImageView by lazy { itemView.findViewById(R.id.item_sys_notification_select) }
        val itemSysNotificationTvTitle: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_title) }
        val itemSysNotificationTvContent: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_content) }
        val itemSysNotificationTvTime: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_time) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var holder: RecyclerView.ViewHolder? = null

        when (viewType) {
            TYPE_ONE -> {
                holder = InnerHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_item_sys_multi_delete, parent, false)
                )
            }
            TYPE_SECOND -> {
                holder = BlankHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_blank_layout, parent, false)
                )
            }
        }

        return holder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InnerHolder) {
            val data = list[position]
            holder.itemSysNotificationTvTitle.text = data.title
            holder.itemSysNotificationTvContent.text = data.content
            holder.itemSysNotificationTvTime.text = Date.getUnExactTime(data.publish_time)
            holder.itemSysNotificationSelect.setOnSingleClickListener {
                holder.isChecked = !holder.isChecked
                if (holder.isChecked) {
                    selectedItemInfos.ids.add(data.id.toString())
                    selectedItemInfos.position.add(position)
                    if (!data.has_read)
                        selectedItemInfos.reads.add(data.has_read)
                } else {
                    selectedItemInfos.ids.remove(data.id.toString())
                    selectedItemInfos.position.remove(position)
                    if (!data.has_read)
                        selectedItemInfos.reads.remove(data.has_read)
                }
                if (holder.isChecked) holder.itemSysNotificationSelect.setImageResource(R.drawable.notification_ic_select)
                else holder.itemSysNotificationSelect.setImageResource(R.drawable.notification_ic_not_select)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_SECOND
        else TYPE_ONE
    }

    override fun getItemCount(): Int = list.size + 1
}