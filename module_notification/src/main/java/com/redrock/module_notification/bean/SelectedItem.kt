package com.redrock.module_notification.bean

/**
 * Author by OkAndGreat
 * Date on 2022/5/7 19:52.
 *
 */
data class SelectedItem(
    val ids:ArrayList<String>,
    val positions:ArrayList<Int>,
    val reads:ArrayList<Boolean>
)
