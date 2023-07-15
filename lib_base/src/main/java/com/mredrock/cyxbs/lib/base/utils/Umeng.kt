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
  
  // 注册成功会返回 deviceId，是推送消息的唯一标志
  var deviceId: String? = null
  
  // 发送自定义事件
  fun sendEvent(event: Event) {
    if (event.map != null) {
      MobclickAgent.onEventObject(appContext, event.eventId, event.map)
    } else {
      MobclickAgent.onEvent(appContext, event.eventId)
    }
  }
  /**
   * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
   * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
   * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
   * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
   * stackoverflow上的回答：
   * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
   */
  // Umeng 的自定义事件类
  // 记得写好注释！！！
  open class Event(val eventId: String, val map: Map<String, Any>?) {
    
    // 课表显示
    object CourseShow : Event("course_show", null)
    
    // 点击课表item查看课程详情
    data class CourseDetail(
      val isHead: Boolean // 是否来自主页课表头部的点击
    ) : Event("click_course_item", mapOf("is_head" to isHead))
    
    // 底部tab点击
    data class ClickBottomTab(val tabIndex: Int) :
      Event("bottom_tab_click", mapOf("tab_index" to tabIndex))
  }
}