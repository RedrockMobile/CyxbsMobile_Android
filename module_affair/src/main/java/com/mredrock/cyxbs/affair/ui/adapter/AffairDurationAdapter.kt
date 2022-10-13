package com.mredrock.cyxbs.affair.ui.adapter

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
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairAdapterData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeAdd
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.affair.ui.dialog.TimeSelectDialog
import com.mredrock.cyxbs.affair.ui.dialog.WeekSelectDialog
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 *
 * 这个 Adapter 里面包括了周数和时间段，使用 FlexboxLayoutManager 自带的换行功能来实现的
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/6/11 15:07
 */
class AffairDurationAdapter :
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
  
  override fun submitList(list: List<AffairAdapterData>?) {
    if (list != null) {
      super.submitList(list.sortList())
    } else {
      super.submitList(list)
    }
  }
  
  override fun submitList(list: List<AffairAdapterData>?, commitCallback: Runnable?) {
    if (list != null) {
      super.submitList(list.sortList(), commitCallback)
    } else {
      super.submitList(list, commitCallback)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
    val inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      WeekDataVHolder::class.hashCode() -> WeekDataVHolder(
        inflater.inflate(
          R.layout.affair_rv_item_edit_affair_week,
          parent,
          false
        )
      )
      TimeDataVHolder::class.hashCode() -> TimeDataVHolder(
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
      is WeekDataVHolder -> {
        data as AffairWeekData
        holder.tvWeek.text = data.getWeekStr()
      }
      is TimeDataVHolder -> {
        data as AffairTimeData
        holder.tvTime.text = data.getTimeStr()
        lp.isWrapBefore = data.isWrapBefore
      }
      is TimeAddVHolder -> {
        holder.ivAdd.animate().rotation(360f)
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is AffairWeekData -> WeekDataVHolder::class.hashCode()
      is AffairTimeData -> TimeDataVHolder::class.hashCode()
      is AffairTimeAdd -> TimeAddVHolder::class.hashCode()
    }
  }

  sealed class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

  inner class WeekDataVHolder(itemView: View) : VHolder(itemView) {
    val tvWeek: TextView = itemView.findViewById(R.id.affair_tv_add_affair_week)

    init {
      tvWeek.setOnSingleClickListener { view ->
        val dialog =
          WeekSelectDialog(
            view.context,
            currentList.mapNotNull { if (it is AffairWeekData) it.week else null } // 获取有哪几周
          ) { week ->
            // 删除旧的,添加新的
            val newList = arrayListOf<AffairAdapterData>()
            newList.addAll(week)
            newList.addAll(currentList.filter { it !is AffairWeekData })
            submitList(newList)
          }
        dialog.show()
      }
    }
  }

  inner class TimeDataVHolder(itemView: View) : VHolder(itemView) {
    val tvTime: TextView = itemView.findViewById(R.id.affair_tv_find_course_history_name)
    val ivDelete: ImageView = itemView.findViewById(R.id.affair_ib_find_course_history_delete)

    init {
      ivDelete.setOnSingleClickListener {
        val data = getItem(layoutPosition) as AffairTimeData
        val a = currentList.filterIsInstance<AffairTimeData>().isEmpty()
        if (a) {
          "掌友，请至少保留1个时间呦".toast()
        } else {
          val tmp = currentList.filter { it !== data }
          submitList(tmp)
        }
      }
    }
  }

  inner class TimeAddVHolder(itemView: View) : VHolder(itemView) {
    val ivAdd: ImageView = itemView.findViewById(R.id.affair_iv_add_time)

    init {
      ivAdd.setOnSingleClickListener {
        val dialog = TimeSelectDialog(it.context) { timeData ->
          val newList = currentList.toMutableList()
          newList.add(timeData)
          submitList(newList)
        }
        dialog.show()
      }
    }
  }
  
  
  
  
  
  /**
   * Time的第一个要换行,建议所有的submitList都用这个方法处理一下
   */
  private fun List<AffairAdapterData>.sortList(): List<AffairAdapterData> {
    val list = sort(this)
    val newList = arrayListOf<AffairAdapterData>()
    var index = 0
    while (index < list.size) {
      val prev = newList.lastOrNull()
      val next = list.getOrNull(index + 1)
      when (val now = list[index]) {
        is AffairWeekData -> {
          newList.add(now)
        }
        is AffairTimeData -> {
          if (prev is AffairWeekData) {
            // 这个时候 isWrapBefore 该为 true
            newList.add(if (now.isWrapBefore) now else now.copy(isWrapBefore = true))
          } else {
            // 这个时候 isWrapBefore 该为 false
            newList.add(if (!now.isWrapBefore) now else now.copy(isWrapBefore = false))
          }
        }
        is AffairTimeAdd -> {
        }
      }
      index++
    }
    // 最后一个是加号
    newList.add(AffairTimeAdd(1))
    return newList
  }
  
  // 将数据按照先AffairWeekData后AffairTimeData排序
  private fun sort(list: List<AffairAdapterData>): List<AffairAdapterData> {
    val newList = arrayListOf<AffairAdapterData>()
    val weekList = arrayListOf<AffairWeekData>()
    val timeList = arrayListOf<AffairTimeData>()
    list.forEach {
      if (it is AffairWeekData) {
        weekList.add(it)
      } else if (it is AffairTimeData) {
        timeList.add(it)
      }
    }
    newList.addAll(weekList)
    newList.addAll(timeList)
    return newList
  }
}