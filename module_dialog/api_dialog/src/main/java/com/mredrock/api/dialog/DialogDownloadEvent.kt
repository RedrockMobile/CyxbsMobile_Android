package com.mredrock.api.dialog

/**
 * com.mredrock.api.dialog.DialogDownloadEvent
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/8/2 21:00
 */
data class DialogDownloadEvent(
    val url: String,
    val fileName: String
)