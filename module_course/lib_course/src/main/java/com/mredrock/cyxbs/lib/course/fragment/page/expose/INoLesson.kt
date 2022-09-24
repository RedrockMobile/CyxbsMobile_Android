package com.mredrock.cyxbs.lib.course.fragment.page.expose

import android.view.View
import com.mredrock.cyxbs.lib.course.internal.item.IItem

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:58
 */
interface INoLesson {
  
  /**
   * 没有课时展示的 View
   *
   * 应该不需要额外操作，所以只需要 View 就可以了
   */
  val viewNoLesson: View
  
  /**
   * 是否是用于展示的 item，会影响没课图片的显示
   * @return 返回 true，则表示该 [item] 是用于展示的
   */
  fun isExhibitionItem(item: IItem): Boolean
}