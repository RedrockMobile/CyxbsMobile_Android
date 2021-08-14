package com.mredrock.cyxbs.course.database.converter

import androidx.room.TypeConverter

/**
 * @author Jovines
 * @create 2020-02-11 5:55 PM
 *
 * 描述:
 *   用于转换老师所教班级的room转换器
 *   【另外，有一说一，别通宵写代码，这下看了都怕这个时间】
 */
class ClassListStringConverter {


    @TypeConverter
    fun strListToString(intList: List<String>?): String? = intList?.let {
        val builder = StringBuilder()
        for (i in it.indices) {
            builder.append(intList[i])
            builder.append("-")
        }
        builder.toString()
    }

    @TypeConverter
    fun stringToStrList(string: String?): List<String>? = string?.let {
        val strs = it.trim().trim('-').split("-")
        val intList = ArrayList<String>()
        if(strs.size==1&&strs[0].isEmpty()) return@let intList//防止无元素却添加一个空字符串元素
        for (str in strs) {
            intList.add(str)
        }
        intList
    }

}