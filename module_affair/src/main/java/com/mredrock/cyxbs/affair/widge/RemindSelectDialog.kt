package com.mredrock.cyxbs.affair.widge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs
import com.mredrock.cyxbs.affair.model.data.AffairEditArgs.AffairDurationArgs.Companion.REMIND_ARRAY
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/14
 * description:
 */
class RemindSelectDialog(context: Context, addRemind: (Int) -> Unit) :
  RedRockBottomSheetDialog(context) {
  var view: View = LayoutInflater.from(context).inflate(R.layout.affair_dialog_remind_select, null)
  private val weekWV: WheelView = view.findViewById(R.id.affair_wheel_view_remind)
  val tvSure: TextView = view.findViewById(R.id.affair_tv_sure)

  init {
    weekWV.data =REMIND_ARRAY.toList()

    tvSure.setOnSingleClickListener {
      addRemind(weekWV.currentPosition)
      dismiss()
    }
    setContentView(view)
  }
}