package com.mredrock.cyxbs.lib.course.view

import com.mredrock.cyxbs.lib.course.column.ITimeLine
import com.mredrock.cyxbs.lib.course.period.dusk.IFoldDusk
import com.mredrock.cyxbs.lib.course.period.noon.IFoldNoon
import com.mredrock.cyxbs.lib.course.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.period.pm.IPmPeriod

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 12:58
 */
interface ICourseLayout : IView,
  ICourseContainer,
  IAmPeriod,
  INoonPeriod,
  IPmPeriod,
  IDuskPeriod,
  INightPeriod,
  IFoldNoon,
  IFoldDusk,
  ITimeLine {
  
  fun getLessonStartHeight(num: Int): Int
  
  fun getLessonEndHeight(num: Int): Int
  
  fun getNoonStartHeight(): Int
  
  fun getNoonEndHeight(): Int
  
  fun getDuskStartHeight(): Int
  
  fun getDuskEndHeight(): Int
}