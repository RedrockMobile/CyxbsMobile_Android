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
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/22 14:49
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
class SearchRvAdapter :
    ListAdapter<ItemActivityBean.ItemAll, SearchRvAdapter.RvSearchActViewHolder>((RvSearchDiffCallback())) {


    /**
     * 点击活动的回调
     */
    private var mActivityClick: ((Int) -> Unit)? = null

    fun setOnActivityClick(listener: (Int) -> Unit) {
        mActivityClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSearchActViewHolder {
        return RvSearchActViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ufield_item_rv_search, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvSearchActViewHolder, position: Int) {
        val itemData = getItem(position)
        holder.bind(itemData)
    }


    inner class RvSearchActViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actPic: ImageView = itemView.findViewById(R.id.uField_search_act_image)
        private val actName: TextView =
            itemView.findViewById<TextView?>(R.id.Ufield_search_act_ame).apply {
                //视觉不让开启跑马灯
//                isSelected = true
            }
        private val actHint: TextView =
            itemView.findViewById<TextView?>(R.id.Ufield_search_act_what)
                .apply {
//                    isSelected = true
                }
        private val actIsGoing: ImageView = itemView.findViewById(R.id.uField_search_isGoing)
        private val actTime: TextView = itemView.findViewById(R.id.uField_search_ddl)

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
            actName.text = itemData.activity_title
            actHint.text = itemData.activity_detail
            actTime.text = timeFormat(itemData.activity_start_at)
            actPic.setImageFromUrl(itemData.activity_cover_url)
            when (itemData.ended) {
                false -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_on)
                else -> actIsGoing.setImageResource(R.drawable.ufield_ic_activity_off)
            }

//            /**
//             * 满足中心比例的缩放
//             */
//            Glide.with(itemView.context)
//                .load(itemData.activity_cover_url)
//                .centerCrop() //中心比例的缩放（如果效果不稳定请删除）
//                .transform(CenterCrop(), RoundedCorners(8))//设置圆角
//                .into(actPic)

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


    class RvSearchDiffCallback : DiffUtil.ItemCallback<ItemActivityBean.ItemAll>() {
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