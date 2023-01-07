package com.mredrock.cyxbs.lib.base.utils

import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.umeng.analytics.MobclickAgent

/**
 * 该类用于给其他模块提供 Umeng 相关功能
 *
 * @author 985892345
 * 2023/1/6 13:48
 */
object Umeng {
  
  // 发送自定义事件
  fun sendEvent(event: Event) {
    if (event.map != null) {
      MobclickAgent.onEventObject(appContext, event.eventId, event.map)
    } else {
      MobclickAgent.onEvent(appContext, event.eventId)
    }
  }
  
  // Umeng 的自定义事件类
  // 记得写好注释！！！
  sealed class Event(val eventId: String, val map: Map<String, Any>?) {
    
    // 课表显示
    object CourseShow : Event("course_show", null)
    
    // 点击课表item查看课程详情
    data class ClickCourseItem(
      val isHead: Boolean // 是否来自主页课表头部的点击
    ) : Event("click_course_item", mapOf("is_head" to isHead))
    
    // 底部tab点击
    data class ClickBottomTab(val tabIndex: Int) :
      Event("bottom_tab_click", mapOf("tab_index" to tabIndex))
  }
}