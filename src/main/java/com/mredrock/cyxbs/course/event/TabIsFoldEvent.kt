package com.mredrock.cyxbs.course.event

/**
 * 表示是否TabLayout折叠。此事件由main模块发出在CourseContainerFragment中进行接收。
 *
 * @param isFold true: 折叠
 * false:不折叠
 *
 * Created by anriku on 2018/8/18.
 */
data class TabIsFoldEvent(val isFold: Boolean)