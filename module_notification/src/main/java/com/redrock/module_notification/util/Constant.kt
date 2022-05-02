package com.redrock.module_notification.util

/**
 * Author by OkAndGreat
 * Date on 2022/4/26 11:36.
 *
 */
object Constant {
    //消息设置界面switch1是否选中的key
    const val IS_SWITCH1_SELECT = "setting_switch1_state"

    //消息设置界面switch2是否选中的key
    const val IS_SWITCH2_SELECT = "setting_switch2_state"

    //消息模块的sp file name
    const val NOTIFICATION_SP_FILE_NAME = "notification_module_sp_file"

    //Notification模块logTag
    const val NOTIFICATION_LOG_TAG = "module_notification_tag"

    //通知主页界面LoadMoreWindow是否应该显示红点（表示设置页面用户是否点进去过，若还没有就显示小红点）
    const val HAS_USER_ENTER_SETTING_PAGE = "has_user_enter_setting_page"
}