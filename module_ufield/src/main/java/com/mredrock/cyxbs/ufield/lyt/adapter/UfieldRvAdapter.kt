package com.mredrock.cyxbs.ufield.lyt.adapter

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
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
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


    class RvAllActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_activity_pic)
        private val actName: TextView = itemView.findViewById<TextView?>(R.id.uField_activity_name).apply { isSelected=true }
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_activity_isGoing)
        private val actType: TextView = itemView.findViewById(R.id.uField_activity_type)
        private val actTime: TextView = itemView.findViewById(R.id.uField_activity_time)


        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: ItemActivityBean.ItemAll) {
            actName.text = itemData.activity_title
            actType.text = itemData.activity_type
            actTime.text = timeFormat(itemData.activity_start_at)
       //     actPic.setImageFromUrl(itemData.activity_cover_url)

            when (itemData.ended) {
                false -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_on)
                else -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_off)
            }

            when (itemData.activity_type) {
                "culture" -> actType.text = "文娱活动"
                "sports" -> actType.text = "体育活动"
                else -> actType.text = "教育活动"
            }
            /**
             * 满足中心比例的缩放，没用setImageFromUrl
             */
            Glide.with(itemView.context)
                .load(itemData.activity_cover_url)
                .centerCrop() //中心比例的缩放（如果效果不稳定请删除）
                .into(actPic)

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

        /**
         * 通过数据类中的一个特征值来比较
         */
        override fun areContentsTheSame(
            oldItem: ItemActivityBean.ItemAll,
            newItem: ItemActivityBean.ItemAll
        ): Boolean {
            return oldItem.activity_id == newItem.activity_id
        }

    }


}