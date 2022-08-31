package com.mredrock.cyxbs.lib.course.fragment.page.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IWeekWrapper
import com.ndhzs.netlayout.view.NetLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:56
 */
@Suppress("LeakingThis")
abstract class WeekWrapperImpl : NoLessonImpl(), IWeekWrapper {
  
  override val nlWeek           by R.id.course_nl_week.view<NetLayout>()
  override val tvMonth          by R.id.course_tv_month.view<TextView>()
  override val tvMonWeek        by R.id.course_tv_mon_week.view<TextView>()
  override val tvMonMonth       by R.id.course_tv_mon_month.view<TextView>()
  override val tvTueWeek        by R.id.course_tv_tue_week.view<TextView>()
  override val tvTueMonth       by R.id.course_tv_tue_month.view<TextView>()
  override val tvWedWeek        by R.id.course_tv_wed_week.view<TextView>()
  override val tvWedMonth       by R.id.course_tv_wed_month.view<TextView>()
  override val tvThuWeek        by R.id.course_tv_thu_week.view<TextView>()
  override val tvThuMonth       by R.id.course_tv_thu_month.view<TextView>()
  override val tvFriWeek        by R.id.course_tv_fri_week.view<TextView>()
  override val tvFriMonth       by R.id.course_tv_fri_month.view<TextView>()
  override val tvSatWeek        by R.id.course_tv_sat_week.view<TextView>()
  override val tvSatMonth       by R.id.course_tv_sat_month.view<TextView>()
  override val tvSunWeek        by R.id.course_tv_sun_week.view<TextView>()
  override val tvSunMonth       by R.id.course_tv_sun_month.view<TextView>()
  
  override fun forEachWeek(block: (week: TextView, month: TextView) -> Unit) {
    block.invoke(tvMonWeek, tvMonMonth)
    block.invoke(tvTueWeek, tvTueMonth)
    block.invoke(tvWedWeek, tvWedMonth)
    block.invoke(tvThuWeek, tvThuMonth)
    block.invoke(tvFriWeek, tvFriMonth)
    block.invoke(tvSatWeek, tvSatMonth)
    block.invoke(tvSunWeek, tvSunMonth)
  }
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    nlWeek.syncColumnWeight(course) // 同步列比重
  }
}