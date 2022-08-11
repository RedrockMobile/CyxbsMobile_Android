package com.mredrock.cyxbs.api.widget.bean

import java.io.Serializable

/**
 * author : Watermelon02
 */
data class Affair(
    val stuNum: String = "",
    val id: Int = 0,
    val time: Int = 0,
    val title: String = "",
    val content: String = "",
    val week: Int = 0,
    val beginLesson: Int = 0,
    val day: Int = 0,
    val period: Int = 0,
) : Serializable