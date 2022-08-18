package com.mredrock.cyxbs.lib.course.view.course

import android.view.View
import com.mredrock.cyxbs.lib.course.column.ITimeLine
import com.mredrock.cyxbs.lib.course.period.dusk.IFoldDusk
import com.mredrock.cyxbs.lib.course.period.noon.IFoldNoon
import com.mredrock.cyxbs.lib.course.period.am.IAmPeriod
import com.mredrock.cyxbs.lib.course.period.dusk.IDuskPeriod
import com.mredrock.cyxbs.lib.course.period.night.INightPeriod
import com.mredrock.cyxbs.lib.course.period.noon.INoonPeriod
import com.mredrock.cyxbs.lib.course.period.pm.IPmPeriod
import com.mredrock.cyxbs.lib.course.touch.IMultiTouch
import com.mredrock.cyxbs.lib.course.view.IView
import com.ndhzs.netlayout.child.IChildListenerProvider
import com.ndhzs.netlayout.draw.ItemDecorationProvider
import com.ndhzs.netlayout.save.SaveStateProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 12:58
 */
interface ICourseLayout : IView,
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
  IChildListenerProvider, // 监听子 View 添加和删除
  ICourseScrollControl // 操控课表的滚轴
{
  
  fun findItemUnderByXY(x: Int, y: Int): View?
  
  fun getLessonStartHeight(num: Int): Int
  
  fun getLessonEndHeight(num: Int): Int
  
  fun getNoonStartHeight(): Int
  
  fun getNoonEndHeight(): Int
  
  fun getDuskStartHeight(): Int
  
  fun getDuskEndHeight(): Int
}