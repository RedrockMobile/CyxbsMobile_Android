package com.mredrock.cyxbs.discover.map.bean

import java.io.Serializable

/**
 * 添加icon的数据类，与MapLayout搭配使用
 */
data class IconBean(
        val id: Int,
        val sx: Float,
        val sy: Float,
        val leftX: Float,
        val rightX: Float,
        val bottomY: Float,
        val topY: Float,
        val tagLeftX: Float,
        val tagRightX: Float,
        val tagBottomY: Float,
        val tagTopY: Float
) : Serializable
