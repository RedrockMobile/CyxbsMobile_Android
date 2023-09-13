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
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.bean.DoneBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:08
 *  description :负责展示已经审核过的活动Rv adapter
 *  version ： 1.0
 */
class DoneRvAdapter :
    ListAdapter<DoneBean, DoneRvAdapter.RvDoneViewHolder>((RvDoneDiffCallback())) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvDoneViewHolder {
        return RvDoneViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_done, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvDoneViewHolder, position: Int) {
        val itemData = getItem(position)

        holder.bind(itemData)
    }



    /**
     * 点击按钮的回调
     */

    private var mClick: ((Int) -> Unit)? = null


    fun setOnItemClick(listener: (Int) -> Unit){
        mClick = listener
    }



    inner class RvDoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actName: TextView = itemView.findViewById(R.id.uField_done_activity_name)
        private val actTime: TextView = itemView.findViewById(R.id.uField_done_activity_time)
        private val actType: TextView = itemView.findViewById(R.id.uField_done_activity_type)
        private val actAuthor: TextView = itemView.findViewById(R.id.uField_done_activity_author)
        private val actImage: ImageView = itemView.findViewById(R.id.uField_todo_activity_isPass)


        init {
            itemView.setOnClickListener {
                mClick?.invoke(absoluteAdapterPosition)
            }
        }


        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: DoneBean) {
            actName.text = itemData.activityTitle
            actTime.text = timeFormat(itemData.activityCreateTimestamp)
            actType.text = itemData.activityType
            actAuthor.text = itemData.activityCreator

            when (itemData.state) {
                "rejected" -> actImage.setImageResource(R.drawable.ufield_ic_reject)
                "published" -> actImage.setImageResource(R.drawable.ufield_ic_pass)
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
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        }
    }

    class RvDoneDiffCallback : DiffUtil.ItemCallback<DoneBean>() {

        override fun areItemsTheSame(oldItem: DoneBean, newItem: DoneBean): Boolean {
            return oldItem == newItem
        }

        /**
         * 通过数据类中的一部分特征值来比较
         */
        override fun areContentsTheSame(oldItem: DoneBean, newItem: DoneBean): Boolean {
            return oldItem.activityId == newItem.activityId && oldItem.activityCreator==newItem.activityCreator && oldItem.activityPhone==newItem.activityPhone
        }

    }


}
