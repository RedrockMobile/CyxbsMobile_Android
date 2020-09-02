package com.mredrock.cyxbs.common.event

/**
 * @author Jovines
 * @create 2020-02-10 1:55 PM
 * @param isUserSee 为true就显示加载动画，否则不
 * 描述:
 *   用来通知课表加载子页的
 */
data class LoadCourse(val isUserSee: Boolean = true)