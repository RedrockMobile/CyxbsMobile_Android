package com.mredrock.cyxbs.affair.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.WEEK_ARRAY
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekSelectData
import com.mredrock.cyxbs.affair.utils.AffairDataUtils
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
class AffairWeekAdapter : ListAdapter<AffairWeekSelectData, AffairWeekAdapter.VHolder>(
  object : DiffUtil.ItemCallback<AffairWeekSelectData>() {
    override fun areItemsTheSame(
      oldItem: AffairWeekSelectData,
      newItem: AffairWeekSelectData
    ): Boolean {
      return oldItem.week == newItem.week
    }

    override fun areContentsTheSame(
      oldItem: AffairWeekSelectData,
      newItem: AffairWeekSelectData
    ): Boolean {
      return oldItem.isChoice != newItem.isChoice
    }

    override fun getChangePayload(
      oldItem: AffairWeekSelectData,
      newItem: AffairWeekSelectData
    ): Any {
      return ""
    }
  }
) {


  inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.affair_tv_add_affair_week)

    init {
      textView.setOnSingleClickListener {
        val data = getItem(layoutPosition)
        data.isChoice = !data.isChoice
        data.toString().toast()
        submitList(AffairDataUtils.checkWeekSelectData(currentList))
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
    val view = LayoutInflater
      .from(parent.context).inflate(R.layout.affair_rv_item_edit_affair_week, parent, false)
    return VHolder(view)
  }

  override fun onBindViewHolder(holder: VHolder, position: Int) {
    holder.textView.text = WEEK_ARRAY[getItem(position).week]
    if (getItem(position).isChoice) {
      holder.textView.setTextColor(appContext.getColor(R.color.affair_edit_affair_select_week_tv))
    } else {
      Log.e("TAG", "onBindViewHolder: hhhh")
      holder.textView.setTextColor(appContext.getColor(com.mredrock.cyxbs.common.R.color.common_level_two_font_color))
    }
  }
}