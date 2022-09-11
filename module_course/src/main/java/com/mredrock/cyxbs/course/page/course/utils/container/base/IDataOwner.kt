package com.mredrock.cyxbs.course.page.course.utils.container.base

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/11 17:32
 */
interface IDataOwner<T : Any> {
  val data: T
  fun setData(newData: T)
}