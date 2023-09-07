package com.redrock.module_notification.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.config.route.UFIELD_DETAIL_ENTRY
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.UfieldMsgBean
import com.redrock.module_notification.util.formatNumberToTime
import com.redrock.module_notification.viewmodel.NotificationViewModel

class ActivityUfieldRVAdapter(
    private val context: Fragment,
    private var viewmodel: NotificationViewModel
) :
    ListAdapter<UfieldMsgBean, ActivityUfieldRVAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<UfieldMsgBean>() {
            override fun areContentsTheSame(
                oldItem: UfieldMsgBean,
                newItem: UfieldMsgBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(
                oldItem: UfieldMsgBean,
                newItem: UfieldMsgBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val clickDetail: LinearLayout
        val notificationTitle: TextView
        val redDot: ImageView
        val yesNo: TextView
        val realName: TextView
        val fakePlace: TextView
        val realPlace: TextView
        val time: TextView

        init {
            view.run {
                clickDetail = findViewById(R.id.item_activity_ll_jump)
                redDot = findViewById(R.id.item_activity_notification_iv_red_dot)
                yesNo = findViewById(R.id.item_activity_notification_tv_yes_no)
                realName = findViewById(R.id.item_activity_notification_tv_real_name)
                fakePlace = findViewById(R.id.item_activity_notification_tv_place)
                realPlace = findViewById(R.id.item_activity_notification_tv_real_place)
                time = findViewById(R.id.item_notification_time)
                notificationTitle = findViewById(R.id.item_activity_notification_tv_title)
            }
            clickDetail.setOnSingleClickListener {
                    viewmodel.changeUfieldMsgStatus(currentList[absoluteAdapterPosition].messageId)
                    getItem(absoluteAdapterPosition).run {
                        ARouter.getInstance().build(UFIELD_DETAIL_ENTRY)
                            .withInt(
                                "actID",
                                currentList[absoluteAdapterPosition].activityInfo.activityId
                            )
                            .navigation(context.activity?.application?.applicationContext)
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item_ufield_activity, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            realName.text = currentList[position].activityInfo.activityTitle
            realPlace.text = currentList[position].activityInfo.activityPlace
            time.text = formatNumberToTime(currentList[position].activityInfo.createdAt)
            if (currentList[position].clicked) {
                redDot.visibility = View.GONE
            } else {
                redDot.visibility = View.VISIBLE
            }
            when (currentList[position].messageType) {
                "examine_report_reject" -> {
                    notificationTitle.text = "审核通知"
                    yesNo.text = "未通过"
                    yesNo.visibility = View.VISIBLE
                    fakePlace.text = "驳回原因:"
                    realPlace.text = currentList[position].rejectReason
                    yesNo.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.notification_textview_no_pass
                        )
                    )
                    yesNo.setBackgroundResource(R.drawable.notification_ic_bk_no_pass)
                }

                "examine_report_pass" -> {
                    notificationTitle.text = "审核通知"
                    yesNo.text = "通过"
                    yesNo.visibility = View.VISIBLE
                    fakePlace.text = "活动地点:"
                    realPlace.text = currentList[position].activityInfo.activityPlace

                    yesNo.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.notification_textview_pass
                        )
                    )
                    yesNo.setBackgroundResource(R.drawable.notification_ic_bk_pass)
                }

                "activity_report" -> {
                    notificationTitle.text = "活动通知"
                    yesNo.visibility = View.GONE
                    fakePlace.text="活动地点:"
                    realPlace.text=currentList[position].activityInfo.activityPlace
                    yesNo.visibility = View.GONE
                }
            }
        }
    }
}