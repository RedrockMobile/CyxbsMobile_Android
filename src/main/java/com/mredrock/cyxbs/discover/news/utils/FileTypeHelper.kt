package com.mredrock.cyxbs.discover.news.utils

import java.io.File

/**
 * Author: Hosigus
 * Date: 2018/9/27 17:56
 * Description: 打开对应文件需要的type
 */
object FileTypeHelper {
    private val MIME_TABLE = mapOf(".doc" to "application/msword",
            ".docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            ".xls" to "application/vnd.ms-excel",
            ".xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            ".pdf" to "application/pdf",
            ".pps" to "application/vnd.ms-powerpoint",
            ".ppt" to "application/vnd.ms-powerpoint",
            ".pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            ".z" to "application/x-compress",
            ".zip" to "application/x-zip-compressed")

    /**
     * 获取文件类型
     */
    fun getMIMEType(file: File): String {
        var type = "*/*"
        val fName = file.name

        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }

        val end = fName.substring(dotIndex, fName.length).toLowerCase()
        if (end.isBlank()) return type

        type = MIME_TABLE[end] ?: return type
        return type
    }
}