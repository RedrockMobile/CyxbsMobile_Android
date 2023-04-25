package com.mredrock.cyxbs.course.page.course.item.affair

import android.view.View
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.lib.course.fragment.page.ICoursePage

/**
 * 负责事务底层数据更新的管理类
 *
 * ## 规定
 * - [onStart] 和 [onEnd] 必须一对一调用
 * - [onChange] 必须在 [onStart] 与 [onEnd] 间调用，在没有发生修改时也可以不调用
 * - [onChange] 建议只调用一次，因为内部会更新数据库也可能会发送网络数据
 * - 目前只有单一的事务才允许修改，请使用 [isSingleAffair] 判断，如果不判断，则 [onChange] 不会进行真正的数据更新
 * - 必须遵守以上规定，不然将出现数据更新失败
 *
 * @author 985892345
 * 2023/2/23 12:53
 */
interface IAffairManager {
  
  /**
   * 是否是单一事务
   *
   * 由于目前事务唯一 id 与时间段不是一对一的关系，存在相同 id 的事务连续出现几周，
   * 这种情况目前还不知道怎么具体设计，所以就只允许事务唯一 id 与时间段是一对一关系的单一事务才允许改变
   */
  fun isSingleAffair(data: AffairData): Boolean
  
  /**
   * 事件的开始
   *
   * 事件开始后将暂时取消课表数据的观察流，并增加计数器值
   */
  fun onStart(
    page: ICoursePage,
    affair: AffairItem,
    child: View,
  )
  
  /**
   * 通知数据发生改变
   *
   * - 调用后将更新数据库，所以该方法应该放在事件结束前调用，而不是频繁修改
   * - 由于课表采用差分刷新，为了在重新开启数据观察流时不闪动，内部将修改差分比对的旧数据集
   *
   * @return 返回改变是否成功,只有在是单一事务时才能成功修改，如果没有成功修改，请调用方自己还原 ui 状态
   */
  fun onChange(
    page: ICoursePage,
    affair: AffairItem,
    child: View,
    oldData: AffairData,
    newData: AffairData,
  ): Boolean
  
  /**
   * 事件的结束
   *
   * 事件结束后将减少计数器值，如果计数器 = 0，则重新开启课表数据观察流
   */
  fun onEnd(
    page: ICoursePage,
    affair: AffairItem,
    child: View,
  )
}