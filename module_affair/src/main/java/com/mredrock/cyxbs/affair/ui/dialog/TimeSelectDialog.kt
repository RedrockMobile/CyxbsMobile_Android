package com.mredrock.cyxbs.affair.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.mredrock.cyxbs.affair.R
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData.Companion.DAY_ARRAY
import com.mredrock.cyxbs.affair.ui.adapter.data.AffairTimeData.Companion.LESSON_ARRAY
import com.mredrock.cyxbs.affair.ui.dialog.base.RedRockBottomSheetDialog
import com.mredrock.cyxbs.api.course.utils.getBeginLesson
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast


/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
class TimeSelectDialog(context: Context, callback: (timeData: AffairTimeData) -> Unit) :
  RedRockBottomSheetDialog(context) {
  var view: View = LayoutInflater.from(context).inflate(R.layout.affair_dialog_time_select, null)
  val weekWV: WheelView = view.findViewById(R.id.affair_wheel_view_week)
  val beginWV: WheelView = view.findViewById(R.id.affair_wheel_view_begin)

  val endWV: WheelView = view.findViewById(R.id.affair_wheel_view_end)
  val tvSure: TextView = view.findViewById(R.id.affair_tv_sure)

  init {
    weekWV.data = DAY_ARRAY.toList()
    beginWV.data = LESSON_ARRAY.toList()
    endWV.data = LESSON_ARRAY.toList()

    tvSure.setOnSingleClickListener {
      if (endWV.currentPosition < beginWV.currentPosition) {
        "非法时间段".toast()
      } else {
        callback.invoke(
          AffairTimeData(
            weekWV.currentPosition,
            getBeginLesson(beginWV.currentPosition),
            endWV.currentPosition - beginWV.currentPosition + 1
          )
        )
        dismiss()
      }
    }
  
    // 在开始时间滚动结束时设置结束时间为对应位置
    beginWV.setOnWheelChangedListener(
      object : OnWheelChangedListener {
        override fun onWheelScrolled(view: WheelView, offset: Int) {
        }
      
        override fun onWheelSelected(view: WheelView, position: Int) {
          if (endWV.currentPosition < position) {
            endWV.smoothScrollTo(position)
          }
        }
      
        override fun onWheelScrollStateChanged(view: WheelView, state: Int) {
        }
      
        override fun onWheelLoopFinished(view: WheelView) {
        }
      }
    )
  
    endWV.setOnWheelChangedListener(
      object : OnWheelChangedListener {
        override fun onWheelScrolled(view: WheelView, offset: Int) {
        }
      
        override fun onWheelSelected(view: WheelView, position: Int) {
          if (position < beginWV.currentPosition) {
            endWV.smoothScrollTo(beginWV.currentPosition)
          }
        }
      
        override fun onWheelScrollStateChanged(view: WheelView, state: Int) {
        }
      
        override fun onWheelLoopFinished(view: WheelView) {
        }
      }
    )
    setContentView(view)
  }
}