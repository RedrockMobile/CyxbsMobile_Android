package com.redrock.module_notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.redrock.module_notification.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Author by OkAndGreat
 * Date on 2022/4/30 17:09.
 * 不要格式化
 */
class ActivityNotificationRvAdapter :
    RecyclerView.Adapter<ActivityNotificationRvAdapter.InnerHolder>() {
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemActivityNotificationIvRedDot: ImageView by lazy { itemView.findViewById<ImageView>(R.id.item_activity_notification_iv_red_dot) }
        val itemActivityNotificationTvTitle: TextView by lazy { itemView.findViewById<TextView>(R.id.item_activity_notification_tv_title) }
        val itemActivityNotificationIvHead: CircleImageView by lazy { itemView.findViewById<CircleImageView>(
            R.id.item_activity_notification_iv_head) }
        val itemActivityNotificationTvPublisher: TextView by lazy { itemView.findViewById<TextView>(
            R.id.item_activity_notification_tv_publisher) }
        val itemActivityNotificationTvPublishTime: TextView by lazy { itemView.findViewById<TextView>(
            R.id.item_activity_notification_tv_publish_time) }
        val itemActivityNotificationTvContent: TextView by lazy { itemView.findViewById<TextView>(R.id.item_activity_notification_tv_content) }
        val itemActivityNotificationIvDetail: ImageView by lazy { itemView.findViewById<ImageView>(R.id.item_activity_notification_iv_detail) }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InnerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_notification,parent,false)
        val holder = InnerHolder(view)

        return holder
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
    }

    override fun getItemCount(): Int = 66


}