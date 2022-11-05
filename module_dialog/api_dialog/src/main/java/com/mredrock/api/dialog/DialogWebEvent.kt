package com.mredrock.api.dialog

/**
 * com.mredrock.api.dialog.DialogWebEvent.kt
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/11/5 下午4:29
 */
sealed interface DialogWebEvent {
    data class Custom(val command: String) : DialogWebEvent
    data class Download(val url: String, val fileName: String) : DialogWebEvent
    data class SavePic(val url: String) : DialogWebEvent
}