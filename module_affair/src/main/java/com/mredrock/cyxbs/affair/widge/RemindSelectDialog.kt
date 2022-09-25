package com.mredrock.cyxbs.affair.widge

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/14
 * description:
 */
class RemindSelectDialog(context: Context, callback: (text: String, minute: Int) -> Unit) :
  RedRockBottomSheetDialog(context) {
  
  companion object {
    private val REMIND_ARRAY = arrayOf("不提醒", "提前5分钟", "提前10分钟", "提前20分钟", "提前30分钟", "提前1小时")
    
    fun getTextByMinute(minute: Int): String {
      return when (minute) {
        0 -> "不提醒"
        5 -> "提前5分钟"
        10 -> "提前10分钟"
        20 -> "提前20分钟"
        30 -> "提前30分钟"
        60 -> "提前1小时"
        else -> "不提醒"
      }
    }
  }
  
  var view: View = LayoutInflater.from(context).inflate(R.layout.affair_dialog_remind_select, null)
  private val weekWV: WheelView = view.findViewById(R.id.affair_wheel_view_remind)
  val tvSure: TextView = view.findViewById(R.id.affair_tv_sure)

  init {
    weekWV.data =REMIND_ARRAY.toList()

    tvSure.setOnSingleClickListener {
      val position = weekWV.currentPosition
      val minute = getMinute(position)
      callback(getTextByMinute(minute), minute)
      dismiss()
    }
    setContentView(view)
  }
  
  /**
   * 得到对应分钟数
   */
  private fun getMinute(position: Int): Int {
    return when (position) {
      0 -> 0
      1 -> 5
      2 -> 10
      3 -> 20
      4 -> 30
      5 -> 60
      else -> 0
    }
  }
}