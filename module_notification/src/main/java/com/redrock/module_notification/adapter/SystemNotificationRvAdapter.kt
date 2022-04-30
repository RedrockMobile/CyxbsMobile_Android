package com.redrock.module_notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.redrock.module_notification.R

/**
 * Author by OkAndGreat
 * Date on 2022/4/30 15:09.
 * 不要格式化
 */
class SystemNotificationRvAdapter : RecyclerView.Adapter<SystemNotificationRvAdapter.InnerHolder>() {
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemSysNotificationIvRedDot: ImageView by lazy { itemView.findViewById<ImageView>(R.id.item_sys_notification_iv_red_dot) }
        val itemSysNotificationTvTitle: TextView by lazy { itemView.findViewById<TextView>(R.id.item_sys_notification_tv_title) }
        val itemSysNotificationTvContent: TextView by lazy { itemView.findViewById<TextView>(R.id.item_sys_notification_tv_content) }
        val itemSysNotificationTvTime: TextView by lazy { itemView.findViewById<TextView>(R.id.item_sys_notification_tv_time) }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sys_notification,parent,false)
        val holder = InnerHolder(view)

        return holder
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
    }

    override fun getItemCount(): Int  = 66
}