package com.mredrock.cyxbs.lib.course.fragment.page.expose

import android.widget.TextView
import com.ndhzs.netlayout.view.NetLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:51
 */
interface IWeekWrapper {
  
  val nlWeek: NetLayout
  val tvMonth: TextView
  val tvMonWeek: TextView
  val tvMonMonth: TextView
  val tvTueWeek: TextView
  val tvTueMonth: TextView
  val tvWedWeek: TextView
  val tvWedMonth: TextView
  val tvThuWeek: TextView
  val tvThuMonth: TextView
  val tvFriWeek: TextView
  val tvFriMonth: TextView
  val tvSatWeek: TextView
  val tvSatMonth: TextView
  val tvSunWeek: TextView
  val tvSunMonth: TextView
  
  fun forEachWeek(block: (week: TextView, month: TextView) -> Unit)
}