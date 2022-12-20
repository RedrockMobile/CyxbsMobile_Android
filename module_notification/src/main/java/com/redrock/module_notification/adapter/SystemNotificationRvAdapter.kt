package com.redrock.module_notification.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.bean.SystemMsgBean
import com.redrock.module_notification.ui.activity.WebActivity
import com.redrock.module_notification.util.Date
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.DeleteDialog

/**
 * Author by OkAndGreat
 * Date on 2022/4/30 15:09.
 */
class SystemNotificationRvAdapter(
    private var list: List<SystemMsgBean>,
    private var viewmodel: NotificationViewModel,
    private var context: Context,
    private var activity: FragmentActivity,
    private var rv: RecyclerView,
    val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_SECOND = 2

        const val CHANGE_DOT_STATUS = 3
    }

    private var multiDeleteAdapter: SysNotifyMultiDeleteRvAdapter? = null

    inner class BlankHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemSysNotificationClMain: View by lazy { itemView.findViewById(R.id.item_sys_notification_cl_main) }
        val itemSysNotificationIvRedDot: ImageView by lazy { itemView.findViewById(R.id.item_sys_notification_iv_red_dot) }
        val itemSysNotificationTvTitle: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_title) }
        val itemSysNotificationTvContent: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_content) }
        val itemSysNotificationTvTime: TextView by lazy { itemView.findViewById(R.id.item_sys_notification_tv_time) }
        val itemNotificationRlHomeDelete: View by lazy { itemView.findViewById(R.id.item_notification_rl_home_delete) }
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
                        .inflate(R.layout.notification_item_sys, parent, false)
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
        val notification_system_btn_negative = activity.findViewById<Button>(R.id.notification_system_btn_negative)
        val notification_system_btn_positive = activity.findViewById<Button>(R.id.notification_system_btn_positive)
        if (holder is InnerHolder) {
            val data = list[position]
            if (data.has_read) holder.itemSysNotificationIvRedDot.visibility = View.INVISIBLE
            else holder.itemSysNotificationIvRedDot.visibility = View.VISIBLE
            holder.itemSysNotificationTvTitle.text = data.title
            holder.itemSysNotificationTvContent.text = data.content
            holder.itemSysNotificationTvTime.text = Date.getUnExactTime(data.publish_time)
            holder.itemSysNotificationClMain.setOnClickListener {
                viewmodel.changeMsgStatus(
                    ChangeReadStatusToBean(listOf(list[position].id.toString())),
                    position
                )
                viewmodel.changeMsgReadStatus.value = position
                WebActivity.startWebViewActivity(data.redirect_url, context)
            }
            holder.itemSysNotificationClMain.setOnLongClickListener {
                notification_system_btn_negative.visibleWithAnim()
                notification_system_btn_positive.visibleWithAnim()
                multiDeleteAdapter = SysNotifyMultiDeleteRvAdapter(list)
                rv.adapter = multiDeleteAdapter
                viewmodel.changePopUpWindowClickableStatus(false)
                true
            }
            holder.itemNotificationRlHomeDelete.setOnSingleClickListener {
                if (data.has_read) {
                    onDelete(holder.adapterPosition)
                } else {
                    DeleteDialog.show(
                        activity.supportFragmentManager,
                        null,
                        tips = "这条消息未读\n确认删除此消息吗",
                        onPositiveClick = {
                            onDelete(holder.adapterPosition)
                            dismiss()
                        },
                        onNegativeClick = {
                            dismiss()
                        }
                    )
                }
            }
        }
    }

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
                            holder.itemSysNotificationIvRedDot.visibility = View.INVISIBLE
                        else
                            holder.itemSysNotificationIvRedDot.visibility = View.VISIBLE
                    }
                }
            }

            //当payload为空时 我们进行full bind 不这样写会出错
            //public void onBindViewHolder(@NonNull VH holder, int position,
            //                @NonNull List<Object> payloads) {
            //            onBindViewHolder(holder, position);
            //        }
            else super.onBindViewHolder(holder, position, payloads)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeAllData(newList: List<SystemMsgBean>) {
        list = newList
        notifyDataSetChanged()
    }

    fun setNewList(data: ArrayList<SystemMsgBean>) {
        list = data
    }

    //获取多选状态时的选择的item的相关信息
    fun getSelectedItemInfos() = multiDeleteAdapter?.selectedItemInfos

    override fun getItemCount(): Int = list.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_SECOND
        else TYPE_ONE
    }
    
    private fun View.visibleWithAnim(duration: Long = 300) {
        if(visibility == View.VISIBLE) return
    
        visibility = View.VISIBLE
        val anim = ValueAnimator
            .ofFloat(0f, 1f)
            .setDuration(duration)
    
        anim.addUpdateListener {
            val curVal = it.animatedValue as Float
            this.alpha = curVal
        }
    
        anim.start()
    }
}