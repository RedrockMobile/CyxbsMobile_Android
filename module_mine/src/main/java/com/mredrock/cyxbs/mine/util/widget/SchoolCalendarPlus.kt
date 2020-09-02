package com.mredrock.cyxbs.mine.util.widget

import com.mredrock.cyxbs.common.utils.SchoolCalendar
import java.math.BigDecimal
import java.util.*


/**
 * Created by roger on 2019/12/13
 */
class SchoolCalendarPlus : SchoolCalendar() {
    fun getChineseWeek(): String? {
        return digital2Chinese(super.weekOfTerm.toLong())
    }

    //返回一个学年的Pair，比如<2019, 2020>，以八月为分界线
    fun getYearPair(): Pair<Int, Int> {
        val currentYear = super.year
        val currentMonth = super.month
        if (currentMonth < Calendar.AUGUST) {
            return Pair(currentYear - 1, currentYear)
        } else {
            return Pair(currentYear, currentYear + 1)
        }
    }

    private val NUMBER_ZH = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十")
    //最大支持9千兆
    private val NUMBER_UNIT = arrayOf("", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "兆", "十", "百", "千")

    /**
     * @param number
     * @return
     */
    private fun digital2Chinese(number: Long): String? {
        var value = number
        var isNegative = false
        if (value < 0) {
            value = -value
            isNegative = true
        }
        var bigDecimal: BigDecimal = BigDecimal.valueOf(value)
        val valueStr = value.toString()
        val digits = arrayOfNulls<Int>(valueStr.length)
        for (i in valueStr.indices) { //循环数存储每一位数字，从低到高
            digits[i] = bigDecimal.divideAndRemainder(BigDecimal.valueOf(10))[1].toInt() //value.intValue()%10;
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(10))
        }
        val sb = StringBuilder()
        digits.reverse() // 从高到低
        var flush = false
        var needFilling = true
        if (digits.size > 16) {
            throw ArrayIndexOutOfBoundsException("数字太大了，超出汉字可读范围")
        }
        for (i in digits.indices) {
            if (digits[i] == 0) {
                if (needFilling) {
                    if (digits.size - i - 1 == 4) {
                        sb.append("万")
                        needFilling = false
                    }
                    if (digits.size - i - 1 == 8) {
                        sb.append("亿")
                        needFilling = false
                    }
                    if (digits.size - i - 1 == 12) {
                        sb.append("兆")
                        needFilling = false
                    }
                }
                flush = true
                continue
            }
            if (flush) {
                sb.append("零")
                flush = false
            }
            if (digits[i] != 1 || digits.size != 2 || i == 1) {
                digits[i]?.let { sb.append(NUMBER_ZH[it]) }
            }
            sb.append(NUMBER_UNIT[digits.size - i - 1])

            needFilling = !listOf(4, 8, 12).contains(digits.size - i - 1)
        }
        return if (isNegative) {
            "负$sb"
        } else sb.toString()
    }

    //是否是上学期（即秋季学期），否则是下学期（春季学期）
    fun isFirstSemester() : Boolean {
        return firstDay[Calendar.MONTH] > 6
    }
}