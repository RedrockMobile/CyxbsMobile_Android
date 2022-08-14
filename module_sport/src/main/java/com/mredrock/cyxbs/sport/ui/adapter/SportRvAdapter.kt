package com.mredrock.cyxbs.sport.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.sport.R
import com.mredrock.cyxbs.sport.databinding.SportItemRvDetailListBinding
import com.mredrock.cyxbs.sport.model.SportDetailItemData

/**
 * @author : why
 * @time   : 2022/8/4 16:18
 * @bless  : God bless my code
 */
/**
 * 体育打卡详细界面下面的RecyclerView的Adapter
 */
class SportRvAdapter : ListAdapter<SportDetailItemData, SportRvAdapter.VH>(
    object : DiffUtil.ItemCallback<SportDetailItemData>() {
        override fun areItemsTheSame(
            oldItem: SportDetailItemData,
            newItem: SportDetailItemData
        ): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: SportDetailItemData,
            newItem: SportDetailItemData
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class VH(val binding: SportItemRvDetailListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //懒加载获取是否有效的两个图标，便于后续加载
        val valid by lazy(LazyThreadSafetyMode.NONE) {
            AppCompatResources.getDrawable(itemView.context, R.drawable.sport_ic_valid)
        }
        val notValid by lazy {
            AppCompatResources.getDrawable(itemView.context, R.drawable.sport_ic_not_valid)
        }

        //懒加载获取奖励的图标，便于后续加载
        val award by lazy {
            AppCompatResources.getDrawable(itemView.context, R.drawable.sport_ic_award)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            SportItemRvDetailListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.run {
            sportDetailItemTvDate.text = item.date      //日期
            //是否有效 分别设置图片
            if (item.valid) {
                sportDetailItemIvValid.setImageDrawable(holder.valid)
            } else {
                sportDetailItemIvValid.setImageDrawable(holder.notValid)
            }
            //若计入奖励则加载并展示奖励图标,否则隐藏
            if (item.isAward) {
                sportDetailItemIvAward.setImageDrawable(holder.award)
                sportDetailItemIvAward.visible()
            } else {
                sportDetailItemIvAward.gone()
            }
            sportDetailItemTvTime.text = item.time      //打卡开始的时间
            sportDetailItemTvSpot.text = item.spot      //打卡的地点
            /**
             * 类型
             */
            when (item.type) {
                "跑步" -> {
                    sportDetailItemTvType.text = "跑步"
                    sportDetailItemTvType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.sport_ic_other,
                        0, 0, 0
                    )
                }
                "其他" -> {
                    sportDetailItemTvType.text = "其他"
                    sportDetailItemTvType.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.sport_ic_other,
                        0, 0, 0
                    )
                }
            }
        }
    }
}
