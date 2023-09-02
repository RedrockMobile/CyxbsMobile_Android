package com.redrock.module_notification.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.redrock.module_notification.bean.ReceivedItineraryMsgBean
import com.redrock.module_notification.util.Date

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/22
 * @Description: 针对接收到的行程是否已被取消提醒写了两个不同的item，使用不同的ViewHold展示，而是根据当前行程的提醒状态分开渲染内容
 *
 */
class ReceivedItineraryNotificationRvAdapter(
    private val onAddAffairClick: ((index: Int) -> Unit)? = null
) : ListAdapter<ReceivedItineraryMsgBean, ReceivedItineraryNotificationRvAdapter.BaseVH>(
    sentItineraryDiffUtil
) {
    companion object {
        private val sentItineraryDiffUtil: DiffUtil.ItemCallback<ReceivedItineraryMsgBean> =
            object : DiffUtil.ItemCallback<ReceivedItineraryMsgBean>() {
                override fun areItemsTheSame(
                    oldItem: ReceivedItineraryMsgBean, newItem: ReceivedItineraryMsgBean
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: ReceivedItineraryMsgBean, newItem: ReceivedItineraryMsgBean
                ): Boolean {
                    return oldItem.title == newItem.title &&
                            oldItem.content == newItem.content &&
                            oldItem.updateTime == newItem.updateTime &&
                            oldItem.hasStart == newItem.hasStart &&
                            oldItem.hasAdd == newItem.hasAdd &&
                            oldItem.hasCancel == newItem.hasCancel
                }

            }
    }

    sealed class BaseVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class NormalVH(itemView: View) : BaseVH(itemView) {
        val title: TextView = itemView.findViewById(R.id.notification_itinerary_tv_received_title)
        val content: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_content)
        val generateTime: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_generate_time)

        //        val unCancelDot: ImageView = itemView.findViewById(R.id.notification_itinerary_iv_status_uncancel_dot)
        val startStatusHint: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_start_status_hint)
        val addAffairArea: LinearLayout =
            itemView.findViewById(R.id.notification_itinerary_ll_add_affair)
        val addedHint: TextView = itemView.findViewById(R.id.notification_itinerary_tv_added_hint)

        init {
            onAddAffairClick?.apply {
                addAffairArea.setOnSingleClickListener(2000) {
                    // 添加该行程的到课表的事务
                    val position = bindingAdapterPosition
                    this.invoke(position)
                }
            }

        }
    }

    inner class CanceledVH(itemView: View) : BaseVH(itemView) {
        val rawTitle: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_title)

        //        val newTitle: TextView = itemView.findViewById(R.id.notification_itinerary_tv_received_title_new)
        val rawContent: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_content)
        val newContent: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_content_new)
        val generateTime: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_generate_time)
        val updateTime: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_received_update_time_new)
        val startStatusHint: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_start_status_hint)
        val newStartStatusHint: TextView =
            itemView.findViewById(R.id.notification_itinerary_tv_start_status_hint_new)
        val addAffairArea: LinearLayout =
            itemView.findViewById(R.id.notification_itinerary_ll_add_affair)
        val addedHint: TextView = itemView.findViewById(R.id.notification_itinerary_tv_added_hint)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).hasCancel) CanceledVH::class.hashCode()
        else NormalVH::class.hashCode()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            NormalVH::class.hashCode() -> NormalVH(
                inflater.inflate(
                    R.layout.notification_item_itiner_received_normal,
                    parent,
                    false
                )
            )

            CanceledVH::class.hashCode() -> CanceledVH(
                inflater.inflate(
                    R.layout.notification_item_itiner_received_cancel,
                    parent,
                    false
                )
            )

            else -> error("ReceivedItineraryNotificationRvAdapter：未知的item ViewHolder")
        }
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        val itemData = getItem(position)
        bindItemData(holder, itemData)
    }

    private fun bindItemData(holder: BaseVH, data: ReceivedItineraryMsgBean) {
        when (holder) {
            is NormalVH -> {
                holder.title.text =
                    String.format(R.string.notification_itinerary_item_rawTitle.string, data.title)
                holder.content.text = data.content
                holder.generateTime.text = Date.getItineraryUpdateTime(data.publishTime)
                if (data.hasStart) holder.startStatusHint.setText(R.string.notification_hasStarted)
                if (data.hasAdd) item2addedStatus(holder)
            }

            is CanceledVH -> {
                holder.rawTitle.text =
                    String.format(R.string.notification_itinerary_item_rawTitle.string, data.title)
                holder.rawContent.text = data.content
                holder.newContent.text =
                    String.format(R.string.notification_itinerary_item_cancelContent.string, data.content)
                holder.generateTime.text = Date.getItineraryUpdateTime(data.publishTime)
                holder.updateTime.text = Date.getItineraryUpdateTime(data.updateTime)
                if (data.hasStart) holder.apply {
                    startStatusHint.setText(R.string.notification_hasStarted)
                    newStartStatusHint.setText(R.string.notification_hasStarted)
                }
                if (data.hasAdd) item2addedStatus(holder)
            }
        }

    }

    private fun item2addedStatus(holder: BaseVH) {
        holder.apply {
            when (this) {
                is NormalVH -> {
                    addAffairArea.apply {
                        // 移除点击回调，节省资源
                        setOnClickListener(null)
                        gone()
                    }
                    addedHint.visible()
                }

                is CanceledVH -> {
                    addAffairArea.gone()
                    addedHint.visible()
                }
            }
        }
    }
}