package com.mredrock.cyxbs.course.database

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
        for (str in strs) {
            intList.add(str.toInt())
        }
        intList
    }

}