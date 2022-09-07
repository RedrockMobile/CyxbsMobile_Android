package com.mredrock.cyxbs.course2.page.affair.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/11 15:07
 */
class AffairDurationAdapter :
  ListAdapter<AffairEditArgs.AffairDurationArgs, AffairDurationAdapter.VHolder>(
    object : DiffUtil.ItemCallback<AffairEditArgs.AffairDurationArgs>() {
      override fun areItemsTheSame(
        oldItem: AffairEditArgs.AffairDurationArgs,
        newItem: AffairEditArgs.AffairDurationArgs
      ): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(
        oldItem: AffairEditArgs.AffairDurationArgs,
        newItem: AffairEditArgs.AffairDurationArgs
      ): Boolean {
        return oldItem == newItem
      }

      override fun getChangePayload(
        oldItem: AffairEditArgs.AffairDurationArgs,
        newItem: AffairEditArgs.AffairDurationArgs
      ): Any {
        return ""
      }
    }
  ) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
    throw RuntimeException()
  }

  override fun onBindViewHolder(holder: VHolder, position: Int) {
  }

  override fun getItemViewType(position: Int): Int {
    return 0
  }

  sealed class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  class WeekVHolder(itemView: View) : VHolder(itemView)

  class DurationVHolder(itemView: View) : VHolder(itemView)
}