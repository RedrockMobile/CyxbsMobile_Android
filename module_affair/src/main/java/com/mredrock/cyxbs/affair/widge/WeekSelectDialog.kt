package com.mredrock.cyxbs.affair.widge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.adapter.AffairWeekAdapter
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairWeekSelectData
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
class WeekSelectDialog(
  context: Context,
  weekList: List<Int>,
  addWeek: (list: List<AffairWeekData>) -> Unit
) : RedRockBottomSheetDialog(context) {
  var view: View = LayoutInflater.from(context).inflate(R.layout.affair_dialog_week_select, null)
  val weekRecyclerView: RecyclerView = view.findViewById(R.id.affair_rv_week_select)
  val tvSure: TextView = view.findViewById(R.id.affair_tv_sure)
  val mRvDurationAdapter = AffairWeekAdapter()

  init {
    weekRecyclerView.adapter = mRvDurationAdapter
    weekRecyclerView.layoutManager =
      FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
    val list = mutableListOf<AffairWeekSelectData>()
    for (i in 0..22) {
      val isChoice = weekList.contains(i)
      list.add(AffairWeekSelectData(i, isChoice))
    }
    mRvDurationAdapter.submitList(list)

    tvSure.setOnSingleClickListener {
      val tmp = mRvDurationAdapter.currentList.filter { it.isChoice }
      if (tmp.isEmpty()) {
        "掌友,请至少选择一个周数哦".toast()
      } else {
        val tmp2 = tmp.map { data -> AffairWeekData(data.week, listOf()) }
        addWeek(tmp2)
        dismiss()
      }
    }
    setContentView(view)
  }

  fun getList() = mRvDurationAdapter.currentList

}