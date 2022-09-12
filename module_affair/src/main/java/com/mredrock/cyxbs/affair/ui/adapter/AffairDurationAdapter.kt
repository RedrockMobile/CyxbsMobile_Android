package com.mredrock.cyxbs.affair.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.DAY_ARRAY
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.WEEK_ARRAY
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairAdapterData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeAdd
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.affair.utils.AffairDataUtils
import com.mredrock.cyxbs.affair.widge.TimeSelectDialog
import com.mredrock.cyxbs.affair.widge.WeekSelectDialog
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/11 15:07
 */
class AffairDurationAdapter(val requireActivity: Context) :
  ListAdapter<AffairAdapterData, AffairDurationAdapter.VHolder>(
    object : DiffUtil.ItemCallback<AffairAdapterData>() {
      override fun areItemsTheSame(
        oldItem: AffairAdapterData,
        newItem: AffairAdapterData
      ): Boolean {
        return if (oldItem::class.hashCode() != newItem::class.hashCode()) {
          false
        } else oldItem.onlyId == newItem.onlyId
      }

      override fun areContentsTheSame(
        oldItem: AffairAdapterData,
        newItem: AffairAdapterData
      ): Boolean {
        return oldItem == newItem
      }

      override fun getChangePayload(
        oldItem: AffairAdapterData,
        newItem: AffairAdapterData
      ): Any {
        return ""
      }
    }
  ) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
    val inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      WeekVHolder::class.hashCode() -> WeekVHolder(
        inflater.inflate(
          R.layout.affair_rv_item_edit_affair_week,
          parent,
          false
        )
      )
      DurationVHolder::class.hashCode() -> DurationVHolder(
        inflater.inflate(
          R.layout.affair_rv_item_edit_affair_duration,
          parent,
          false
        )
      )
      TimeAddVHolder::class.hashCode() -> TimeAddVHolder(
        inflater.inflate(
          R.layout.affair_rv_item_edit_affair_time_add,
          parent,
          false
        )
      )
      else -> error("???")
    }
  }

  override fun onBindViewHolder(holder: VHolder, position: Int) {
    val data = getItem(position)
    val lp = holder.itemView.layoutParams as FlexboxLayoutManager.LayoutParams

    when (holder) {
      is WeekVHolder -> {
        data as AffairWeekData
        holder.tvWeek.text = WEEK_ARRAY[data.week]
      }
      is DurationVHolder -> {
        data as AffairTimeData
        holder.tvTime.text =
          "${DAY_ARRAY[data.weekNum]} ${AffairEditArgs.AffairDurationArgs.LESSON_ARRAY[data.beginLesson]}-${AffairEditArgs.AffairDurationArgs.LESSON_ARRAY[data.beginLesson + data.period]}"
        lp.isWrapBefore = data.isWrapBefore
        Log.e("TAG", "onBindViewHolder: ${data.isWrapBefore}")
      }
      is TimeAddVHolder -> {
        holder.ivAdd.animate().rotation(360f)
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is AffairWeekData -> WeekVHolder::class.hashCode()
      is AffairTimeData -> DurationVHolder::class.hashCode()
      is AffairTimeAdd -> TimeAddVHolder::class.hashCode()
    }
  }

  sealed class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  inner class WeekVHolder(itemView: View) : VHolder(itemView) {
    val tvWeek: TextView = itemView.findViewById(R.id.affair_tv_add_affair_week)

    init {
      tvWeek.setOnSingleClickListener {
        var tmp = currentList.toMutableList()
        val dialog =
          WeekSelectDialog(requireActivity, AffairDataUtils.getAffairWeekData(tmp)) { week ->
            // 删除旧的,添加新的
            tmp = tmp.filter { it !is AffairWeekData }.toMutableList()
            tmp.addAll(week)
            submitList(AffairDataUtils.getNewList(tmp))
          }
        dialog.show()
      }
    }
  }

  inner class DurationVHolder(itemView: View) : VHolder(itemView) {
    val tvTime: TextView = itemView.findViewById(R.id.affair_tv_find_course_history_name)
    val ivDelete: ImageView = itemView.findViewById(R.id.affair_ib_find_course_history_delete)

    init {
      ivDelete.setOnSingleClickListener {
        val data = getItem(layoutPosition) as AffairTimeData
        val tmp = currentList.filter { it != data }
        submitList(AffairDataUtils.getNewList(tmp))
      }
    }
  }

  inner class TimeAddVHolder(itemView: View) : VHolder(itemView) {
    val ivAdd: ImageView = itemView.findViewById(R.id.affair_iv_add_time)

    init {
      ivAdd.setOnSingleClickListener {
        val dialog = TimeSelectDialog(requireActivity) { timeData ->
          if (AffairDataUtils.funCheckTime(currentList, timeData)) {
            submitList(AffairDataUtils.addNewTime(currentList, timeData))
            true
          } else
            false
        }
        dialog.show()
      }
    }
  }
}