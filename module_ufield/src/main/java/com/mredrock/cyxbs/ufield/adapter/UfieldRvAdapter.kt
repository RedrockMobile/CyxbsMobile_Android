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
import com.mredrock.cyxbs.ufield.helper.formatNumberToTime

/**
 *  description :用于活动布告栏的Rv adapter
 *  author : lytMoon
 *  date : 2023/8/20 20:40
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class UfieldRvAdapter :
    ListAdapter<ItemActivityBean.ItemAll, UfieldRvAdapter.RvAllActViewHolder>(object :
        DiffUtil.ItemCallback<ItemActivityBean.ItemAll>() {
        override fun areItemsTheSame(oldItem: ItemActivityBean.ItemAll, newItem: ItemActivityBean.ItemAll) = oldItem.activityId == newItem.activityId
        override fun areContentsTheSame(oldItem: ItemActivityBean.ItemAll, newItem: ItemActivityBean.ItemAll) = oldItem == newItem
    }) {


    /**
     * 点击活动的回调
     */
    private var mActivityClick: ((Int) -> Unit)? = null

    fun setOnActivityClick(listener: (Int) -> Unit) {
        mActivityClick = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=RvAllActViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_all, parent, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvAllActViewHolder, position: Int) {
        val itemData = getItem(position)

        holder.bind(itemData)
    }


    inner class RvAllActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_activity_pic)
        private val actName: TextView = itemView.findViewById(R.id.uField_activity_name)
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_activity_isGoing)
        private val actType: TextView = itemView.findViewById(R.id.uField_activity_type)
        private val actTime: TextView = itemView.findViewById(R.id.uField_activity_time)

        init {
            itemView.setOnClickListener {
                mActivityClick?.invoke(absoluteAdapterPosition)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: ItemActivityBean.ItemAll) {
            actName.text = itemData.activityTitle
            actType.text = itemData.activityType
            actTime.text = formatNumberToTime(itemData.activityStartAt)
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

    }


}