package com.mredrock.cyxbs.api.widget.bean

import java.io.Serializable

/**
 * author : Watermelon02
 */
data class Affair(
    val stuNum: String="",
    val id: Int=-1,
    val time: Int=-1,
    val title: String="",
    val content: String="",
    val week: Int=-1,
    val beginLesson: Int=-1,
    val day: Int=-1,
    val period: Int=-1,
):Serializable