package com.mredrock.cyxbs.qa.utils

/**
 * @date 2021-03-30
 * @author Sca RayleighZ
 * Describe: 字符串中出现的连续回车（超过两行）或者大量空白（180个空格或者等价的tab）替换为两个回车
 */
fun cutEnterAndBlank(content: String): String{
    val pattern = "\\n{3,}|[ ]{160,}|\n{3,}".toRegex()
    return content.replace("\\t".toRegex(), "    ").replace(pattern, "\n\n\n")
}