package com.mredrock.cyxbs.course.page.course.item.view

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:44
 */
interface IOverlapTag {
  /**
   * 设置是否显示课表右上角重叠的小标志
   *
   * 可以使用 [OverlapTagHelper] 来实现该标志
   */
  fun setIsShowOverlapTag(isShow: Boolean)
}