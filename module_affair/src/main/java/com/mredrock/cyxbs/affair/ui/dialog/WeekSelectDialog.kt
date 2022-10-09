package com.mredrock.cyxbs.affair.ui.dialog

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
import com.mredrock.cyxbs.affair.ui.dialog.base.RedRockBottomSheetDialog
import com.mredrock.cyxbs.api.course.ICourseService
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
    val list = mutableListOf<AffairWeekAdapter.AffairWeekSelectData>()
    for (i in 0 .. ICourseService.maxWeek) {
      val isChoice = weekList.contains(i)
      list.add(AffairWeekAdapter.AffairWeekSelectData(i, isChoice))
    }
    mRvDurationAdapter.submitList(list)

    tvSure.setOnSingleClickListener {
      val tmp = mRvDurationAdapter.currentList.filter { it.isChoice }
      if (tmp.isEmpty()) {
        "掌友，请至少选择一个周数哦".toast()
      } else {
        /*
        * 特别注意：这里面拿到的整学期的 week 为 0，affair 的 api 模块的数据类也同样为 0
        * */
        val tmp2 = tmp.map { data -> AffairWeekData(data.week) }
        addWeek(tmp2)
        dismiss()
      }
    }
    setContentView(view)
  }
}