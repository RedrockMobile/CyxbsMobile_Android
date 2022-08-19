package com.mredrock.cyxbs.lib.course.internal.view.course

import com.mredrock.cyxbs.lib.course.internal.timeline.ITimeLine
import com.mredrock.cyxbs.lib.course.internal.period.dusk.IFoldDusk
import com.mredrock.cyxbs.lib.course.internal.period.noon.IFoldNoon
import com.mredrock.cyxbs.lib.course.internal.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.internal.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.internal.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.internal.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.internal.period.pm.IPmPeriod
import com.mredrock.cyxbs.lib.course.internal.touch.IMultiTouch
import com.mredrock.cyxbs.lib.course.internal.view.IView
import com.ndhzs.netlayout.child.IChildExistListenerProvider
import com.ndhzs.netlayout.draw.ItemDecorationProvider
import com.ndhzs.netlayout.orientation.IColumn
import com.ndhzs.netlayout.orientation.IRow
import com.ndhzs.netlayout.save.SaveStateProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 12:58
 */
interface ICourseLayout : IView,
  IColumn, IRow,
  ICourseContainer, // 课表 item 容器
  IAmPeriod, // 上午时间段
  INoonPeriod, // 中午时间段
  IPmPeriod, // 下午时间段
  IDuskPeriod, // 傍晚时间段
  INightPeriod, // 晚上时间段
  IFoldNoon, // 折叠中午
  IFoldDusk, // 折叠傍晚
  ITimeLine, // 左侧时间轴
  IMultiTouch, // 多指触摸
  ItemDecorationProvider, // 绘制监听
  SaveStateProvider, // 状态保存
  IChildExistListenerProvider, // 监听子 View 添加和删除
  ICourseScrollControl // 操控课表的滚轴
{
  /**
   * debug 属性，开启后会绘制表格
   */
  var debug: Boolean
}