package com.mredrock.cyxbs.lib.course.fragment.page

import com.mredrock.cyxbs.lib.course.fragment.course.ICourseBase
import com.mredrock.cyxbs.lib.course.fragment.page.expose.ICourseDefaultTouch
import com.mredrock.cyxbs.lib.course.fragment.page.expose.INoLesson
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IToday
import com.mredrock.cyxbs.lib.course.fragment.page.expose.IWeekWrapper

/**
 * page 包下主要放需要跟除课表容器外的其他控件进行交互的实现类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 17:57
 */
interface ICoursePage :
  ICourseBase, // course 包
  ICourseDefaultTouch, // 包含默认的触摸事件
  INoLesson, // 没课时显示的图片
  IToday, // 显示今天是周几的逻辑
  IWeekWrapper // 包含课表上方的周视图
{
}
