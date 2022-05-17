package com.redrock.module_notification.bean

/**
 * Author by OkAndGreat
 * Date on 2022/5/7 19:52.
 * 多选删除时保留
 */
data class SelectedItem(
    val ids:ArrayList<String>,
    val position:ArrayList<Int>,
    val reads:ArrayList<Boolean>
)
