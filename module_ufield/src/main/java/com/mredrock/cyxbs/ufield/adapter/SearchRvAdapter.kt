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
 *  description : 负责展示搜索活动数据的Rv adapter
 *  author : lytMoon
 *  date : 2023/8/22 14:49
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class SearchRvAdapter :
    ListAdapter<ItemActivityBean.ItemAll, SearchRvAdapter.RvSearchActViewHolder>(object :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RvSearchActViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ufield_item_rv_search, parent, false)
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvSearchActViewHolder, position: Int) {
        val itemData = getItem(position)
        holder.bind(itemData)
    }


    inner class RvSearchActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_search_act_image)
        private val actName: TextView = itemView.findViewById(R.id.Ufield_search_act_ame)
        private val actHint: TextView = itemView.findViewById(R.id.Ufield_search_act_what)
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_search_isGoing)
        private val actTime: TextView = itemView.findViewById(R.id.uField_search_ddl)

        init {
            itemView.setOnClickListener {
                mActivityClick?.invoke(absoluteAdapterPosition)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: ItemActivityBean.ItemAll) {
            actName.text = itemData.activityTitle
            actHint.text = itemData.activityDetail.trimStart()
            actTime.text = formatNumberToTime(itemData.activityStartAt)
            actPic.setImageFromUrl(itemData.activityCoverUrl)
            when (itemData.ended) {
                false -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_on)
                else -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_off)
            }
        }


    }


}