package com.mredrock.cyxbs.ufield.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.bean.ItemActivityBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  description :用于活动布告栏的Rv adapter
 *  author : lytMoon
 *  date : 2023/8/20 20:40
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class UfieldRvAdapter :
    ListAdapter<ItemActivityBean.ItemAll, UfieldRvAdapter.RvAllActViewHolder>((RvAllDiffCallback())) {


    /**
     * 点击活动的回调
     */
    private var mActivityClick: ((Int) -> Unit)? = null

    fun setOnActivityClick(listener: (Int) -> Unit) {
        mActivityClick = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvAllActViewHolder {
        return RvAllActViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_all, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvAllActViewHolder, position: Int) {
        val itemData = getItem(position)

        holder.bind(itemData)
    }


    inner class RvAllActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_activity_pic)
        private val actName: TextView =
            itemView.findViewById<TextView?>(R.id.uField_activity_name).apply {
//            isSelected=true
            }
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_activity_isGoing)
        private val actType: TextView = itemView.findViewById(R.id.uField_activity_type)
        private val actTime: TextView = itemView.findViewById(R.id.uField_activity_time)

        init {
            itemView.setOnClickListener {
                mActivityClick?.invoke(absoluteAdapterPosition)
            }
        }

        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: ItemActivityBean.ItemAll) {
            actName.text = itemData.activityTitle
            actType.text = itemData.activityType
            actTime.text = timeFormat(itemData.activityStartAt)
            actPic.setImageFromUrl(itemData.activityCoverUrl)

            when (itemData.ended) {
                false -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_on)
                else -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_off)
            }


            when (itemData.activityType) {
                "culture" -> actType.text = "文娱活动"
                "sports" -> actType.text = "体育活动"
                else -> actType.text = "教育活动"
            }

        }


        /**
         * 加工时间戳,把时间戳转化为“年.月.日”格式
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun timeFormat(time: Long): String {
            return Instant
                .ofEpochSecond(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
        }
    }

    class RvAllDiffCallback : DiffUtil.ItemCallback<ItemActivityBean.ItemAll>() {
        override fun areItemsTheSame(
            oldItem: ItemActivityBean.ItemAll,
            newItem: ItemActivityBean.ItemAll
        ): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(
            oldItem: ItemActivityBean.ItemAll,
            newItem: ItemActivityBean.ItemAll
        ): Boolean {
            return oldItem.activityId == newItem.activityId && oldItem.activityCreator == newItem.activityCreator && oldItem.activityStartAt == newItem.activityStartAt
        }

    }


}