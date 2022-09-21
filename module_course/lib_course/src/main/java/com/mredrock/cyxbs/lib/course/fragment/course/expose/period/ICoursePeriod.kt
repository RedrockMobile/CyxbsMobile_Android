package com.mredrock.cyxbs.lib.course.fragment.course.expose.period

import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.fragment.course.expose.period.pm.IPmPeriod

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:52
 */
interface ICoursePeriod : IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  /**
   * 得到某节课开始前的高度值
   */
  fun getLessonStartHeight(num: Int): Int
  
  /**
   * 得到某节课结束时的高度值
   */
  fun getLessonEndHeight(num: Int): Int
}