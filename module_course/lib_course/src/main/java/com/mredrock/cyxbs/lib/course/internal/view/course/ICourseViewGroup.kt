package com.mredrock.cyxbs.lib.course.internal.view.course

import com.mredrock.cyxbs.lib.course.internal.affair.IAffairContainer
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.lesson.ILessonContainer
import com.mredrock.cyxbs.lib.course.internal.touch.IMultiTouch
import com.mredrock.cyxbs.lib.course.internal.view.IView
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.ndhzs.netlayout.child.IChildExistListenerProvider
import com.ndhzs.netlayout.draw.ItemDecorationProvider
import com.ndhzs.netlayout.orientation.IColumn
import com.ndhzs.netlayout.orientation.IRow
import com.ndhzs.netlayout.save.SaveStateProvider

/**
 * 课表控件的总接口
 *
 * 但请注意：这只是一个 ViewGroup，缺失很多实际课表的逻辑，如果命名为 CourseLayout，总感觉不合适，
 * 所以改名为 ICourseViewGroup，而很多业务逻辑都移到了 Fragment 层
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 12:58
 */
interface ICourseViewGroup : IView,
  IColumn, IRow,
  IItemContainer, // IItem 容器
  ILessonContainer, // ILessonItem 容器
  IAffairContainer, // IAffairItem 容器
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
  
  fun setCompareLayoutParams(compare: Comparator<ItemLayoutParams>)
}