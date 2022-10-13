package com.redrock.module_notification.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.ActiveMsgBean
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.ui.activity.WebActivity
import com.redrock.module_notification.util.Date
import com.redrock.module_notification.viewmodel.NotificationViewModel
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Author by OkAndGreat
 * Date on 2022/4/30 17:09.
 */
class ActivityNotificationRvAdapter(
    private var list: List<ActiveMsgBean>,
    private var viewmodel: NotificationViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ONE = 1
        const val TYPE_SECOND = 2

        const val CHANGE_DOT_STATUS = 3
    }

    inner class BlankHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemActivityNotificationIvRedDot: ImageView by lazy { itemView.findViewById(R.id.item_activity_notification_iv_red_dot) }
        val itemActivityNotificationTvTitle: TextView by lazy { itemView.findViewById(R.id.item_activity_notification_tv_title) }
        val itemActivityNotificationIvHead: CircleImageView by lazy { itemView.findViewById(R.id.item_activity_notification_iv_head) }
        val itemActivityNotificationTvPublisher: TextView by lazy { itemView.findViewById(R.id.item_activity_notification_tv_publisher) }
        val itemActivityNotificationTvPublishTime: TextView by lazy { itemView.findViewById(R.id.item_activity_notification_tv_publish_time) }
        val itemActivityNotificationTvContent: TextView by lazy { itemView.findViewById(R.id.item_activity_notification_tv_content) }
        val itemActivityNotificationIvDetail: ImageView by lazy { itemView.findViewById(R.id.item_activity_notification_iv_detail) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        var holder: RecyclerView.ViewHolder? = null

        when (viewType) {
            TYPE_ONE -> {
                holder = InnerHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_item_activity, parent, false)
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
            if (data.has_read) holder.itemActivityNotificationIvRedDot.visibility = View.INVISIBLE
            else holder.itemActivityNotificationIvRedDot.visibility = View.VISIBLE
            holder.itemActivityNotificationTvTitle.text = data.title
            holder.itemActivityNotificationTvPublisher.text = data.user_name
            holder.itemActivityNotificationTvPublishTime.text =
                Date.getUnExactTime(data.publish_time)
            holder.itemActivityNotificationTvContent.text = data.content
            holder.itemActivityNotificationIvHead.setImageFromUrl(data.user_head_url)
            holder.itemActivityNotificationIvDetail.setImageFromUrl(data.pic_url)
            holder.itemView.setOnSingleClickListener {
                viewmodel.changeMsgStatus(
                    ChangeReadStatusToBean(listOf(list[position].id.toString())),
                    //我们约定position >= 0 为系统通知的消息 <=0 为活动通知的消息
                    -position
                )
                viewmodel.changeMsgReadStatus.value = -position
                WebActivity.startWebViewActivity(data.redirect_url, holder.itemView.context)
            }
        }
    }

    //如果我们仅需要改变红点的状态
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (holder is InnerHolder)
            if (payloads.isNotEmpty()) {
                when (payloads[0]) {
                    CHANGE_DOT_STATUS -> {
                        val data = list[position]
                        if (data.has_read)
                            holder.itemActivityNotificationIvRedDot.visibility = View.INVISIBLE
                        else
                            holder.itemActivityNotificationIvRedDot.visibility = View.VISIBLE
                    }
                }
            }

        //当payload为空时 我们进行full bind 不这样写会出错
        else super.onBindViewHolder(holder, position, payloads)
    }

    fun setNewList(newList: List<ActiveMsgBean>) {
        list = newList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAllData(newList: List<ActiveMsgBean>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_SECOND
        else TYPE_ONE
    }

}