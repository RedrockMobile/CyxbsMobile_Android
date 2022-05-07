package com.redrock.module_notification.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
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

    private val lottieProgress = 0.39f//点击同意用户协议时的动画的时间

    inner class BlankHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var isChecked = false
        val itemSysNotificationSelect: LottieAnimationView by lazy { itemView.findViewById(R.id.item_sys_notification_select) }
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
                        .inflate(R.layout.item_sys_multi_delete, parent, false)
                )
            }
            TYPE_SECOND -> {
                holder = BlankHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.blank_layout, parent, false)
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
                holder.itemSysNotificationSelect.playAnimation()
                holder.isChecked = !holder.isChecked
                if (holder.isChecked) {
                    selectedItemInfos.ids.add(data.id.toString())
                    selectedItemInfos.positions.add(position)
                    if (!data.has_read)
                        selectedItemInfos.reads.add(data.has_read)
                } else {
                    selectedItemInfos.ids.remove(data.id.toString())
                    selectedItemInfos.positions.remove(position)
                    if (!data.has_read)
                        selectedItemInfos.reads.remove(data.has_read)
                }
            }
            holder.itemSysNotificationSelect.addAnimatorUpdateListener {
                if (it.animatedFraction == 1f && holder.isChecked) {
                    holder.itemSysNotificationSelect.pauseAnimation()
                } else if (it.animatedFraction >= lottieProgress && it.animatedFraction != 1f && !holder.isChecked) {
                    holder.itemSysNotificationSelect.pauseAnimation()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeAllData(newList: List<SystemMsgBean>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_SECOND
        else TYPE_ONE
    }

    override fun getItemCount(): Int = list.size + 1
}