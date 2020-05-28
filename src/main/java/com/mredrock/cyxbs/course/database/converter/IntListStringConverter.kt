package com.mredrock.cyxbs.course.database.converter

import androidx.room.TypeConverter

/**
 * Created by anriku on 2018/8/17.
 */

class IntListStringConverter {

    @TypeConverter
    fun intListToString(intList: List<Int>?): String? = intList?.let {
        val builder = StringBuilder()
        for (i in it.indices) {
            builder.append(intList[i])
            builder.append("-")
        }
        builder.toString()
    }

    @TypeConverter
    fun stringToIntList(string: String?): List<Int>? = string?.let {
        val strs = it.trim().trim('-').split("-")
        val intList = ArrayList<Int>()
        if(strs.size==1&&strs[0].isEmpty()) return@let intList//防止无元素却添加一个空字符串元素
        for (str in strs) {
            intList.add(str.toInt())
        }
        intList
    }

}