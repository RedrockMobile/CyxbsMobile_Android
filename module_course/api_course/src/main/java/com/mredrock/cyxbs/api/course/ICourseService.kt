package com.mredrock.cyxbs.api.course

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/9 16:39
 */
interface ICourseService : IProvider {
  
  /**
   * 显示主页的课表
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   */
  fun replaceHomeFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int
  )

  /**
   * 只有一个人时的学生课表
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   * @param id 想要替换的 layoutId
   * @param arg 加载课表所必须的参数
   */
  fun replaceStuCourseFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int,
    arg: ICourseArgs
  )

  /**
   * 只有一个人时的老师课表
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   * @param id 想要替换的 layoutId
   * @param arg 加载课表所必须的参数
   */
  fun replaceTeaCourseFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int,
    arg: ICourseArgs
  )

  /**
   * 多个人时的学生课表
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   * @param id 想要替换的 layoutId
   * @param args 加载课表所必须的参数
   */
  fun replaceStuCourseFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int,
    args: List<ICourseArgs>
  )

  /**
   * 打开课表所需要的最少参数
   */
  interface ICourseArgs {
    /**
     * 学号或者老师工号
     */
    val num: String

    companion object {
      /**
       * 重写 invoke 方法，使你能够直接像使用构造器一样 new 出一个对象
       */
      operator fun invoke(num: String): ICourseArgs {
        return object : ICourseArgs {
          override val num: String = num
        }
      }
    }
  }
}