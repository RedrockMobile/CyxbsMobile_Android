package com.mredrock.cyxbs.course.page.find.bean

import com.mredrock.cyxbs.api.course.ICourseService

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/8 20:32
 */
sealed interface IFindPerson : ICourseService.ICourseArgs {
  /**
   * 学生或老师名字
   */
  val name: String
  /**
   * 学生或老师的学院
   */
  val content: String
  /**
   * 学生的学号或者是老师的工号
   */
  override val num: String
}