package com.mredrock.cyxbs.lib.course.internal.view.course

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem
import com.mredrock.cyxbs.lib.course.internal.item.IItemContainer
import com.mredrock.cyxbs.lib.course.internal.touch.IMultiTouch
import com.mredrock.cyxbs.lib.course.internal.view.IViewGroup
import com.mredrock.cyxbs.lib.course.internal.view.course.lp.ItemLayoutParams
import com.ndhzs.netlayout.child.ChildExistListenerContainer
import com.ndhzs.netlayout.draw.ItemDecorationContainer
import com.ndhzs.netlayout.orientation.IColumn
import com.ndhzs.netlayout.orientation.IRow
import com.ndhzs.netlayout.save.SaveStateListenerContainer
import com.ndhzs.netlayout.transition.ChildVisibleListenerContainer
import com.ndhzs.netlayout.view.NetLayout

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
interface ICourseViewGroup : IViewGroup,
  IColumn, IRow,
  ItemDecorationContainer, // 绘制监听
  SaveStateListenerContainer, // 状态保存
  ChildExistListenerContainer, // 监听子 View 添加和删除
  ChildVisibleListenerContainer, // 监听课表控件添加和移除，这个支持子 View visible 的监听
  IItemContainer, // IItem 容器
  IMultiTouch, // 多指触摸
  ICourseScrollControl // 操控课表的滚轴
{
  /**
   * debug 属性，开启后会绘制表格
   */
  var debug: Boolean
  
  /**
   * 设置子 View 添加进来时的顺序
   */
  fun setCompareLayoutParams(compare: Comparator<ItemLayoutParams>)
  
  /**
   * 倒序查找子 View (该方法由 [NetLayout] 实现)
   *
   * 倒序的原因是因为一般排在后面的显示在最上面
   *
   * @see findViewUnderByRowColumn
   */
  fun findViewUnderByXY(x: Int, y: Int): View?
  
  /**
   * 根据行和列倒序查找子 View (该方法由 [NetLayout] 实现)
   * @see findViewUnderByXY
   */
  fun findViewUnderByRowColumn(row: Int, column: Int): View?
  
  /**
   * 单独添加一个 view，而不是以 [IItem] 的形式加入 (使用 [IItem] 的形式加入，可以方便统一管理)
   */
  fun addView(view: View, lp: ItemLayoutParams)
}