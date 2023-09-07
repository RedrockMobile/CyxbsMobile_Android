package com.redrock.module_notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.string
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.SentItineraryMsgBean
import com.redrock.module_notification.util.Date
import com.redrock.module_notification.util.myGetColor

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/22
 * @Description: 针对已发送的行程是否取消提醒的item的两种状态，
 * 并未使用不同的ViewHold展示，而是根据当前行程的提醒状态分开渲染内容
 *
 */
class SentItineraryNotificationRvAdapter(
    private val onCancelReminderClick: ((itineraryId: Int, index: Int) -> Unit)? = null
) : ListAdapter<SentItineraryMsgBean, SentItineraryNotificationRvAdapter.VH>(sentItineraryDiffUtil) {
    companion object {
        private val sentItineraryDiffUtil: DiffUtil.ItemCallback<SentItineraryMsgBean> =
            object : DiffUtil.ItemCallback<SentItineraryMsgBean>() {
                override fun areItemsTheSame(
                    oldItem: SentItineraryMsgBean, newItem: SentItineraryMsgBean
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: SentItineraryMsgBean, newItem: SentItineraryMsgBean
                ): Boolean {
                    return oldItem.title == newItem.title &&
                            oldItem.content == newItem.content &&
                            oldItem.updateTime == newItem.updateTime &&
                            oldItem.hasCancel == newItem.hasCancel &&
                            oldItem.hasStart == newItem.hasStart
                }

            }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notification_itinerary_tv_sent_title)
        val content: TextView = itemView.findViewById(R.id.notification_itinerary_tv_sent_content)
        val startTime: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_sent_generate_time)
        val unCancelDot: ImageView =
            itemView.findViewById(R.id.notification_itinerary_iv_status_uncancel_dot)
        val startStatusHint: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_start_status_hint)
        val canceledHint: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_canceled_hint)
        val cancelReminder: LinearLayout =
            itemView.findViewById(R.id.notification_itinerary_ll_cancel_reminder)

        init {
            onCancelReminderClick?.apply {
                cancelReminder.setOnSingleClickListener(2000) {
                    // 取消该行程的提醒
                    val position = bindingAdapterPosition
                    this.invoke(getItem(position).id, position)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item_itiner_sent_normal, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val itemData = getItem(position)
        if (itemData.hasCancel) {
            item2canceledStatus(holder)
        }
        bindItemData(holder, itemData)
    }

    private fun bindItemData(holder: VH, data: SentItineraryMsgBean) {
        holder.apply {
            title.text = String.format(R.string.notification_itinerary_item_rawTitle.string, data.title)
            content.text = data.content
            startTime.text = Date.getItineraryUpdateTime(data.publishTime)
            if (data.hasStart) {
                // 按照逻辑，行程已开始之后再取消提醒没有意义，故移除点击取消提醒的回调
                cancelReminder.setOnClickListener(null)
                startStatusHint.setText(R.string.notification_hasStarted)
            }
        }
    }

    private fun item2canceledStatus(holder: VH) {
        holder.cancelReminder.apply {
            // 移除点击回调，节省资源
            setOnClickListener(null)
            gone()
        }

        holder.unCancelDot.gone()
        holder.canceledHint.visible()
        changeTextColor(holder)

    }

    private fun changeTextColor(holder: VH) {
        holder.title.setTextColor(myGetColor(R.color.notification_itinerary_item_canceled_title_text))
        holder.content.setTextColor(myGetColor(R.color.notification_itinerary_item_canceled_content_text))
        holder.startTime.setTextColor(myGetColor(R.color.notification_itinerary_item_canceled_generate_time_text))
        holder.startStatusHint.setTextColor(myGetColor(R.color.notification_itinerary_item_canceled_start_status_hint_text))
        holder.canceledHint.setTextColor(myGetColor(R.color.notification_itinerary_item_canceled_hint_text))
    }
}