package com.mredrock.cyxbs.noclass.util

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description: EditText的输入格式判断
 *
 */
object InputFormatUtil {
    fun isNoInput(s: String): Boolean {
        return s.trim().isEmpty()
    }

    /**
     * 检查一个字符串是否包含标点符号
     * @param s 待检查字符串
     * @return true表示s包含至少一个标点符号
     */
    fun isIncludePunctuate(s: String): Boolean {
        val punctuateRegex = Regex("\\p{P}")
        val normalSymbol =
            Regex("[`~!@#$^&*()=|{}':;',\\[\\].<>《》/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？+-/ ]")
        return punctuateRegex.containsMatchIn(s) && normalSymbol.containsMatchIn(s)
    }

    /**
     * 检查一个字符串是否是数字序列
     * @param s 待检查字符串
     * @return true表示s为一个纯数字序列
     */
    fun isNumbersSequence(s: String): Boolean {
        return s.chars().allMatch { Character.isDigit(it) }
    }

    /**
     * 检查一个字符串是否是纯中文序列
     * @param s 待检查字符串
     * @return true表示s为一个纯中文序列
     */
    fun isChineseCharacters(s: String): Boolean {
        val chineseRegex = Regex("[\\u4e00-\\u9fa5]+")
        return chineseRegex.matches(s)
    }

}