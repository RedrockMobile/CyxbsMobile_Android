package com.mredrock.cyxbs.lib.course.fragment.page.expose

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/4 13:51
 */
interface IToday {
  
  /**
   * 显示正处于今天的淡蓝色背景
   * @param weekNum 星期数，星期一为 1
   */
  fun showToday(weekNum: Int)
}