package com.mredrock.cyxbs.common.utils

import java.util.regex.Pattern

/**
 * 此类用于对教室进行解析。
 * 这里有个奇葩的问题搞了好久，需要做一下记录在Android里面使用\w会匹配到汉字！！！但在Java中就只会匹配字母、数字和_
 * 具体的原因还不太清楚。我想应该是编码的问题
 *
 * Created by anriku on 2018/10/17.
 */
object ClassRoomParse {

    // group(1)匹配到的是非字母和数字；group(2)匹配到的是数字、字母以及/。在group(1)中不能把/添加，这里需要匹配
    // 的/是夹在数字中间的/。
    private const val mRegex = "([^[a-zA-Z0-9]]*)([a-zA-Z0-9|/]*)"
    private val mPattern = Pattern.compile(mRegex)

    /**
     * @param classRoom 原始的教室字符串
     */
    fun parseClassRoom(classRoom: String): String {
        // 短教室直接返回
        if (classRoom.length <= 7) {
            return classRoom
        }

        val matcher = mPattern.matcher(classRoom)
        val parseStrings = mutableListOf<String>()

        // 找到字符串中所有的长度大于3的连续数字或字符。
        while (matcher.find()) {
            if (matcher.group(2).length > 3) {
                parseStrings.add(matcher.group(2))
            }
        }
        // 如果没有前面没有匹配到就返回原始字符串；如果匹配到了就进行链接操作。
        return if (parseStrings.isEmpty()) {
            classRoom
        } else {
            val linkStringSb = StringBuilder()
            for ((index, str) in parseStrings.withIndex()) {
                linkStringSb.append(str)
                if (index < (parseStrings.size - 1)) {
                    linkStringSb.append("/")
                }
            }
            linkStringSb.toString()
        }
    }
}