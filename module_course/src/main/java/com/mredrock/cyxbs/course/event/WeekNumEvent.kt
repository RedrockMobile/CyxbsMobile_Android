package com.mredrock.cyxbs.course.event

/**
 * 表示当前显示的课表是第几周的。此事件由CourseContainerFragment发出在main模块下进行接收。进行ToolBar周数显示
 * 的修改。
 *
 * @param weekString 当前是第几周。其中0表示整学期
 * @param isOthers 是否是别人课表，防止事件更新到主页面
 *
 * Created by anriku on 2018/8/18.
 */
data class WeekNumEvent(val weekString: String, val isOthers: Boolean = false)