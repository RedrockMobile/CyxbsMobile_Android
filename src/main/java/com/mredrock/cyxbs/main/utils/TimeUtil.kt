package com.mredrock.cyxbs.main.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  Jon
 * @date  2019/12/2 15:47
 * description：
 */
object TimeUtil {
    //时间参数，固定“时”以上
    private val pattern = "yyyy-MM-dd HH:mm"
    private val specificDate = "2019-11-28 "
    private val startingTime = "${specificDate}8:00"
    private val endTime = "${specificDate}22:30"
    private var simpleDateFormat = SimpleDateFormat(pattern)

    //一天的起始时间戳，和结束时间戳
    private val startTimeStamp = simpleDateFormat.parse(startingTime)?.time
    private val endTimeStamp = simpleDateFormat.parse(endTime)?.time

    //当前时间，占今天上课时间总体的百分比
    private var percentage: Float = 0f


    /**
     * 获取当前时间戳
     */
    private fun getNowTime(): Long? = simpleDateFormat.parse(
            "${specificDate}${simpleDateFormat.format(Date()).substringAfter(" ")}"
    )?.time

    /**
     * 更新百分比
     */
    fun getPercentage():Float{
        val now = getNowTime()
        if (now != null && startTimeStamp != null && endTimeStamp != null) {
             percentage = if (now > this.startTimeStamp && now < this.endTimeStamp) {
                ((now - startTimeStamp).toFloat() / (endTimeStamp - startTimeStamp).toFloat())
            } else {
                0f
            }
        }
        return percentage
    }
}