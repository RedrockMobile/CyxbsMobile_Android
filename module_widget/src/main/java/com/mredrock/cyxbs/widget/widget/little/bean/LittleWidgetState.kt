package com.mredrock.cyxbs.widget.widget.little.bean

import com.mredrock.cyxbs.widget.util.getWeekDayChineseName
import java.util.*

/**
 *@author ZhiQiang Tu
 *@time 2022/4/17  20:19
 *@signature 我将追寻并获取我想要的答案
 */
data class LittleWidgetState(
    val title: String = getWeekDayChineseName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)),
    val courseName: String = "今天没有课~",
    val classRoomNum: String = "",
)
private val empty = LittleWidgetState()
fun emptyLittleWidgetState() = empty